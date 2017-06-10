package com.flashvisions.server.red5.jsbridge.alternate.model;

import java.util.List;
import java.util.Map;

public class Connection {
	
	
	private String duty;
	
	
	private String encoding;
	
	
	private boolean connected;
	
	
	private Map<String, Object> connectionParams;
	
	
	private String host;
	
	
	private String remoteAddress;
	
	
	private List<String> remoteAddresses;
	
	
	private int remotePort;
	
	
	private String path;
	
	
	private String sessionId;
	
	
	private long readBytes;
	
	
	private long writtenBytes;
	
	
	private long readMessages;
	 
	
	private long writtenMessages;
	
	
	private long droppedMessages;
	
	
	private long pendingMessages;
	
	
	private long lastPingTime;
	
	
	private String protocol;
	
	
	private String className;


	public String getDuty() {
		return duty;
	}


	public void setDuty(String duty) {
		this.duty = duty;
	}


	public String getEncoding() {
		return encoding;
	}


	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}


	public boolean isConnected() {
		return connected;
	}


	public void setConnected(boolean connected) {
		this.connected = connected;
	}


	public Map<String, Object> getConnectionParams() {
		return connectionParams;
	}


	public void setConnectionParams(Map<String, Object> connectionParams) {
		this.connectionParams = connectionParams;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public String getRemoteAddress() {
		return remoteAddress;
	}


	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}


	public List<String> getRemoteAddresses() {
		return remoteAddresses;
	}


	public void setRemoteAddresses(List<String> remoteAddresses) {
		this.remoteAddresses = remoteAddresses;
	}


	public int getRemotePort() {
		return remotePort;
	}


	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


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


	public long getLastPingTime() {
		return lastPingTime;
	}


	public void setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}

}
