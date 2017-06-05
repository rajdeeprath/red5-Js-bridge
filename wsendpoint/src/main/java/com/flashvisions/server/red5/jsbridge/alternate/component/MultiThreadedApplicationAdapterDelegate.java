package com.flashvisions.server.red5.jsbridge.alternate.component;

import java.io.IOException;
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
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.ISubscriberStream;
import org.red5.server.api.stream.ResourceExistException;
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
		IScope subScope = fromScope(scope);
		return appAdapter.hasBroadcastStream(subScope, name);
	}
	
	
	public BroadcastStream getBroadcastStream(String name) {		
		BroadcastStream stream = toBroadcastStream(appAdapter.getBroadcastStream(appScope, name));
		return stream;
	}

	

	public BroadcastStream getBroadcastStream(Scope scope, String name) throws ResourceNotFoundException {
		IScope subScope = fromScope(scope);
		BroadcastStream stream = toBroadcastStream(appAdapter.getBroadcastStream(subScope, name));
		return stream;
	}


	public Set<String> getBroadcastStreamNames() {
		return appAdapter.getBroadcastStreamNames(appScope);
	}

	
	public Set<String> getBroadcastStreamNames(Scope scope) throws ResourceNotFoundException {
		IScope subScope = fromScope(scope);
		return appAdapter.getBroadcastStreamNames(subScope);
	}
	
	
	public SubscriberStream getSubscriberStream(String name) {
		SubscriberStream stream = this.toSubscribeStream(appAdapter.getSubscriberStream(appScope, name));
		return stream;
	}
	
	
	public SubscriberStream getSubscriberStream(Scope scope, String name) throws ResourceNotFoundException {
		IScope subScope = fromScope(scope);
		SubscriberStream stream = this.toSubscribeStream(appAdapter.getSubscriberStream(subScope, name));
		return stream;
	}

	
	public double getStreamLength(String name) {
		return appAdapter.getStreamLength(name);
	}

	
	
	public void recordStream(BroadcastStream stream, String saveAs, boolean overWrite) throws IOException, ResourceNotFoundException, ResourceExistException {
		IBroadcastStream bStream = appAdapter.getBroadcastStream(appScope, stream.getName());
		if(bStream != null){
			bStream.saveAs(saveAs, !overWrite);
		}
	}
	
	
	public void recordStream(BroadcastStream stream, Scope scope, String saveAs, boolean overWrite) throws IOException, ResourceNotFoundException, ResourceExistException {
		IScope subScope = fromScope(scope);
		IBroadcastStream bStream = appAdapter.getBroadcastStream(subScope, stream.getName());
		if(bStream != null){
			bStream.saveAs(saveAs, !overWrite);
		}
	}
	
	
	
	/****************************************************
	 * 
	 * RED5-JS SHARED OBJECT API
	 * @throws ResourceNotFoundException 
	 * 
	 ****************************************************/
	
	public boolean createSharedObject(String name, boolean persistent) throws ResourceNotFoundException {
		
		ISharedObject so = null;
		
		so = appAdapter.getSharedObject(appScope, name);
		if(so == null){
			boolean created = appAdapter.createSharedObject(appScope, name, persistent);
			return created;
		}
		
		return true;
	}
	
	
	

	public boolean createSharedObject(Scope scope, String name, boolean persistent) throws ResourceNotFoundException {
		IScope target = this.fromScope(scope);
		ISharedObject so = null;
		
		so = appAdapter.getSharedObject(target, name);
		if(so == null){
			boolean created = appAdapter.createSharedObject(target, name, persistent);
			return created;
		}
		
		return true;
	}

	
	
	public SharedObject getSharedObject(String name) throws IOException {
		
		ISharedObject so = null;
		
		so = appAdapter.getSharedObject(appScope, name);
		
		if(so == null)
		{
			logger.info("Shared object not found, creating new..");
			boolean created = appAdapter.createSharedObject(appScope, name, false);
			if(created)
			{
				so = appAdapter.getSharedObject(appScope, name);
			}
			else
			{
				throw new IOException("SharedObject with name " + name + " could not be created");
			}
		}
		
		return this.toSharedObject(so);
	}
	
	
	
	
	public SharedObject getSharedObject(Scope scope, String name) throws IOException, ResourceNotFoundException {
		
		ISharedObject so = null;
		IScope subScope = fromScope(scope);
		
		so = appAdapter.getSharedObject(subScope, name);
		
		if(so == null)
		{
			logger.info("Shared object not found, creating new..");
			boolean created = appAdapter.createSharedObject(subScope, name, false);
			if(created)
			{
				so = appAdapter.getSharedObject(subScope, name);
			}
			else
			{
				throw new IOException("SharedObject with name " + name + " could not be created");
			}
		}
		
		return this.toSharedObject(so);
	}

	
	
	
	
	public SharedObject getSharedObject(String name, boolean persistent) throws IOException {
		
		ISharedObject so = null;
		
		so = appAdapter.getSharedObject(appScope, name);
		
		if(so == null)
		{
			logger.info("Shared object not found, creating new..");
			boolean created = appAdapter.createSharedObject(appScope, name, persistent);
			if(created)
			{
				so = appAdapter.getSharedObject(appScope, name);
			}
			else
			{
				throw new IOException("SharedObject with name " + name + " could not be created");
			}
		}
		
		return this.toSharedObject(so);
	}
	
	
	
	
	public SharedObject getSharedObject(Scope scope, String name, boolean persistent) throws IOException, ResourceNotFoundException {
		
		ISharedObject so = null;
		IScope subScope = fromScope(scope);
		
		so = appAdapter.getSharedObject(subScope, name);
		
		if(so == null)
		{
			logger.info("Shared object not found, creating new..");
			boolean created = appAdapter.createSharedObject(subScope, name, persistent);
			if(created)
			{
				so = appAdapter.getSharedObject(subScope, name);
			}
			else
			{
				throw new IOException("SharedObject with name " + name + " could not be created");
			}
		}
		
		return this.toSharedObject(so);
	}

	
	
	public Set<String> getSharedObjectNames() {
		return appAdapter.getSharedObjectNames(appScope);
	}
	
	
	public Set<String> getSharedObjectNames(Scope scope) throws ResourceNotFoundException {
		IScope subScope = fromScope(scope);
		return appAdapter.getSharedObjectNames(subScope);
	}
	
	
	
	public boolean hasSharedObject(String name) {
		return appAdapter.hasSharedObject(appScope, name);
	}

	
	
	public boolean hasSharedObject(Scope scope, String name) throws ResourceNotFoundException {
		IScope subScope = fromScope(scope);
		return appAdapter.hasSharedObject(subScope, name);
	}
	
	
	public boolean clearSharedObjects(String name) {
		return appAdapter.clearSharedObjects(appScope, name);
	}
	
	
	public boolean clearSharedObjects(Scope scope, String name) throws ResourceNotFoundException {
		IScope subScope = fromScope(scope);
		return appAdapter.clearSharedObjects(subScope, name);
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
	
	
	
	
	private SharedObject toSharedObject(ISharedObject so) 
	{
		SharedObject alias = new SharedObject();
		alias.setName(so.getName());
		alias.setPath(so.getPath());
		alias.setType(so.getType().name());
		alias.setAcquired(so.isAcquired());
		alias.setDepth(so.getDepth());
		alias.setPersistent(so.isPersistent());
		alias.setVersion(so.getVersion());
		alias.setValid(so.isValid());
		alias.setLocked(so.isLocked());
		alias.setData(so.getData());
		
		return alias;
	}
	
	
	
	
	private BroadcastStream toBroadcastStream(IBroadcastStream stream) 
	{
		BroadcastStream alias = new BroadcastStream();
		alias.setName(stream.getName());
		alias.setCreationTime(stream.getCreationTime());
		alias.setScopePath(stream.getScope().getPath());
		alias.setPublishedName(stream.getPublishedName());
		alias.setSaveFilename(stream.getSaveFilename());
		alias.setStartTime(stream.getStartTime());
		
		return null;
	}
	
	
	
	
	private SubscriberStream toSubscribeStream(ISubscriberStream stream) 
	{
		SubscriberStream alias = new SubscriberStream();
		alias.setName(stream.getName());
		alias.setBroadcastStreamPublishName(stream.getBroadcastStreamPublishName());
		alias.setCreationTime(stream.getCreationTime());
		alias.setScopePath(stream.getScope().getPath());
		alias.setPaused(stream.isPaused());
		alias.setState(stream.getState().name());
		alias.setStartTime(stream.getStartTime());
		
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
	
	
	
	
	
	private IScope fromScope(Scope scope) throws ResourceNotFoundException {
		IScope roomScope = ScopeUtils.resolveScope(appScope, scope.getPath());
        if (roomScope == null)
            throw new ResourceNotFoundException("Scope for path " + scope.getPath() + " could not be resolved.");
        return roomScope;
	}
}
