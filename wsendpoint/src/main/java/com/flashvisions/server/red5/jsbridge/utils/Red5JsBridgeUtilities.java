package com.flashvisions.server.red5.jsbridge.utils;

import org.red5.server.api.scope.IScope;
import org.red5.server.api.statistics.IClientBroadcastStreamStatistics;
import org.red5.server.api.stream.ResourceNotFoundException;
import org.red5.server.util.ScopeUtils;

import com.flashvisions.server.red5.jsbridge.alternate.model.Scope;
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

}
