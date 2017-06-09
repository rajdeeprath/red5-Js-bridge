package com.flashvisions.server.red5.jsbridge.utils;

import org.red5.server.api.scope.IScope;

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

}
