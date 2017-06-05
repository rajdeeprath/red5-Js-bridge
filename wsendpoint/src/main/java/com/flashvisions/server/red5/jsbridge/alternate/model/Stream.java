package com.flashvisions.server.red5.jsbridge.alternate.model;

public class Stream {
	
	String streamId;
	
	String name;
	
	long creationTime;
	
	long startTime;
	
	String scopePath;
	
	String broadcastStreamPublishName;

	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getScopePath() {
		return scopePath;
	}

	public void setScopePath(String scopePath) {
		this.scopePath = scopePath;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public String getBroadcastStreamPublishName() {
		return broadcastStreamPublishName;
	}

	public void setBroadcastStreamPublishName(String broadcastStreamPublishName) {
		this.broadcastStreamPublishName = broadcastStreamPublishName;
	}
	
	
	

}
