package com.flashvisions.server.red5.jsbridge.alternate.model;

public class SubscriberStream extends Stream {

	String state;
	
	boolean paused;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	
	
}
