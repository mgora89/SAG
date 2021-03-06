
public class CellularNode {

	private String name; // node name
	private String userCount; // number of users inside node
	private String usersMoved; // users moved in last quantum
	
	public CellularNode(String nodeName)
	{
		setName(nodeName);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserCount() {
		return userCount;
	}
	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}
	public String getUsersMoved() {
		return usersMoved;
	}
	public void setUsersMoved(String usersMoved) {
		this.usersMoved = usersMoved;
	}
}
