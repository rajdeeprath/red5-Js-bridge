package com.flashvisions.server.red5.jsbridge.alternate.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.IApplication;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.ResourceNotFoundException;
import org.red5.server.util.ScopeUtils;
import org.slf4j.Logger;

import com.flashvisions.server.red5.jsbridge.alternate.model.BroadcastStream;
import com.flashvisions.server.red5.jsbridge.alternate.model.Connection;
import com.flashvisions.server.red5.jsbridge.alternate.model.Scope;
import com.flashvisions.server.red5.jsbridge.alternate.model.SharedObject;
import com.flashvisions.server.red5.jsbridge.alternate.model.SubscriberStream;
import com.flashvisions.server.red5.jsbridge.interfaces.IJsBridge;
import com.flashvisions.server.red5.jsbridge.model.ConnectParamsEvent;
import com.flashvisions.server.red5.jsbridge.model.ScopeConnectionEvent;

public class MultiThreadedApplicationAdapterDelegate implements IApplication {
	
	private static final Logger logger = Red5LoggerFactory.getLogger(MultiThreadedApplicationAdapterDelegate.class, "red5-js-bridge");

	
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
		logger.info("MultiThreadedApplicationAdapter Delegate initialized");
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
		
		Connection connection = toConnection(conn);
		ConnectParamsEvent notification = new ConnectParamsEvent();
		notification.setConnection(connection);
		notification.setParams(params);
		
		bridge.broadcastApplicationEvent("application.appConnect", notification);
		return true;
	}

	
	
	
	@Override
	public boolean appJoin(IClient client, IScope app) {
		// TODO Auto-generated method stub
		IConnection conn = Red5.getConnectionLocal();
		
		ScopeConnectionEvent notification = new ScopeConnectionEvent();
		notification.setConnection(toConnection(conn));
		notification.setScope(this.toScope(app));
		
		bridge.broadcastApplicationEvent("application.appJoin", notification);
		return true;
	}

	
	
	
	@Override
	public void appDisconnect(IConnection conn) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appDisconnect", this.toConnection(conn));
		
	}

	
	
	
	@Override
	public void appLeave(IClient client, IScope app) {
		// TODO Auto-generated method stub
		IConnection conn = Red5.getConnectionLocal();
		
		ScopeConnectionEvent notification = new ScopeConnectionEvent();
		notification.setConnection(toConnection(conn));
		notification.setScope(this.toScope(app));
		
		bridge.broadcastApplicationEvent("application.appLeave", notification);
	}

	
	
	
	@Override
	public void appStop(IScope app) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.appStop", this.toScope(app));
	}

	
	
	
	@Override
	public boolean roomStart(IScope room) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomStart", this.toScope(room));
		return true;
	}

	
	
	
	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		// TODO Auto-generated method stub
		
		Connection connection = toConnection(conn);
		ConnectParamsEvent notification = new ConnectParamsEvent();
		notification.setConnection(connection);
		notification.setParams(params);
		
		bridge.broadcastApplicationEvent("application.roomConnect", notification);
		return true;
	}

	
	
	
	@Override
	public boolean roomJoin(IClient client, IScope room) {
		// TODO Auto-generated method stub
		IConnection conn = Red5.getConnectionLocal();
		
		ScopeConnectionEvent notification = new ScopeConnectionEvent();
		notification.setConnection(toConnection(conn));
		notification.setScope(this.toScope(room));
		
		bridge.broadcastApplicationEvent("application.roomJoin", null);
		return true;
	}

	
	
	
	@Override
	public void roomDisconnect(IConnection conn) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomDisconnect", this.toConnection(conn));
	}

	
	
	
	@Override
	public void roomLeave(IClient client, IScope room) {
		// TODO Auto-generated method stub
		IConnection conn = Red5.getConnectionLocal();
		
		ScopeConnectionEvent notification = new ScopeConnectionEvent();
		notification.setConnection(toConnection(conn));
		notification.setScope(this.toScope(room));
		
		bridge.broadcastApplicationEvent("application.roomLeave", notification);
	}

	
	
	
	@Override
	public void roomStop(IScope room) {
		// TODO Auto-generated method stub
		bridge.broadcastApplicationEvent("application.roomStop", this.toScope(room));
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
	
	
	
	public Connection getConnection(String sessionId) throws Exception {
		IConnection connection = getConnectionById(sessionId);
		return toConnection(connection);
	}
	
	
	
	public boolean addAtrributes(Connection conn, Map<String, Object> attribute) throws Exception {
		IConnection connection = this.getConnectionById(conn.getSessionId());
		return connection.setAttributes(attribute);
	}
	
	
	
	public boolean addAtrribute(Connection conn, String name, Object value) throws Exception {
		IConnection connection = this.getConnectionById(conn.getSessionId());
		return connection.setAttribute(name, value);
	}
	
	
	
	public Map<String, Object> getAtrributes(Connection conn) throws Exception {
		IConnection connection = this.getConnectionById(conn.getSessionId());
		return connection.getAttributes();
	}
	
	
	
	public Object getAtrribute(Connection conn, String name) throws Exception {
		IConnection connection = this.getConnectionById(conn.getSessionId());
		return connection.getAttribute(name);
	}
	
	
	
	public void disconnect(Connection conn) throws Exception {
		IConnection connection = this.getConnectionById(conn.getSessionId());
		connection.close();
	}
	
	
	
	public void ping(Connection conn) throws Exception {
		IConnection connection = this.getConnectionById(conn.getSessionId());
		connection.ping();
	}
	
	
	
	
	/****************************************************
	 * 
	 * RED5-JS STREAM API
	 * 
	 ****************************************************/
	
	
	public boolean hasBroadcastStream(String name) {
		return appAdapter.hasBroadcastStream(appScope, name);
	}
	

	
	public boolean hasBroadcastStream(Scope scope, String name) throws ResourceNotFoundException {
		return false;
	}
	
	
	public BroadcastStream getBroadcastStream(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	

	public BroadcastStream getBroadcastStream(Scope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public Set<String> getBroadcastStreamNames() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<String> getBroadcastStreamNames(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public SubscriberStream getSubscriberStream(String name) {
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
	 * @throws Exception 
	 * 
	 ****************************************************/
	
	
	private IConnection getConnectionById(String sessionId) throws Exception{
		
		IScope appScope = appAdapter.getScope();
		Set<IConnection>connections = appScope.getClientConnections();
		for(IConnection connection: connections){
			if(connection.getSessionId().equalsIgnoreCase(sessionId) && connection.isConnected()){
				return connection;
			}
		}
		
		throw new Exception("Connection object not found");
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
	
	
	
	
	private BroadcastStream toBroadcastStream(IBroadcastStream stream) 
	{
		return null;
	}
	
	
	
	
	private Scope toScope(IScope scope)
	{
		Scope alias = new Scope();
		alias.setName(scope.getName());
		alias.setPath(scope.getPath());
		alias.setContextPath(scope.getContextPath());
		alias.setDepth(scope.getDepth());
		alias.setValid(scope.isValid());
		alias.setType(scope.getType().name());
		alias.setAttributes(scope.getAttributes());
		
		return alias;
	}

	
	
	

	private IScope getSubScope(IScope parent, String subScopePath) throws ResourceNotFoundException {
        IScope roomScope = ScopeUtils.resolveScope(parent, subScopePath);
        if (roomScope == null)
            throw new ResourceNotFoundException("Scope for path " + subScopePath + " could not be resolved.");
        return roomScope;
    }
}
