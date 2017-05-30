var red5JsBridgeObject = require("./lib/red5/red5JsBridgeObject.js");

red5JsBridgeObject.onOnce("session.ready", function(){
    console.log("Session ready"); 
        red5JsBridgeObject.invoke("greet", "Rajdeep").then(function(data){       
            console.log("data received " + data);
        }).catch(function(error) {
            console.log("Failed!", error);   
        });
});
red5JsBridgeObject.init({
    autoConnect: true
});