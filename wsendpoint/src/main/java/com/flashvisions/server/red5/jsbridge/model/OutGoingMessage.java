package com.flashvisions.server.red5.jsbridge.model;


public class OutGoingMessage extends Message {
	
	
	// error or data
	MessageStatus status;
	
	
	
	public OutGoingMessage()
	{
		super();
	}
	
	
	

	public MessageStatus getStatus() {
		return status;
	}




	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	
	
}
