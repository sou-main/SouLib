package soulib.net;

public class SimpleEvent implements Event{

	private final String Return;
	public SimpleEvent(String r){
		Return=r;
	}
	@Override
	public String run(){
		return Return;
	}

}
