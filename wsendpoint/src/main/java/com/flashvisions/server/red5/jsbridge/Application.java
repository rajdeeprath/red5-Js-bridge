package com.flashvisions.server.red5.jsbridge;

/*
 * RED5 Open Source Flash Server - http://www.osflash.org/red5
 * 
 * Copyright (c) 2006-2008 by respective authors (see below). All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 2.1 of the License, or (at your option) any later 
 * version. 
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with this library; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */

import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketPlugin;
import org.red5.net.websocket.WebSocketScope;
import org.red5.net.websocket.WebSocketScopeManager;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
//import org.slf4j.Logger;


import org.red5.server.plugin.PluginRegistry;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.flashvisions.server.red5.jsbridge.interfaces.IJSBridgeAware;
import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridge;
import com.flashvisions.server.red5.jsbridge.model.annotations.Invocable;

/**
 * Sample application that uses the client manager.
 * 
 * @author The Red5 Project (red5@osflash.org)
 */
public class Application extends MultiThreadedApplicationAdapter implements IJSBridgeAware, ApplicationContextAware {

	private static Logger log = Red5LoggerFactory.getLogger(Application.class);

	private IJsBridge bridge;
	
	private ApplicationContext applicationContext;
	
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
	
	
	@Override
	public boolean appStart(IScope arg0) 
	{
		WebSocketPlugin wsPlugin = (WebSocketPlugin) PluginRegistry.getPlugin("WebSocketPlugin");
        wsPlugin.setApplication(this);
        WebSocketScopeManager manager = wsPlugin.getManager(arg0);
        WebSocketScope defaultWebSocketScope = (WebSocketScope) applicationContext.getBean("webSocketScopeDefault");
        manager.addWebSocketScope(defaultWebSocketScope);
        
        return super.appStart(arg0);
	}

	
	
	@Override
	public void appStop(IScope arg0) 
	{;
        // remove our app
        WebSocketScopeManager manager = ((WebSocketPlugin) PluginRegistry.getPlugin("WebSocketPlugin")).getManager(arg0);
        manager.removeApplication(arg0);
        manager.stop();
        
        super.appStop(arg0);
	}

	
	
	@Override
	public void setRed5JSBridge(IJsBridge bridge) 
	{
		this.bridge = bridge;
	}

	
	
	@Override
	public IJsBridge getRed5JSBridge() 
	{
		return bridge;
	}
	

	
	@Invocable
	public void consoleLog(String message)
	{
		log.info(message);
	}
	
	
	
	@Invocable
	public int add(int a, int b)
	{
		return a + b;
	}
	
	
	@Invocable
	public String greet(String title)
	{
		return "Hello, " + title;
	}


	@Override
	public boolean appConnect(IConnection conn, Object[] params) {
		// TODO Auto-generated method stub
		
		ConnectionInfo info = new ConnectionInfo();
		info.setId(conn.getSessionId());
		info.setRemoteAddress(conn.getRemoteAddress());
		info.setPath(conn.getPath());
		info.setType(conn.getProtocol());
		
		bridge.broadcastEvent("appConnect", info);
		
		return super.appConnect(conn, params);
	}


	@Override
	public void appDisconnect(IConnection conn) {
		// TODO Auto-generated method stub
		super.appDisconnect(conn);
	}

	
	
}
