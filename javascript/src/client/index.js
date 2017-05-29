var red5JsBridgeObject = require("./lib/red5/red5JsBridgeObject.js");

red5JsBridgeObject.onOnce("session.ready", function(){
   console.log("Session ready"); 
   red5JsBridgeObject.invoke("add", 1, 2);
});
red5JsBridgeObject.init({
    autoConnect: true
});