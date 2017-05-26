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
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
//import org.slf4j.Logger;


import org.slf4j.Logger;

import com.flashvisions.server.red5.jsbridge.interfaces.IJSBridgeAware;
import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridge;

/**
 * Sample application that uses the client manager.
 * 
 * @author The Red5 Project (red5@osflash.org)
 */
public class Application extends MultiThreadedApplicationAdapter implements IJSBridgeAware{

	private static Logger log = Red5LoggerFactory.getLogger(Application.class);

	private IJsBridge bridge;
	
	
	@Override
	public boolean appConnect(IConnection arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		return super.appConnect(arg0, arg1);
	}

	
	
	@Override
	public void appDisconnect(IConnection arg0) 
	{
		super.appDisconnect(arg0);
	}
	
	
	
	@Override
	public boolean appStart(IScope arg0) 
	{
		return super.appStart(arg0);
	}

	
	
	@Override
	public void appStop(IScope arg0) 
	{
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
	


}
