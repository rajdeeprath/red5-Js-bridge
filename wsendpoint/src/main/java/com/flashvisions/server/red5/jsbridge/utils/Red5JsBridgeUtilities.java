package com.flashvisions.server.red5.jsbridge.utils;

import org.red5.server.api.scope.IScope;
import org.red5.server.api.stream.ResourceNotFoundException;
import org.red5.server.util.ScopeUtils;

import com.flashvisions.server.red5.jsbridge.alternate.model.Scope;

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
		if(appScope.getPath().equals(path)){
			return appScope;
		}
		
		String appScopePath = appScope.getPath() + "/" + appScope.getName() + "/";
		if(path.contains(appScopePath)){
			path = path.replace(appScopePath, "");
		}
		
		IScope roomScope = ScopeUtils.resolveScope(appScope, scope.getPath());
        if (roomScope == null)
            throw new ResourceNotFoundException("Scope for path" + scope.getPath() +" could not be resolved.");
        return roomScope;
	}
	
	
	
	
	public static IScope fromScopePath(IScope appScope, String path) throws ResourceNotFoundException {
		
		if(appScope.getPath().equals(path)){
			return appScope;
		}
		
		String appScopePath = appScope.getPath() + "/" + appScope.getName() + "/";
		if(path.contains(appScopePath)){
			path = path.replace(appScopePath, "");
		}
		
		IScope roomScope = ScopeUtils.resolveScope(appScope, path);
        if (roomScope == null)
            throw new ResourceNotFoundException("Scope for path" + path +" could not be resolved.");
        return roomScope;
	}

}
