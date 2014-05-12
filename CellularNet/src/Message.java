import jade.util.leap.Serializable;


//messages sent between cells or from user manager to cell
public class Message implements Serializable{
	//type of message
	static public enum Type {
	    USERHEARTBEAT, 
	    CHECKUSEREXISTENCE,
	    MOVEMENTRECORD
	};
	public Type messageType; 
	//User with which the message is assosiated
	public String user;
	//receiver cell-to whom sent
	public String receiverCell; //also cellfrom when sent to overseer 
	//sender cell-who sends
	public String senderCell=null; //also cellto when sent to overseer
	//to be removed/discussed
	
	//heartbeatconstructor
	public Message(String usr,String rCell, Type MT)
	{
		messageType=MT;
		user=usr;
		receiverCell=rCell;
	}
	
	//
	public Message(String usr,String rCell, String sCell,Type MT)
	{
		messageType=MT;
		user=usr;
		receiverCell=rCell;
		senderCell=sCell;		
	}
	

	
}
