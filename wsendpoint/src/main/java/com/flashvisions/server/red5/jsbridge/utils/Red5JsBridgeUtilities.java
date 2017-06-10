package com.flashvisions.server.red5.jsbridge.utils;

import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.statistics.IClientBroadcastStreamStatistics;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStream;
import org.red5.server.api.stream.ISubscriberStream;
import org.red5.server.api.stream.ResourceNotFoundException;
import org.red5.server.util.ScopeUtils;

import com.flashvisions.server.red5.jsbridge.alternate.model.BroadcastStream;
import com.flashvisions.server.red5.jsbridge.alternate.model.Connection;
import com.flashvisions.server.red5.jsbridge.alternate.model.Scope;
import com.flashvisions.server.red5.jsbridge.alternate.model.SharedObject;
import com.flashvisions.server.red5.jsbridge.alternate.model.Stream;
import com.flashvisions.server.red5.jsbridge.alternate.model.SubscriberStream;
import com.flashvisions.server.red5.jsbridge.model.BroadcastStreamStatistics;

public class Red5JsBridgeUtilities {
	
	public static Scope toScope(IScope scope)
	{
		Scope alias = new Scope();
		alias.setName(scope.getName());
		alias.setPath(scope.getPath());
		alias.setContextPath(scope.getContextPath());
		alias.setDepth(scope.getDepth());
		alias.setValid(scope.isValid());
		alias.setType(scope.getType().name());
		//alias.setAttributes(scope.getAttributes());
		
		return alias;
	}
	
	
	public static IScope fromScope(IScope appScope, Scope scope) throws ResourceNotFoundException {
		
		String path = scope.getPath();
		String appScopePath = appScope.getPath() + "/" + appScope.getName();
		if(appScopePath.equalsIgnoreCase(appScopePath) || appScope.getPath().equals(path))
		{
			return appScope;
		}
		else if(path.contains(appScopePath))
		{
			path = path.replace(appScopePath, "");
		}
		
		if(path.startsWith("/")){
			path = path.replace("/", "");
		}
		
		IScope roomScope = ScopeUtils.resolveScope(appScope, scope.getPath());
        if (roomScope == null){
            throw new ResourceNotFoundException("Scope for path" + scope.getPath() +" could not be resolved.");
        }
        
        return roomScope;
	}
	
	
	
	
	public static IScope fromScopePath(IScope appScope, String path) throws ResourceNotFoundException {
		
		String appScopePath = appScope.getPath() + "/" + appScope.getName();
		if(appScopePath.equalsIgnoreCase(path) || appScope.getPath().equals(path))
		{
			return appScope;
		}
		else if(path.contains(appScopePath))
		{
			path = path.replace(appScopePath, "");
		}
		
		if(path.startsWith("/")){
			path = path.replace("/", "");
		}
		
		IScope roomScope = ScopeUtils.resolveScope(appScope, path);
        if (roomScope == null){
            throw new ResourceNotFoundException("Scope for path" + path +" could not be resolved.");
        }
        
        return roomScope;
	}
	
	
	
	
	public static BroadcastStreamStatistics toBroadcastStreamStatistics(IClientBroadcastStreamStatistics statistics) {
		
		BroadcastStreamStatistics alias = new BroadcastStreamStatistics();
		alias.setTotalSubscribers(statistics.getTotalSubscribers());
		alias.setMaxSubscribers(statistics.getMaxSubscribers());
		alias.setActiveSubscribers(statistics.getActiveSubscribers());
		alias.setBytesReceived(statistics.getBytesReceived());
		return alias;
	}
	
	
	
	public static Connection toConnection(IConnection connection){
		
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
		alias.setClassName(connection.getClass().getSimpleName());
		
		return alias;
	}
	
	
	
	
	public static SharedObject toSharedObject(ISharedObject so) 
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
	
	
	
	
	public static BroadcastStream toBroadcastStream(IBroadcastStream stream) 
	{
		BroadcastStream alias = new BroadcastStream();
		alias.setName(stream.getName());
		alias.setCreationTime(stream.getCreationTime());
		alias.setScopePath(stream.getScope().getPath() + "/" + stream.getScope().getName());
		alias.setPublishedName(stream.getPublishedName());
		alias.setSaveFilename(stream.getSaveFilename());
		alias.setStartTime(stream.getStartTime());
		
		return alias;
	}
	
	
	
	
	public static SubscriberStream toSubscribeStream(ISubscriberStream stream) 
	{
		SubscriberStream alias = new SubscriberStream();
		alias.setName(stream.getName());
		alias.setBroadcastStreamPublishName(stream.getBroadcastStreamPublishName());
		alias.setCreationTime(stream.getCreationTime());
		alias.setScopePath(stream.getScope().getPath() + "/" + stream.getScope().getName());
		alias.setPaused(stream.isPaused());
		alias.setState(stream.getState().name());
		alias.setStartTime(stream.getStartTime());
		
		return alias;
	}
	
	
	
	
	
	public static Stream toStream(IStream stream) 
	{
		Stream alias = new Stream();
		alias.setName(stream.getName());
		alias.setCreationTime(stream.getCreationTime());
		alias.setScopePath(stream.getScope().getPath() + "/" + stream.getScope().getName());
		alias.setStartTime(stream.getStartTime());
		
		return alias;
	}
	

}
