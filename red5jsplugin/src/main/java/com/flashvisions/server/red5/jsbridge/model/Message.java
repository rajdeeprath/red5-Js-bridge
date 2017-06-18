package com.flashvisions.server.red5.jsbridge.model;

import java.time.Instant;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;

public class Message implements IMessage {
	
	
	String id;
	
	
	// RMI or EVENT
	BridgeMessageType type;
	
	
	// Data 
	Object data;
	
	
	// timestamp
	long timestamp;
	
	
	
	public Message(){
	
		timestamp = Instant.now().toEpochMilli();
	}
	

	

	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#getId()
	 */
	@Override
	public String getId() {
		return id;
	}



	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}




	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#getType()
	 */
	@Override
	public BridgeMessageType getType() {
		return type;
	}


	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#setType(com.flashvisions.server.red5.jsbridge.model.MessageType)
	 */
	@Override
	public void setType(BridgeMessageType type) {
		this.type = type;
	}


	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#getData()
	 */
	@Override
	public Object getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#setData(java.lang.Object)
	 */
	@Override
	public void setData(Object data) {
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#getTimestamp()
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IMessage#setTimestamp(long)
	 */
	@Override
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
