/**
* Red5 js bridge library
**/

var red5JsBridgeObject = (function (window) {
   
    'use strict';
    
    
    var Promise = require('promise');
    var Events = require('events');
    var typeCheck = require('type-check').typeCheck;
    
    var eventEmitter = new Events.EventEmitter();
    var ws, evtHandlers = {}, rmiPromises = {}, connected = false, sessionId, rmiRecordKeeper,
    options = {
            port: 8081,
            protocol: "ws",
            host: "localhost",
            app: "wsendpoint",
            channel: "jsbridge",
            autoConnect: false,
            debug: true,
            rmiTimeout: 5000
        };
    
    
    
    
    function manageRMIRegistry(){
        
        if(options.debug) {
            console.log("Scanning RMI registry");
        }
        
        var now = new Date().getTime();
        for (var key in rmiPromises) {
            var obj = rmiPromises[key];
            if(obj.timestamp && (now - obj.timestamp > rmiTimeout)){
                obj.reject(new Error("Request failed to commute"));
                delete rmiPromises[key];
            }
        }
    }
    
    
    
    function startRMIRegistryMonitor(){
        if(rmiRecordKeeper) clearInterval(rmiRecordKeeper);
        rmiRecordKeeper = setInterval(manageRMIRegistry, 10000);
    }
    
    
    
    
    function stopRMIRegistryMonitor(){
        if(rmiRecordKeeper) clearInterval(rmiRecordKeeper);
    }
    
    
    
    
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
        
        eventEmitter.once("session.id", onSessionId);
        
        
        eventEmitter.once("session.closing", function(reason){
            if(options.debug) {
                console.log("Session closing." + reason);
            }
        });
        
        
        if(options.debug) {
            console.log("Initialized " + this);
        }
    }
    
    
    function getConnectionURL() {
        return options.protocol +"://" + options.host + ":" + options.port + "/" + options.app;
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
        
        eventEmitter.emit("session.ready");
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
        
        startRMIRegistryMonitor();
        eventEmitter.emit("session.connected");
    }
    
    
    function onSocketMessage(evt){
        
        if(options.debug) {
            console.log("Message received " + JSON.stringify(evt.data));
        }
        
        handleMessage(evt.data);
    }
    
    
    function onSocketError(error){
        
        if(options.debug) {
            console.log("Socket error " + JSON.stringify(error));
        }
        
        eventEmitter.emit("session.error", error);
    }
    
    
    function onSocketClose(evt){
        
        connected = false;
        
        if(options.debug) {
            console.log("Socket closed");
        }
        
        stopRMIRegistryMonitor();
        eventEmitter.emit("session.closed");
    }
    
    
    
    function handleMessage(response) {
        
        var obj = JSON.parse(response);
        var messageId = obj.id;
        
        if(obj.hasOwnProperty("type")){
            if(obj["type"] === "EVENT"){         
                // handle event
                var evt = obj.data;
                eventEmitter.emit(evt.name, evt.data);
            }else if(obj["type"] === "RMI") {
                
                if(options.debug){
                    console.log("RMI response received");
                }
                
                // handle RMI response
                if(rmiPromises[messageId]){
                    var prom = rmiPromises[messageId];
                    if(obj["status"] === "DATA"){
                        prom.resolve(obj.data);
                    }else{
                        prom.reject(obj.data);
                    }
                    delete rmiPromises[messageId];
                }else{
                    throw new Error("No promise found for RMI response");
                }
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
    * Check if we have valid options for websocket connection to red5 app
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
                
                if(options.debug) {
                    console.log("processing " + param);
                }
            
                if(typeCheck('String', param)) 
                {
                    try 
                    {
                        JSON.parse(param);
                        processedParameters.push({value:param, type: "JSONObject"});
                    } 
                    catch (e) 
                    {
                        processedParameters.push({value:param, type: "String"});
                    }                 
                    
                }
                else if(typeCheck('Boolean', param)) 
                {   
                    processedParameters.push({value:param, type: "Boolean"});
                }
                else if(typeCheck('Number', param)) 
                {
                    var paramString = param.toString();
                    
                    if(paramString.indexOf(".") >= 0)
                    {
                        if(paramString.length >= 16)
                        {
                            processedParameters.push({value:param, type: "Double"});
                        }
                        else
                        {
                            processedParameters.push({value:param, type: "Float"});   
                        }
                    }
                    else
                    {
                        if(paramString.length >= 10)
                        {
                            processedParameters.push({value:param, type: "Long"});   
                        }
                        else
                        {
                            processedParameters.push({value:param, type: "Integer"});  
                        }
                    }                    
                }
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
        
         return new Promise(function(resolve, reject) {
            
            var requestId = request.id;
            
            //send message via websockets
            if (connected) 
            {
                
                // Storing reference to resolve and reject
                rmiPromises[requestId] = {
                    resolve: resolve,
                    reject: reject,
                    time: request.timestamp
                };
                
                if(options.debug){
                    console.log("Sending request " +  request);
                }
                
                ws.send(JSON.stringify(request));
            } 
            else 
            {
                reject('Websocket connection is closed');
            }
        });
    }
     
    
    
   
    /*
    * Invoke a method on server
    */
    function invoke() {
        
        if(arguments.length == 0) return;
        
        var args = Array.prototype.slice.call(arguments);        
        var method = args[0];
        var params = (args.length>1)?args.slice(1, args.length):[];
        
        var request = createRMIRequest(method, params);        
        return send(request);
    }
    
    
    
    
    
    
    
    /*******************
    * Event managment
    *******************/
    
    
    
    /*
    * Register a handler for event
    */
    function registerEventHandler(evt, handler) {
        eventEmitter.addListener(evt, handler);
    }
    
    
    
    /*
    * Register a handler for event just once
    */
    function registerOnceEventHandler(evt, handler) {
        eventEmitter.once(evt, handler);
    }
    
    
    
    /*
    * UnRegister a handler for event
    */
    function unregisterEventHandler(evt, handler) {
        eventEmitter.removeListener(evt, handler);
    }
    
    
    
    
    
    
    /*
    * Clear all handlers for event
    */
    function unregisterAllHandlers(evt) {
        eventEmitter.removeAllListeners(evt);        
    }
    
    
    
    
    return {
        invoke: invoke,
        getSessionId: getSessionId,
        on: registerEventHandler,
        off: unregisterEventHandler,
        onOnce:registerOnceEventHandler,
        offAll: unregisterAllHandlers,
        init: init
    };
    
}(window));


module.exports = red5JsBridgeObject;