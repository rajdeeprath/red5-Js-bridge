package com.flashvisions.server.red5.jsbridge.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.beanutils.MethodUtils;
import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.red5.net.websocket.listener.WebSocketDataListener;
import org.red5.net.websocket.model.MessageType;
import org.red5.net.websocket.model.WSMessage;
import org.red5.server.adapter.IApplication;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.scope.GlobalScope;
import org.red5.server.scope.Scope;
import org.red5.server.scope.WebScope;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.flashvisions.server.red5.jsbridge.exceptions.MessageFormatException;
import com.flashvisions.server.red5.jsbridge.interfaces.IJSBridgeAware;
import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridge;
import com.flashvisions.server.red5.jsbridge.model.EventMessage;
import com.flashvisions.server.red5.jsbridge.model.RMIMessage;
import com.flashvisions.server.red5.jsbridge.model.OutGoingMessage;
import com.flashvisions.server.red5.jsbridge.model.MessageStatus;
import com.flashvisions.server.red5.jsbridge.model.BridgeMessageType;
import com.flashvisions.server.red5.jsbridge.model.annotations.Invocable;
import com.flashvisions.server.red5.jsbridge.model.converter.MessageConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

public class JsBridgeDataListener extends WebSocketDataListener implements IJsBridge, InitializingBean, ApplicationContextAware {

	private static final Logger logger = Red5LoggerFactory.getLogger(JsBridgeDataListener.class, "red5-js-bridge");
	
	private ApplicationContext applicationContext;
	
	private IScope appScope;
	
	private MultiThreadedApplicationAdapter appAdapter;
	
	private ExecutorService threadedExecutor = Executors.newCachedThreadPool();
	
	private ConnectionManager connManager;


    {
        setProtocol("jsbridge");
    }
    
    
    public JsBridgeDataListener()
    {
    	connManager = new ConnectionManager();
    	connManager.initialize();
    }


	
	
	@Override
	public void onWSMessage(WSMessage message) 
	{
		
		final JsBridgeConnection connection = (JsBridgeConnection) message.getConnection().getSession().getAttribute(JsBridgeConnection.TAG);
		
		if (message.getMessageType() == MessageType.PING) 
		{
            logger.debug("Ping received, no processing required");
            return;
        }
		else if (message.getMessageType() == MessageType.PONG) 
		{
			connection.pingAcknowledged();
            return;
        }
		else if (message.getMessageType() != MessageType.CLOSE) 
		{
			
			if (message.getMessageType() == MessageType.TEXT)
			{
				resolveWebsocketMessage(message);
			}
		}
		else
		{
			logger.debug("closing connection {}", message.getConnection());
			connection.close();
		}
				
	}





