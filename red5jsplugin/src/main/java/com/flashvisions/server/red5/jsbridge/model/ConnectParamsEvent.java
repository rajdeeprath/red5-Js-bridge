package com.flashvisions.server.red5.jsbridge.model;

import com.flashvisions.server.red5.jsbridge.alternate.model.Connection;

public class ConnectParamsEvent {
	
	private Connection connection;
	
	private Object[] params;
	

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}
}
