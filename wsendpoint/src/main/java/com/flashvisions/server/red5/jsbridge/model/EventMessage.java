package com.flashvisions.server.red5.jsbridge.model;

public class EventMessage {
	
	String name;
	
	Object data;
	
	
	public EventMessage(){
		
	}
	
	
	public EventMessage(String name, Object data){
		this.name = name;
		this.data = data;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
	

}
