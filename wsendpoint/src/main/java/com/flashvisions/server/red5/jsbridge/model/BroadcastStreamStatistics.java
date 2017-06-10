package com.flashvisions.server.red5.jsbridge.model;

public class BroadcastStreamStatistics {
	
	String publishedName;
	
	int totalSubscribers;
	
	int maxSubscribers;
	
	int activeSubscribers;
	
	long bytesReceived;

	public String getPublishedName() {
		return publishedName;
	}

	public void setPublishedName(String publishedName) {
		this.publishedName = publishedName;
	}

	public int getTotalSubscribers() {
		return totalSubscribers;
	}

	public void setTotalSubscribers(int totalSubscribers) {
		this.totalSubscribers = totalSubscribers;
	}

	public int getMaxSubscribers() {
		return maxSubscribers;
	}

	public void setMaxSubscribers(int maxSubscribers) {
		this.maxSubscribers = maxSubscribers;
	}

	public int getActiveSubscribers() {
		return activeSubscribers;
	}

	public void setActiveSubscribers(int activeSubscribers) {
		this.activeSubscribers = activeSubscribers;
	}

	public long getBytesReceived() {
		return bytesReceived;
	}

	public void setBytesReceived(long bytesReceived) {
		this.bytesReceived = bytesReceived;
	}
	
	
	

}
