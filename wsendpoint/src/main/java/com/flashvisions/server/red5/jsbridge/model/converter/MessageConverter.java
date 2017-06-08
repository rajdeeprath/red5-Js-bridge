package com.flashvisions.server.red5.jsbridge.model.converter;

import java.time.Instant;
import java.util.UUID;

import com.flashvisions.server.red5.jsbridge.interfaces.IMessage;
import com.flashvisions.server.red5.jsbridge.model.APIMessage;
import com.flashvisions.server.red5.jsbridge.model.BridgeMessageType;
import com.flashvisions.server.red5.jsbridge.model.RMIMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
	
	
	
	public IMessage fromJson(JsonObject json)
	{
		return gson.fromJson(json.toString(), RMIMessage.class);
	}
	
	
	
	public IMessage fromJson(JsonObject json, BridgeMessageType type)
	{
		if(type == BridgeMessageType.RMI)
		{
			return gson.fromJson(json.toString(), RMIMessage.class);
		}
		else if(type == BridgeMessageType.API)
		{
			return gson.fromJson(json.toString(), APIMessage.class);
		}
		else
		{
			return null;
		}
		
	}

}
