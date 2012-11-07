package se.z_app.stb;


import java.util.Date;

public class Program{
	
	private String name;
	private int eventID = -1;
	private Date start;
	private int duration = -1;
	private String shortText;
	private String longText;
	private Channel channel;
	
	public Program(Channel parentChannel){
		this.channel = channel;
	}
	
	public Channel getParentChannel(){
		return channel;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getEventID() {
		return eventID;
	}
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	/**
	 * 
	 * @return  duration of a program given in secound
	 */
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getShortText() {
		return shortText;
	}
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	public String getLongText() {
		return longText;
	}
	public void setLongText(String longText) {
		this.longText = longText;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	
}
