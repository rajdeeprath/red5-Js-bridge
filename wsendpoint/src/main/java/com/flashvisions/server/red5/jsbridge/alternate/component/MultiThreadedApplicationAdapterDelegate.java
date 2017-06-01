package com.flashvisions.server.red5.jsbridge.alternate.component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.red5.server.adapter.IApplication;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectSecurity;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IOnDemandStream;
import org.red5.server.api.stream.IStreamPlaybackSecurity;
import org.red5.server.api.stream.IStreamPublishSecurity;
import org.red5.server.api.stream.ISubscriberStream;
import org.red5.server.exception.ClientRejectedException;

import com.flashvisions.server.red5.jsbridge.alternate.model.BroadcastStream;
import com.flashvisions.server.red5.jsbridge.alternate.model.Connection;
import com.flashvisions.server.red5.jsbridge.alternate.model.Scope;
import com.flashvisions.server.red5.jsbridge.alternate.model.SharedObject;
import com.flashvisions.server.red5.jsbridge.alternate.model.SubscriberStream;
import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridge;
import com.google.gson.JsonObject;

public class MultiThreadedApplicationAdapterDelegate implements IApplication {
	
	
	IJsBridge bridge;
	
	MultiThreadedApplicationAdapter appAdapter;
	
	
	
	public MultiThreadedApplicationAdapterDelegate(){
		
	}
	
	
	
	public MultiThreadedApplicationAdapterDelegate(IJsBridge bridge){
		this.bridge = bridge;
	}
	
	
	
	public MultiThreadedApplicationAdapterDelegate(IJsBridge bridge, MultiThreadedApplicationAdapter appAdapter){
		this.bridge = bridge;
		this.appAdapter = appAdapter;
	}	
	
	
	
	
	
	public MultiThreadedApplicationAdapter getAppAdapter() {
		return appAdapter;
	}



	public void setAppAdapter(MultiThreadedApplicationAdapter appAdapter) {
		this.appAdapter = appAdapter;
	}



	public IJsBridge getBridge() {
		return bridge;
	}



	public void setBridge(IJsBridge bridge) {
		this.bridge = bridge;
	}



	@Override
	public boolean appStart(IScope app) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appStart", null);
		return true;
	}

	
	
	
	@Override
	public boolean appConnect(IConnection conn, Object[] params) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appConnect", null);
		return true;
	}

	
	
	
	@Override
	public boolean appJoin(IClient client, IScope app) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appJoin", null);
		return true;
	}

	
	
	
	@Override
	public void appDisconnect(IConnection conn) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appDisconnect", null);
		
	}

	
	
	
	@Override
	public void appLeave(IClient client, IScope app) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appLeave", null);
	}

	
	
	
	@Override
	public void appStop(IScope app) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appStop", null);
	}

	
	
	
	@Override
	public boolean roomStart(IScope room) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomStart", null);
		return true;
	}

	
	
	
	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomConnect", null);
		return true;
	}

	
	
	
	@Override
	public boolean roomJoin(IClient client, IScope room) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomJoin", null);
		return true;
	}

	
	
	
	@Override
	public void roomDisconnect(IConnection conn) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomDisconnect", null);
	}

	
	
	
	@Override
	public void roomLeave(IClient client, IScope room) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomLeave", null);
	}

	
	
	
	@Override
	public void roomStop(IScope room) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomStop", null);
	}
	
	
	
	
	/****************************************************
	 * 
	 * RED5-JS APPLICATION API
	 * 
	 ****************************************************/

	
	
	
	
	
	/****************************************************
	 * 
	 * RED5-JS CONNECTION API
	 * 
	 ****************************************************/
	
	
	protected List<Connection> getConnections() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	protected Connection getConnection(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	protected boolean rejectClient() throws ClientRejectedException {
		// TODO Auto-generated method stub
		return false;
	}

	
	protected boolean rejectClient(Object reason) throws ClientRejectedException {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	public void disconnect(Connection conn, JsonObject scope) {
		// TODO Auto-generated method stub
	}
	
	
	
	public void disconnect(Connection conn) {
		// TODO Auto-generated method stub
	}

	
	
	
	/****************************************************
	 * 
	 * RED5-JS STREAM API
	 * 
	 ****************************************************/
	

	
	public boolean hasBroadcastStream(Scope scope, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public BroadcastStream getBroadcastStream(Scope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<String> getBroadcastStreamNames(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean hasOnDemandStream(Scope scope, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public IOnDemandStream getOnDemandStream(Scope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public SubscriberStream getSubscriberStream(Scope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public double getStreamLength(String name) {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	
	public double getStreamLength(Scope scope, String name) {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	
	public boolean recordStream(BroadcastStream stream, String path) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public boolean recordStream(Scope stream, String path, boolean overWrite) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	/****************************************************
	 * 
	 * RED5-JS SHARED OBJECT API
	 * 
	 ****************************************************/

	public boolean createSharedObject(Scope scope, String name, boolean persistent) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public SharedObject getSharedObject(Scope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public SharedObject getSharedObject(Scope scope, String name, boolean persistent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<String> getSharedObjectNames(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean hasSharedObject(Scope scope, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public boolean clearSharedObjects(Scope scope, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
