

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BookBuyerAgent extends Agent {
	// ��Ҫ�����Ŀ
	private String targetBookTitle;
	// ��֪�����б�
	private AID[] sellerAgents;

	// agent��ʼ��
	protected void setup() {
		// ��ӡ�ʺ���
		System.out.println("Hallo! Buyer-agent	" + getAID().getName()
				+ " is ready.");
		// ��ȡ��������,�Ա�õ�Ҫ�����Ŀ��
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			targetBookTitle = (String) args[0];
			System.out.println("Trying to buy " + targetBookTitle);
			// ���һ��TickerBehaviour����ÿ���ӵ���һ��������Agent���͵�����
			addBehaviour(new TickerBehaviour(this, 6000) {
				protected void onTick() {
					System.out.println("Trying to buy " + targetBookTitle);
					// �������������б�
					// ������������Ҫ�ķ�������
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("book-selling");
					template.addServices(sd);
					// ������������Ҫ�ķ���
					try {
						DFAgentDescription[] result = DFService.search(myAgent,
								template);
						// ��������Ҫ���������
						System.out
								.println("Found the following seller agents:");
						// �������������б�
						sellerAgents = new AID[result.length];
						// ��ӡ�������б�
						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName());
						}
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}
					// ���������巢������,ͨ�����RequestPerformer��Ϊ
					myAgent.addBehaviour(new RequestPerformer());
				}
			});
		} else {
			// ����Agent
			System.out.println("No target book title specified");
			doDelete();
		}
	}

	// ע��:myAgent��������Behaviour���ж����,��ָ������ִ�����
	// ��Ϊ��Agent.��ʵ��RequestPerformer��Ϊ�Ǹ���������Agent��
	// �͹�������,�⽫

	// ��Agent����������������
	protected void takeDown() {
		// ��ӡ������Ϣ
		System.out
				.println("Buyer-agent " + getAID().getName() + "terminating.");
	}

	/**
	 * �ڲ���RequestPerformer. �����Ϊ����Agent����������Agent���������.
	 */
	private class RequestPerformer extends Behaviour {
		private AID bestSeller = null; // �ṩ����ѡ��(����������)������Agent
		private int bestPrice; // ��͵ļ۸�
		private int repliesCnt = 0; // ������Agent�յ��Ļ�Ӧ��Ϣ��
		private MessageTemplate mt; // ������Ϣ��ģ��
		private int step = 0; // ����һ��״̬����,�������ȡ��ͬ�Ĳ���

		public void action() {
			switch (step) {
			case 0:
				System.out.println(getAID().getName() + " step 0");

				// �����е�����Agent����CFP��Ϣ
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				}
				cfp.setContent(targetBookTitle);
				cfp.setConversationId("book-trade");
				cfp.setReplyWith("cfp" + System.currentTimeMillis());
				// Unique value
				myAgent.send(cfp);
				// ������Ϣ��ģ��,�Ա��յ�PROPOSE���͵���Ϣ
				mt = MessageTemplate.and(MessageTemplate
						.MatchConversationId("book-trade"), MessageTemplate
						.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;

			case 1:
				System.out.println(getAID().getName() + " step 1");

				// �������е�PROPOSE��REFUSDER���͵���Ϣ
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					repliesCnt++;
					// �յ���Ӧ��Ϣ
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// System.out.println(getAID().getName()+" get proposse! ");
						// //******************
						// ����һ����Ӧ��
						int price = Integer.parseInt(reply.getContent());
						if (bestSeller == null || price < bestPrice) {
							bestPrice = price;
							bestSeller = reply.getSender();
						}
						// ����ת��Ϊ����
						if (bestSeller != null
								&& repliesCnt == sellerAgents.length) {
							// �Ѿ��յ����л�Ӧ��Ϣ
							step = 2;
						}
					} else {
						System.out.println(getAID().getName() + " block! "); // ******************
						block();
					}
				}
				break;

			case 2:
				System.out.println(getAID().getName() + " step 2");

				// ���������˵������߷��͹�������
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				// ����ȷ��ֵ�����Ϣ�ĸ�����
				order.addReceiver(bestSeller);
				order.setContent(targetBookTitle);
				order.setConversationId("book-trade");
				order.setReplyWith("order" + System.currentTimeMillis());
				myAgent.send(order);
				// ������ʵ���Ϣģ���������ܺ��ʵĻ�Ӧ��Ϣ
				mt = MessageTemplate.and(MessageTemplate
						.MatchConversationId("book-trade"), MessageTemplate
						.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;

			case 3:
				System.out.println(getAID().getName() + " step 3");

				// ������������Ϣ�Ļ�Ӧ
				reply = myAgent.receive(mt);
				if (reply != null) {
					// �յ���������Ϣ�Ļ�Ӧ
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// �ɹ�����,��ʱ��Agent���Խ�����
						System.out.println(targetBookTitle
								+ " successfullypurchased.");
						System.out.println("Price = " + bestPrice);
						myAgent.doDelete();
					}
					step = 4;
				} else {
					block();
				}
				break;
			}
		}

		public boolean done() {
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	} // ����
}
