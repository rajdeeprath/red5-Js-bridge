package com.flashvisions.server.red5.jsbridge.model;

import java.time.Instant;

public class Message {
	
	// RMI or EVENT
	MessageType type;
	
	
	// error or data
	MessageStatus status;
	
	
	// Data 
	Object data;
	
	
	// timestamp
	long timestamp;
	
	
	
	public Message(){
	
		timestamp = Instant.now().toEpochMilli();
	}
	

	

	public MessageType getType() {
		return type;
	}




	public void setType(MessageType type) {
		this.type = type;
	}


	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}




	public MessageStatus getStatus() {
		return status;
	}




	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	
	
}
