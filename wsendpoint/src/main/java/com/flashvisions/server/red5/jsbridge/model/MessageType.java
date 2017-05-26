package com.flashvisions.server.red5.jsbridge.model;

public enum MessageType {
	
	EVENT("event"),
	RMI("rmi"),
	PUSH_MESSAGE("push_message");
	
	private final String name;    
	
	private MessageType(String s) {
        name = s;
    }
	
	public boolean equalsName(String otherName) { 
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }

}
