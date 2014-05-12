import java.util.Vector;

// Movement record class in monitor
public class MovementRecord {
	public String cellFrom; //from which cell was the movement
	public String cellTo; //destination cell of movement
	public Vector <Integer> movementQuantums; //how many people moved so far
	public MovementRecord(String from, String to)
	{
		cellFrom=from;
		cellTo=to;
		movementQuantums=new Vector<Integer>();
		movementQuantums.add(5);
	}
	

	
}
