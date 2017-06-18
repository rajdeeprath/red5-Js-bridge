package com.flashvisions.server.red5.jsbridge.model;

public enum MessageStatus {
	
	ERROR("error"),
	DATA("data");
	
	private final String name;    
	
	private MessageStatus(String s) {
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
