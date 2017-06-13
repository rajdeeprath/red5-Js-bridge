(function (window) {

	var Red5JsBridgedApplication = Red5Js.Red5JsBridgedApplication;
    var bridge = new Red5JsBridgedApplication({debug: true}, {
    
    "appStart" : function(scope) {
        console.log("appStart");
    },                                          
                                          
    "appConnect" : function(connection, params) {
        console.log("appConnect");        
        
        bridge.getApplicationScope().then(function(scope){
            
            console.log("App scope " + JSON.stringify(scope));
            
        }).catch(err) {
            
            console.error("err " + err);
        }
    },                                        
                                          
    "appJoin" : function(connection, scope) {
        console.log("appJoin");
    },
                                          
    "roomConnect" : function(connection, params) {
        console.log("roomConnect");
    },                                        
                                          
    "roomJoin" : function(connection, scope) {
        console.log("roomJoin");
        
        bridge.getChildScopeNames().then(function(result){
            
            console.log("Room names :" + JSON.stringify(result));
            
        }).catch(function(err){
           
            console.error("err " + err);
            
        });
    },

    "roomLeave" : function(connection, scope) {
        console.log("roomLeave");
    },  
    
    "roomStop" : function(scope) {
        console.log("roomStop");
    },
                                          
    "roomDisconnect" : function(connection) {
        console.log("roomDisconnect");
    },
    
    "appDisconnect" : function(connection) {
        console.log("appDisconnect");
    },
    
    "appStop" : function(scope) {
        console.log("appStop");
    },
    
    "streamBroadcastStart" : function(connection, stream) {
        console.log("streamBroadcastStart");
		
		bridge.getScope(stream.scopePath).then(function(scope){
            
             console.log("stream scope = " + scope);
            
        }).catch(function(err){
           
            console.error("err " + err);
            
        });
    },
    
    "streamBroadcastClose" : function(connection, stream) {
        console.log("streamBroadcastClose");
    },
    
    "streamSubscriberStart" : function(connection, stream) {
        console.log("streamSubscriberStart");        
    },
    
    "streamSubscriberClose" : function(connection, stream) {
         console.log("streamSubscriberClose");
    }
                                          
});


bridge.on('bridge.ready', function(session) {
    
    console.log("bridge is connected " + JSON.stringify(session));
    
});
    
bridge.connect();
                    
})(window);