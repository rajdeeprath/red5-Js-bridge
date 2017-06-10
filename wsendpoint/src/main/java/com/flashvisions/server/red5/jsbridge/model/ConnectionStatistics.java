package com.flashvisions.server.red5.jsbridge.model;

public class ConnectionStatistics {

	
	private long readBytes;
	
	
	private long writtenBytes;
	
	
	private long readMessages;
	 
	
	private long writtenMessages;
	
	
	private long droppedMessages;
	
	
	private long pendingMessages;
	
	
	
	
	public long getReadBytes() {
		return readBytes;
	}


	public void setReadBytes(long readBytes) {
		this.readBytes = readBytes;
	}


	public long getWrittenBytes() {
		return writtenBytes;
	}


	public void setWrittenBytes(long writtenBytes) {
		this.writtenBytes = writtenBytes;
	}


	public long getReadMessages() {
		return readMessages;
	}


	public void setReadMessages(long readMessages) {
		this.readMessages = readMessages;
	}


	public long getWrittenMessages() {
		return writtenMessages;
	}


	public void setWrittenMessages(long writtenMessages) {
		this.writtenMessages = writtenMessages;
	}


	public long getDroppedMessages() {
		return droppedMessages;
	}


	public void setDroppedMessages(long droppedMessages) {
		this.droppedMessages = droppedMessages;
	}


	public long getPendingMessages() {
		return pendingMessages;
	}


	public void setPendingMessages(long pendingMessages) {
		this.pendingMessages = pendingMessages;
	}
}
