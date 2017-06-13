(function (window) {

	var Red5JsBridgedApplication = Red5Js.Red5JsBridgedApplication;
    var bridge = new Red5JsBridgedApplication({debug: true}, {
    
    "appStart" : function(scope) {
        console.log("appStart");
    },                                          
                                          
    "appConnect" : function(connection, params) {
        console.log("appConnect");
        
        bridge.addAtrribute(connection, "time", new Date().getTime())
        .then(function(result){
            console.log("result " + JSON.stringify(result));
            
            bridge.getAtrributes(connection).then(function(result) {
                
                console.log("result " + JSON.stringify(result));
                return result;  
                
            }).then(function(result) {
                
                let map = new Map();
                map.set("name", "rajdeep");
                map.set("active", true);
                
                bridge.addAtrributes(connection, map).then(function(result){
                     
                    console.log("success");
                    return result;
                    
                }).then(function(result){
                     
                    disconnect(connection).then(function(res){
                        
                        console.log("success");
                        
                    }).catch(function(err){
                     
                        console.error("err " + err);
                    
                    });
                    
                }).catch(function(err){
                     
                    console.error("err " + err);
                    
                });
                
            })
            .catch(function(err){
                
                console.error("err " + err);
                
            });
        })
        .catch(function(err){
            
             console.error("err " + err);
            
        });
        
    },                                        
                                          
    "appJoin" : function(connection, scope) {
        console.log("appJoin");
    },
                                          
    "roomConnect" : function(connection, params) {
        console.log("roomConnect");
    },                                        
                                          
    "roomJoin" : function(connection, scope) {
        roomscope = scope;
        console.log("roomJoin");
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