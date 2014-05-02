
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.*;

public class BookSellerAgent extends Agent {
	// ������Ŀ�б�,��ϣ��,����Ŀӳ�䵽�۸���
	private Hashtable catalogue;
	// ����������û�ͼ�ν���,ͨ�����û���������Ŀ�б�����µ���Ŀ
	private BookSellerGui myGui;
	// �����ʼ��
	protected void setup() {
		// ������Ŀ�б�
		catalogue = new Hashtable();
		// ��������ʾGUI
		myGui = new BookSellerGui(this);
		myGui.show();
		// �ڻ�ҳ��ע���������
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// ��ӷ����������ѯ����Ϊ
		addBehaviour(new OfferRequestsServer());
		// ��Ӵ��������幺���������Ϊ,
		addBehaviour(new PurchaseOrdersServer());
	}

	// ���������
	protected void takeDown() {
		// �ӻ�ҳ��ע��÷���
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// �ر�GUI
		myGui.dispose();
		// ��ӡ�����Ѿ��������Ϣ
		System.out.println("Seller-agent " + getAID().getName()	+ "terminating.");
	}

	/**
	 * ����ĺ��������û���ͨ��GUI�������Ŀ���������ʱ�������
	 */
	public void updateCatalogue(final String title, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(title, new Integer(price));
				System.out.println(title + " inserted into catalogue. Price ="+ price);
			}
		});
	}

	/**
	 * �ڲ���OfferRequestsServer. �����Ϊ�����������������������巢�����Ĳ�ѯ��Ϣ��
	 * ����ѯ����Ŀ�ڱ��صĴ�����Ŀ��,���������Ӧһ��PROPOSE�� Ϣ,�����,����REFUSEIf
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				//System.out.println(getAID().getName()+" get message! ");	 //*****************			

				// ����յ�CFP��Ϣ,����
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				// ��ȡ�۸�
				Integer price = (Integer) catalogue.get(title);
				if (price != null) {
					// ����ѯ����Ŀ�ڴ�����Ŀ��,���ȡ��ļ۸�
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(price.intValue()));
					System.out.println(getAID().getName()+" propose! ");  //******************
				} else {
					// ��ѯ����Ŀ������
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
					System.out.println(getAID().getName()+" refuse! ");  //******************
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	} // OfferRequestsServer����

	/**
	 * �ڲ���PurchaseOrdersServer. ��������ڴ��������巢�����Ĺ�������,��������Ӵ�����Ŀ��
	 * ����ɾ�����Ŀ,���������巢��һ��INFORM��Ϣ,���ڸ����� ����,�����Ѿ��ɹ��Ľ�����.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// �յ�ACCEPT_PROPOSAL��Ϣ,�����д���
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				Integer price = (Integer) catalogue.remove(title);
				if (price != null) {
					reply.setPerformative(ACLMessage.INFORM);
					System.out.println(title + " sold to agent" + msg.getSender().getName());
				} else {
					// �������Ŀͬʱ������ȥ��
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			} else {
				block();
			}
		}
	} // OfferRequestsServer�����
}

