package com.flashvisions.server.red5.jsbridge.model;

import com.flashvisions.server.red5.jsbridge.alternate.model.BroadcastStream;
import com.flashvisions.server.red5.jsbridge.alternate.model.Connection;

public class ConnectionStreamEvent {
	
	private Connection connection;
	
	private BroadcastStream stream;
	

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public BroadcastStream getStream() {
		return stream;
	}

	public void setStream(BroadcastStream stream) {
		this.stream = stream;
	}
	
	
}
