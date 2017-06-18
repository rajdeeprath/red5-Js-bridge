package com.flashvisions.server.red5.plugin.api;

import java.util.Iterator;
import java.util.Set;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.listeners.IScopeListener;
import org.red5.server.api.scope.IBasicScope;
import org.red5.server.api.scope.IGlobalScope;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.plugin.PluginRegistry;
import org.red5.server.plugin.Red5Plugin;
import org.red5.net.websocket.WebSocketPlugin;
import org.red5.net.websocket.WebSocketScope;
import org.red5.net.websocket.WebSocketScopeManager;
import org.red5.net.websocket.listener.IWebSocketScopeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flashvisions.server.red5.jsbridge.Application;
import com.flashvisions.server.red5.jsbridge.listeners.JsBridgeDataListener;
import com.flashvisions.server.red5.plugin.api.model.Configuration;



public class Red5JsPlugin extends Red5Plugin {
	
	private static Logger log = LoggerFactory.getLogger(Application.class);
	
	private String name = "red5-js-plugin";
	
	private Red5JsWebSocketScopeListener wsScopeLisneter;
	
	private static String BEAN_ID = "red5JsBridge";
	
	
	@Override
	public void doStart() throws Exception {
		// TODO Auto-generated method stub
		
		attachRed5JsToScopes();
	}

	
	
	
	@Override
	public void doStop() throws Exception {
		// TODO Auto-generated method stub
		log.info("stop");
	}

	
	
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		log.info("init");
	}

	
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	
	
	
	private void attachRed5JsToScopes()
	{
		IScopeListener scopeListener = new IScopeListener() {

			@Override
			public void notifyScopeCreated(IScope scope) {
				
				if (scope.getType() == ScopeType.APPLICATION) {
					
					if (scope.getContext().hasBean(BEAN_ID)) {
						
                        Configuration scopeBridgeConfig = (Configuration) scope.getContext().getBean(BEAN_ID);
	            		configureWebsocketService(scope);
	            	}
				}
				
			}
			

			@Override
			public void notifyScopeRemoved(IScope scope) {
				
				if (scope.getType() == ScopeType.APPLICATION) {
					
					removeWebsocketService(scope);
				}
			}


		};
		
		
		 server.addListener(scopeListener);

	     /**********************************************************************/
		 
		 
		 Iterator<IGlobalScope> inter = server.getGlobalScopes();
	        while (inter.hasNext()) {
	            IGlobalScope gscope = inter.next();
	            Set<String> appSet = gscope.getBasicScopeNames(ScopeType.APPLICATION);
	            Iterator<String> setInter = appSet.iterator();
	            while (setInter.hasNext()) {
	                String sApp = setInter.next();
	                IBasicScope theApp = gscope.getBasicScope(ScopeType.APPLICATION, sApp);
	                IScope issc = (IScope) theApp;
	                
	                if (issc.getContext().hasBean(BEAN_ID)) {
	                	
	                	Configuration scopeBridgeConfig = (Configuration) issc.getContext().getBean(BEAN_ID);
	            		configureWebsocketService(issc);
	            	}
	            }
	        }
	}
	
	
	
	
	private void configureWebsocketService(IScope scope) {
		// TODO Auto-generated method stub
		
		 log.debug("Configuring application scope: {}", scope);
         
		 WebSocketPlugin wsPlugin = ((WebSocketPlugin) PluginRegistry.getPlugin("WebSocketPlugin"));
         WebSocketScopeManager manager = wsPlugin.getManager(scope);
         if (manager == null) {
        	 log.info("Creating WebSocketScopeManager for {}", scope);
             
        	 MultiThreadedApplicationAdapter app = (MultiThreadedApplicationAdapter) scope.getHandler();
             wsPlugin.setApplication(app);
             manager = wsPlugin.getManager(scope);
         }
         
         
         if (wsScopeLisneter == null) {
        	 wsScopeLisneter = new Red5JsWebSocketScopeListener();
         }
         manager.addListener(wsScopeLisneter);
         
         
         JsBridgeDataListener listener = new JsBridgeDataListener();
         listener.setApplicationContext(scope.getContext().getApplicationContext());
         try 
         {
             listener.afterPropertiesSet();
             WebSocketScope wsScope = new WebSocketScope(scope);
             wsScope.addListener(listener);
             wsScope.register();
         } 
         catch (Exception e) 
         {
             log.warn("Websocket setup failure", e);
         }
	}

	
	
	private void removeWebsocketService(IScope scope) 
	{
		WebSocketPlugin wsPlugin = ((WebSocketPlugin) PluginRegistry.getPlugin("WebSocketPlugin"));
        WebSocketScopeManager manager = wsPlugin.removeManager(scope);
        if (manager != null) {
            manager.stop();
        }
	}
	
	
	
	
	class Red5JsWebSocketScopeListener implements IWebSocketScopeListener {

		@Override
		public void scopeCreated(WebSocketScope wsScope) {
			
			IScope scope = wsScope.getScope();
			
			if (scope != null) 
			{
	            if (scope.getType() == ScopeType.APPLICATION) 
	            {
	            	if (scope.getContext().hasBean(BEAN_ID)) {
	            		
	            		Configuration scopeBridgeConfig = (Configuration) scope.getContext().getBean(BEAN_ID);
	            		configureWebsocketService(scope);
	            	}
	            }
			}
			else
			{
				log.warn("Something is not right... I didnt get the scope object!");
			}
			
		}
		
	}
}
