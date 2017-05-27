package com.flashvisions.server.red5.jsbridge.model;

public enum BridgeMessageType {
	
	EVENT("event"),
	RMI("rmi"),
	PUSH("push_message");
	
	private final String name;    
	
	private BridgeMessageType(String s) {
        name = s;
    }
	
	public boolean equalsName(String otherName) { 
        return name.equals(otherName);
    }

    @Override
	public String toString() {
       return this.name;
    }

}
