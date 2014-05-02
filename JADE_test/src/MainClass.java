import java.*;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class MainClass extends Agent{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
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
