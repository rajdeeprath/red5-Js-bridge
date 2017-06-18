package com.flashvisions.server.red5.jsbridge.model;

import java.util.Map;

import com.flashvisions.server.red5.jsbridge.alternate.model.SharedObject;

public class SharedObjectUpdate {
	
	SharedObject so;
	
	Map<String, Object> data;

	public SharedObject getSo() {
		return so;
	}

	public void setSo(SharedObject so) {
		this.so = so;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	
	
	
}
