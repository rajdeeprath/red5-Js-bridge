/**
* Red5 js bridge library
**/

var red5JsBridgeObject = (function (window) {
   
    'use strict';
    
    
    var ws, evtHandlers = {}, rmiPromises = {}, connected = false, sessionId,
    options = {
            port: 8081,
            protocol: "ws",
            host: "localhost",
            app: "wsendpoint",
            channel: "jsbridge",
            autoConnect: false,
            debug: true
        };
    
    
    
    
    /*
    * Initialize module
    */
    function init(opts) {

        
        /* Copying options */
        
        if (opts) {
            for (var k in opts) {
                options[k]=opts[k];
            }
        }
        
        /* Initialize socket connection */
        
        if(options.autoConnect === true) {
            connect();
        }
        
        
        if(options.debug) {
            console.log("Initialized " + this);
        }
    }
    
    
    
    
    function getConnectionURL() {
        return options.protocol +"://" + options.host + ":" + options.port + "/" + options.app;
    }
    
    
    
    function test() {
        window.console.log("test");
    }
    
    
    function isNumeric(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }
    
    
    
    /*
    * Session Id from server
    */
    
    function onSessionId(id){
        
        sessionId = id;
        
        if(options.debug) {
            console.log("Session id received");
        }
    }
    
    
    
    /*
    * Get Current Session Id
    */
    function getSessionId() {
        return sessionId;
    }
    
    
    
    /*
    * Websocket events
    */
    
    
    function onSocketOpen(evt){
        
        connected = true;
        
        if(options.debug) {
            console.log("Connected");
        }
    }
    
    
    function onSocketMessage(evt){
        
        if(options.debug) {
            console.log("Message received " + JSON.stringify(evt.data));
        }
        
        handleMessage(evt.data);
    }
    
    
    function onSocketError(evt){
        
        if(options.debug) {
            console.log("Socket error " + JSON.stringify(evt));
        }
    }
    
    
    function onSocketClose(evt){
        
        connected = false;
        
        if(options.debug) {
            console.log("Socket closed");
        }
    }
    
    
    
    function handleMessage(response) {
        
        var obj = JSON.parse(response);
        
        if(obj.hasOwnProperty("type")){
            if(obj["type"] === "EVENT"){
                if(options.debug) {
                    console.log("Event received");
                }                
                // handle event
                var evt = obj.data;
                if(evt.name === "session.id"){
                    sessionId = evt.data;
                    if(options.debug) {
                        console.log("Session Id set " + sessionId);
                    }
                }
            }else if(response["type"] === "RMI") {
                // handle RMI response
            }
        }
    }
    
    
    
    function connect(){
        
        if(isValidOptions()){
            if(!ws) {
                ws = new WebSocket(getConnectionURL(), options.channel);
                ws.onopen = onSocketOpen;
                ws.onmessage = onSocketMessage;
                ws.onerror = onSocketError;
                ws.onclose = onSocketClose;
            }else{
                throw new Error("Websocket was already initialized");
            }
        }else{
            throw new Error("Invalid options for websocket connection");
        }
    }
    
    
    
    
    /*
    * Check if we have valid optiosn for websocket connection to red5 app
    */
    
    function isValidOptions(){
        
        var valid = true;
        if(options){
            if(!options.port || !isNumeric(options.port)){
               valid = false; 
            }else if(options.protocol !== "ws" && options.protocol !== "wss" ){
               valid = false; 
            }else if(!options.app){
               valid = false; 
            }else if(!options.host){
               valid = false; 
            }else if(!options.channel){
               valid = false; 
            }
        }
        
        return valid;
    }
    
    
    
    
    /*
    * Identify parameter types smartly and package them for sending over the bridge
    */
    function preprocess(parameters) {
        
        var processedParameters = [];
        
        parameters.forEach(function (param) {
           // TO DO 
        });
        
        
        return processedParameters;
    }
    
    
    
    
    /*
    * Generate a unique id for RMI requests
    */
    function generateUniqueId() {
        
        return sessionId;
    }
    
    
    
    
    /*
    * Create request packet for RMI call
    */
    function createRMIRequest(method, parameters) {
        var packet = {};
        packet.id = generateUniqueId();
        packet.timestamp = new Date().getTime();
        packet.type = "RMI";
        packet.method = method;
        packet.data = preprocess(parameters);
        return packet;
    }
    
    
    
    
    /*
    * Raw send via websocket
    */
    function send(request) {
        
         // Return promise
    }
     
    
    
   
    /*
    * Invoke a method on server
    */
    function invoke(method, parameters) {
        
        var request = createRMIRequest(method, parameters);
        send(request);
    }
    
    
    
    
    
    
    
    /*******************
    * Event managment
    *******************/
    
    
    
    /*
    * Register a handler for event
    */
    function registerEventHandler(evt, handler) {
        
        if (evtHandlers[evt] === 'undefined') {
            evtHandlers[evt] = [];
        }
        
        
        evtHandlers[evt].push(handler);
    }
    
    
    
    /*
    * UnRegister a handler for event
    */
    function unregisterEventHandler(evt, handler) {
        
        if (evtHandlers[evt] === 'undefined') {
            return;
        }
        
        var handlers = evtHandlers[evt], i, fn;
        
        for (i = 0; i < handlers.length; i += 1) {
            if (handlers[i] === handler) {
                fn = handlers.splice(i, 1);
                return fn;
            }
        }
        
        return;
    }
    
    
    
    /*
    * Clear all handlers for event
    */
    function unregisterAllHandlers(evt) {
        
        if (evtHandlers[evt] === 'undefined') {
            return;
        }
        
        var handlers = evtHandlers[evt], i = evtHandlers[evt].length;
        
        while (i > 0) {
            handlers.splice(i, 1);
            i -= 1;
        }
        
    }
    
    
    return {
        test: test,
        invoke: invoke,
        getSessionId: getSessionId,
        on: registerEventHandler,
        off: unregisterEventHandler,
        offAll: unregisterAllHandlers,
        init: init
    };
    
}(window));


module.exports = red5JsBridgeObject;