package com.flashvisions.server.red5.jsbridge.alternate.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.red5.server.adapter.IApplication;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
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
	
	IScope appScope;
	
	
	
	public MultiThreadedApplicationAdapterDelegate(){
		
	}
	
	
	
	public MultiThreadedApplicationAdapterDelegate(IJsBridge bridge){
		this.bridge = bridge;
	}
	
	
	
	public MultiThreadedApplicationAdapterDelegate(IJsBridge bridge, MultiThreadedApplicationAdapter appAdapter){
		this.bridge = bridge;
		this.appAdapter = appAdapter;		
	}	
	
	
	
	public void initialize(){
		this.appScope = this.appAdapter.getScope();
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
	
	
	public List<Connection> getConnections() {
		
		Set<IConnection> connections = appScope.getClientConnections();
		List<Connection> aliases = new ArrayList<Connection>();
		
		for(IConnection conn : connections){
			aliases.add(this.toConnection(conn));
		}
		
		return aliases;
	}
	
	
	
	public Connection getConnection(String sessionId) {
		IConnection conn = getConnectionById(sessionId);
		if(conn != null){
			return toConnection(conn);
		}		
		return null;
	}

	
	
	
	public boolean addAtrributes(Connection conn, Map<String, Object> attribute) {
		
		IConnection connection = this.getConnectionById(conn.getSessionId());
		if(conn != null){
			return connection.setAttributes(attribute);
		}
		
		return false;
	}
	
	
	
	
	public boolean addAtrribute(Connection conn, String name, Object value) {
		
		IConnection connection = this.getConnectionById(conn.getSessionId());
		if(conn != null){
			return connection.setAttribute(name, value);
		}
		
		return false;
	} 

	
	
	
	public Map<String, Object> getAtrributes(Connection conn) {
		
		IConnection connection = this.getConnectionById(conn.getSessionId());
		if(conn != null){
			return connection.getAttributes();
		}
		
		return null;
	}

	
	
	
	public Object getAtrribute(Connection conn, String name) {
		
		IConnection connection = this.getConnectionById(conn.getSessionId());
		if(conn != null){
			return connection.getAttribute(name);
		}
		
		return null;
	}
	
	
	
	public void disconnect(Connection conn) 
	{
		IConnection connection = this.getConnectionById(conn.getSessionId());
		if(conn != null){
			connection.close();
		}
	}
	
	
	
	public void ping(Connection conn) {
		
		IConnection connection = this.getConnectionById(conn.getSessionId());
		if(conn != null){
			connection.ping();
		}
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
	
	
	
	/****************************************************
	 * 
	 * RED5-JS UTILITIES
	 * 
	 ****************************************************/
	
	
	private IConnection getConnectionById(String sessionId){
		
		IScope appScope = appAdapter.getScope();
		Set<IConnection>connections = appScope.getClientConnections();
		for(IConnection connection: connections){
			if(connection.getSessionId().equalsIgnoreCase(sessionId)){
				return connection;
			}
		}
		
		return null;
	}
	
	
	
	
	
	
	
	
	private Connection toConnection(IConnection connection){
		
		Connection alias = new Connection();
		alias.setSessionId(connection.getSessionId());
		alias.setConnected(connection.isConnected());
		alias.setConnectionParams(connection.getConnectParams());
		alias.setDroppedMessages(connection.getDroppedMessages());
		alias.setDuty(connection.getDuty().name());
		alias.setEncoding(connection.getEncoding().name());
		alias.setHost(connection.getHost());
		alias.setLastPingTime(connection.getLastPingTime());
		alias.setPath(connection.getPath());
		alias.setPendingMessages(connection.getPendingMessages());
		alias.setProtocol(connection.getProtocol());
		alias.setReadBytes(connection.getReadBytes());
		alias.setWrittenBytes(connection.getWrittenBytes());
		alias.setReadMessages(connection.getReadMessages());
		alias.setWrittenMessages(connection.getWrittenMessages());
		alias.setRemoteAddress(connection.getRemoteAddress());
		alias.setRemoteAddresses(connection.getRemoteAddresses());
		alias.setRemotePort(connection.getRemotePort());
		
		return alias;
	}
}
