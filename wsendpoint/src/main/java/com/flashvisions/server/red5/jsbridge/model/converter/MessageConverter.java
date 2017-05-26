package com.flashvisions.server.red5.jsbridge.model.converter;

import com.flashvisions.server.red5.jsbridge.model.Message;
import com.google.gson.Gson;

public class MessageConverter {
	
	
	private Gson gson;
	
	
	public MessageConverter()
	{
		gson = new Gson();
	}
	
	
	public String toJson(Message message)
	{
		return gson.toJson(message);
	}

}
