package se.z_app.stb.api;

/**
 * A default state for the RCProxy. 
 * @author Linus Back
 *
 */
class DefaultState extends RCProxyState {

	public void up() {
		RemoteControl.instance().sendButton(RemoteControl.Button.UP);
	
	}

	
	public void down() {
		RemoteControl.instance().sendButton(RemoteControl.Button.DOWN);
	
	}

	
	public void right() {
		RemoteControl.instance().sendButton(RemoteControl.Button.RIGHT);
		
	}	

	
	public void left() {
		RemoteControl.instance().sendButton(RemoteControl.Button.LEFT);
		
	}

	
	public void ok() {
		RemoteControl.instance().sendButton(RemoteControl.Button.OK);
		
	}
	
	
	public void back() {
		RemoteControl.instance().sendButton(RemoteControl.Button.BACK);
		
	}
	
	
	public void mute() {
		RemoteControl.instance().sendButton(RemoteControl.Button.MUTE);
			
	}

	
	public void info() {
		RemoteControl.instance().sendButton(RemoteControl.Button.INFO);
		
	}
	
	
	public void menu() {
		RemoteControl.instance().sendButton(RemoteControl.Button.MENU);
	}
	
	public void exit() {
		RemoteControl.instance().sendButton(RemoteControl.Button.EXIT);
	}
}