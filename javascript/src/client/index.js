var red5Js = require('./red5Js.js');


var bridge = new red5Js.Red5JsBridgedApplication({debug: true}, {
    
    "appStart" : function(scope) {
        console.log("appStart");
    },                                          
                                          
    "appConnect" : function(connection, params) {
        console.log("appConnect");
    },                                        
                                          
    "appJoin" : function(connection, scope) {
        console.log("appJoin");
    },
                                          
    "roomConnect" : function(connection, params) {
        console.log("roomConnect");
    },                                        
                                          
    "roomJoin" : function(connection, scope) {
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


bridge.on('bridge.ready', function(session){
    console.log("bridge is open");
    console.log("Session Id " + session.sessionId);
    console.log("Application Scope " + JSON.stringify(session.scope));
});

bridge.connect();