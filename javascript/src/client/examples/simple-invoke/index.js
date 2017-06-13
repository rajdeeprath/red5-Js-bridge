(function (window) {

	var Red5JsBridgedApplication = Red5Js.Red5JsBridgedApplication;
    var bridge = new Red5JsBridgedApplication({debug: true});
    
    bridge.on('bridge.ready', function(session) {
    
        console.log("bridge is connected " + JSON.stringify(session));
        
        bridge.invoke('greet', "rajdeep").then(function(result) {
            
          console.log("result =>" + result);  
            
        }).catch(function(error) {
            
          console.error("error =>" + error);  
            
        });    
    });
    
    bridge.connect();
                    
})(window);