	@Override
	public void onWSConnect(WebSocketConnection conn) 
	{
		final JsBridgeConnection bridgeConnection = ConnectionManager.createBridgeConnectionObject(conn);
		connManager.addConnection(bridgeConnection);
		
		threadedExecutor.execute(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub				
				OutGoingMessage message = new OutGoingMessage();
				message.setType(BridgeMessageType.EVENT);
				message.setStatus(MessageStatus.DATA);
				message.setData(new EventMessage("session.id", bridgeConnection.getSessionId()));
				connManager.sendToConnection(bridgeConnection.getSignalChannel(), message);
			}
			
		});
	}

	
	
	@Override
	public void onWSDisconnect(WebSocketConnection conn) 
	{
		JsBridgeConnection bridgeConnection = ConnectionManager.getConnection(conn);
		connManager.removeConnection(bridgeConnection);	
	}

	
	
	@Override
	public void stop() 
	{
		this.threadedExecutor.shutdown();
		this.connManager.shutdown();
	}

	
	
	@Override
	public void pushMessage(Object data) throws Exception {
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.PUSH);
		message.setStatus(MessageStatus.DATA);
		message.setData(data);
		connManager.sendToAll(message);
	}
	
	
	
	
	
	@Override
	public void pushMessage(IConnection conn, Object data) throws Exception {
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.PUSH);
		message.setStatus(MessageStatus.DATA);
		message.setData(data);
		sendToIP(conn.getRemoteAddress(), message);
	}

	
	
	

	@Override
	public void broadcastEvent(String event, Object data) 
	{
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage(event, data)); // some how pack =>  close as special event
		connManager.sendToAll(message);
	}
	
	
	
	
	
	@Override
	public void broadcastApplicationEvent(String event, Object data) 
	{
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage(event, data)); // some how pack =>  close as special event
		connManager.sendApplicationEvents(message);
	}

	
	
	
	
	@Override
	public void close(String reason) 
	{
		// notify closing to client
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage("session.closing", reason)); // some how pack =>  close as special event
		connManager.sendToAll(message);
		
		
		// close all connections
		
		connManager.closeAllConnections();		
	}

	
	
	@Override
	public void close() 
	{
		// notify closing to client
		
		OutGoingMessage message = new OutGoingMessage();
		message.setType(BridgeMessageType.EVENT);
		message.setStatus(MessageStatus.DATA);
		message.setData(new EventMessage("session.closing", null)); // some how pack =>  close as special event
		connManager.sendToAll(message);
		
		
		// close all connections
		
		connManager.closeAllConnections();
	}	
	
	
	
	
	private void sendToIP(String ip, OutGoingMessage message) 
	{
		connManager.sendToIP(ip, message);		
	}
	
	
	
	
	@Override
	public int getTotalConnection() 
	{
		return connManager.getTotalConnection();
	}

	
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)	throws BeansException {
		this.applicationContext = applicationContext;
	}

	
	
	
	@Override
	public void afterPropertiesSet() throws Exception 
	{
		 if (applicationContext.containsBean("web.scope")) 
		 {
	            appScope = (WebScope) applicationContext.getBean("web.scope");
	            logger.debug("Linked to app scope: {}", appScope);
	            appAdapter = (MultiThreadedApplicationAdapter) applicationContext.getBean("web.handler");
	            appAdapter.addListener(appListener);
	            logger.debug("Linked to app: {}", appAdapter);
	     } 
		 else 
	     {
	            GlobalScope globalScope = (GlobalScope) applicationContext.getBean("global.scope");
	            if (globalScope == null) {
	                globalScope = new GlobalScope();
	            }
	            appScope = new Scope.Builder(globalScope, ScopeType.APPLICATION, "webrtc", false).build();
	            logger.debug("Built app scope {}", appScope);
	     }
		 
		 
		 if(appAdapter != null)
		 {
			 setBridgeOnApplication();
		 }
	}




	private void setBridgeOnApplication() 
	{
		if(appAdapter != null && appAdapter instanceof IJSBridgeAware) 
		{
			IJSBridgeAware ref = (IJSBridgeAware) appAdapter;
			ref.setRed5JSBridge(this);
		 }
	}



	private void resolveWebsocketMessage(WSMessage message) 
	{
		RMIMessage request = null;
		OutGoingMessage response = null;
		Exception exception = null;
		
		try 
		{
			request = getMessageFromPayload(message);
			
			Object instance = appAdapter;
			
			
			String methodName = request.getMethod();
			Object[] args = sanitize((ArrayList<?>) request.getData());
			
			int arguments = args.length;
			Class[] parameterTypes = new Class[arguments];
			    for (int i = 0; i < arguments; i++) {
			    parameterTypes[i] = args[i].getClass();
			}
			
			Method method = MethodUtils.getMatchingAccessibleMethod(appAdapter.getClass(), methodName, parameterTypes);
			
			if(method == null) {
				throw new NoSuchMethodException("No such invocable method '" + methodName + "' found in Application Adapter");
			}
			else if(!method.isAnnotationPresent(Invocable.class)){
				throw new SecurityException("Method found is not invocable");
			}
			
			
			response = new OutGoingMessage();
			response.setId(request.getId());
			response.setType(request.getType());
			response.setStatus(MessageStatus.DATA);
			
			if(method.getReturnType() == void.class || method.getReturnType() == Void.TYPE)
			{
				method.invoke(appAdapter, args);
			}
			else
			{
				Object result = method.invoke(appAdapter, args);
				response.setData(result);
			}
			
			
			connManager.sendToConnection(message.getConnection(), response);
		}
		catch (IllegalAccessException e) 
		{
			exception = e;
			logger.error("Error " + e.getMessage());
		} 
		catch (InvocationTargetException | NoSuchMethodException e)
		{
			exception = e;
			logger.error("Error " + e.getMessage());
		}
		catch (MessageFormatException e)
		{
			exception = e;
			logger.error("Error " + e.getMessage());
		}
		catch (Exception e) 
		{
			exception = e;
			logger.error("Error " + e.getMessage());
		}
		finally
		{
			if(exception != null)
			{
				response = new OutGoingMessage();
				response.setId(request.getId());
				response.setType(request.getType());
				response.setStatus(MessageStatus.ERROR);
				response.setData(exception.getMessage());
				
				connManager.sendToConnection(message.getConnection(), response);

			}
		}
		
	}



	private Object[] sanitize(ArrayList<?> arguments)
	{		
		// substitute nulls
		ArrayList<Object> sanitizedParameters = new ArrayList<Object>();
		
		for(Object argument : arguments)
		{			
			
			// null check
			if(argument == null || String.valueOf(argument) == null || String.valueOf(argument).equalsIgnoreCase("undefined") || String.valueOf(argument).equalsIgnoreCase(null))
			{
				sanitizedParameters.add(null);
				continue;
			}
			
			
			String klassName = argument.getClass().getSimpleName();
			
			if(klassName.equals("LinkedTreeMap"))
			{
				// find more info
				LinkedTreeMap<?, String> map = (LinkedTreeMap<?, String>) argument;
				Object data = map.get("value");
				String type = map.get("type");
				
				
				if(type.equals("String"))
				{
					String param = String.valueOf(data);
					sanitizedParameters.add(param);
				}
				else if(type.equals("Boolean"))
				{
					Boolean param = Boolean.valueOf(String.valueOf(data));;
					sanitizedParameters.add(param);
				}
				else if(type.equals("Number"))
				{
					// guess what it is
					sanitizedParameters.add(smartRecognizeNumeric(argument));
				}
				else if(type.equals("Integer"))
				{
					Double tmp = Double.parseDouble(String.valueOf(data));
					Integer param = tmp.intValue();
					sanitizedParameters.add(param);
				}
				else if(type.equals("Long"))
				{
					Double tmp = Double.parseDouble(String.valueOf(data));
					Long param = tmp.longValue();
					sanitizedParameters.add(param);
				}
				else if(type.equals("Float"))
				{
					Float param = Float.parseFloat(String.valueOf(data));
					sanitizedParameters.add(param);
				}
				else if(type.equals("Double"))
				{
					Double param = Double.parseDouble(String.valueOf(data));
					sanitizedParameters.add(param);
				}
				else if(type.equals("JsonObject"))
				{
					JsonObject param = new JsonParser().parse(data.toString()).getAsJsonObject();
					sanitizedParameters.add(param);
				}
				else
				{
					throw new InvalidParameterException("Unrecognized parameter type");
				}
			}
			else if(klassName.equals("String"))
			{
				sanitizedParameters.add(String.valueOf(argument));
			}
			else if(klassName.equals("Boolean"))
			{
				sanitizedParameters.add(Boolean.valueOf(String.valueOf(argument)));
			}
			else if(klassName.equals("Number"))
			{
				// guess what it is
				sanitizedParameters.add(smartRecognizeNumeric(argument));
			}
			else if(klassName.equals("Float"))
			{
				sanitizedParameters.add(Float.parseFloat(String.valueOf(argument)));
			}
			else if(klassName.equals("Double"))
			{
				sanitizedParameters.add(Double.parseDouble(String.valueOf(argument)));
			}
			else if(klassName.equals("Integer"))
			{
				Double tmp = Double.parseDouble(String.valueOf(argument));
				sanitizedParameters.add(tmp.intValue());
			}
			else if(klassName.equals("Long"))
			{
				Double tmp = Double.parseDouble(String.valueOf(argument));
				sanitizedParameters.add(tmp.longValue());
			}
			else
			{
				throw new InvalidParameterException("Unrecognized parameter type");
			}
		}
		
		return sanitizedParameters.toArray();
	}
	
	
	
	
	
	private Object smartRecognizeNumeric(Object argument) 
	{
		String parameterString = String.valueOf(argument);
		
		if(parameterString.contains("."))
		{
			try
			{
				Float  param = Float.parseFloat(parameterString);
				
				if(String.valueOf(param).length() != parameterString.length())
				throw new Exception("Number is not fit to be called a Float");
				
				return param;
			}
			catch(Exception fe)
			{
				try
				{
					Double param = Double.parseDouble(parameterString);
					return param;
				}
				catch(Exception le)
				{
					throw new IllegalArgumentException();
				}
			}
		}
		else
		{
			try
			{
				Double tmp = Double.parseDouble(parameterString);
				Integer  param = tmp.intValue();
				
				if(String.valueOf(param).length() != parameterString.length())
				throw new Exception("Number is not fit to be called an Integer");					
				
				return param;
			}
			catch(Exception ie)
			{
				try
				{
					Double tmp = Double.parseDouble(parameterString);
					Long param = tmp.longValue();
					return param;
				}
				catch(Exception le)
				{
					throw new IllegalArgumentException();
				}
				
			}
		}
	}




	private RMIMessage getMessageFromPayload(WSMessage message) throws MessageFormatException
	{
		MessageConverter messageConverter;
		String path = null;
		
		try
		{
			JsonParser p =new JsonParser();
			String msgAsString = message.getMessageAsString();
			
			JsonElement msg = p.parse(msgAsString);
			JsonObject json = msg.getAsJsonObject();
			
			messageConverter = new MessageConverter();
			
			if(json.has("type")) // proper message ?
			{
				RMIMessage request = (RMIMessage) messageConverter.fromJson(json);
				return request;
			}
			else
			{
				throw new MessageFormatException("Unexpected data format. Missing fields detected in" + json.toString());
			}
		}
		catch(Exception me)
		{
			throw new MessageFormatException("Invalid message format.Cause " + me.getMessage());
		}
	}
	
	
	
	
	private IApplication appListener = new IApplication(){
		

		@Override
		public boolean appStart(IScope app) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.appStart", null);
			return true;
		}

		@Override
		public boolean appConnect(IConnection conn, Object[] params) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.appConnect", null);
			return true;
		}

		@Override
		public boolean appJoin(IClient client, IScope app) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.appJoin", null);
			return true;
		}

		@Override
		public void appDisconnect(IConnection conn) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.appDisconnect", null);
			
		}

		@Override
		public void appLeave(IClient client, IScope app) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.appLeave", null);
		}

		@Override
		public void appStop(IScope app) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.appStop", null);
		}

		@Override
		public boolean roomStart(IScope room) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.roomStart", null);
			return true;
		}

		@Override
		public boolean roomConnect(IConnection conn, Object[] params) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.roomConnect", null);
			return true;
		}

		@Override
		public boolean roomJoin(IClient client, IScope room) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.roomJoin", null);
			return true;
		}

		@Override
		public void roomDisconnect(IConnection conn) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.roomDisconnect", null);
		}

		@Override
		public void roomLeave(IClient client, IScope room) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.roomLeave", null);
		}

		@Override
		public void roomStop(IScope room) {
			// TODO Auto-generated method stub
			broadcastApplicationEvent("application.roomStop", null);
		}
		
	};
}
