import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class Agent1 extends Agent {
	
	public void setup() {
		System.out.println("Hello. My name is "+getLocalName());
		addBehaviour(new CyclicBehaviour() {
		public void action() {
		ACLMessage msgRx = receive();
		if (msgRx != null) {
		System.out.println(msgRx);
		ACLMessage msgTx = msgRx.createReply();
		msgTx.setContent("Hello!");
		send(msgTx);
		} else {
		block();
		}
		}
		});
		}

}

