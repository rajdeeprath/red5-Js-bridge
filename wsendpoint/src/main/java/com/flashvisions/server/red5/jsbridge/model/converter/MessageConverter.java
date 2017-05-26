package com.flashvisions.server.red5.jsbridge.model.converter;

import java.time.Instant;
import java.util.UUID;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;
import com.flashvisions.server.red5.jsbridge.model.OutGoingMessage;
import com.google.gson.Gson;

public class MessageConverter {
	
	
	private Gson gson;
	
	
	public MessageConverter()
	{
		gson = new Gson();
	}
	
	
	public String toJson(IMessage message)
	{
		if(message.getId() == null || message.getId().equals("") || message.getId().length() == 0)
		{
			message.setId(UUID.randomUUID().toString().toLowerCase() + "-" + Instant.now().toEpochMilli());
		}
		
		return gson.toJson(message);
	}

}
