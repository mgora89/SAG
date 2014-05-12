// Movement record class in monitor
public class MovementRecord {
	public String cellFrom; //from which cell was the movement
	public String cellTo; //destination cell of movement
	public int howManyMoved; //how many people moved so far
	public MovementRecord(String from, String to)
	{
		cellFrom=from;
		cellTo=to;
		howManyMoved=1;
	}
	
}
