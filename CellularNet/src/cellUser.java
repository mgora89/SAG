
public class cellUser {
	//how long will new user remain a new user
	//when reaches zero then user is removed from the vector
	public int newQuantums;
	//user name
	public String uName;
	public cellUser(String nam) {
		uName=nam;
		newQuantums=5;
	}
}
