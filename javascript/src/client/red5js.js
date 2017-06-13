const red5Js = (function (window) {
    
const defaults = {port: 8081, protocol: "ws", host: "localhost", app: "wsendpoint", channel: "jsbridge", autoConnect: false, debug: true, rmiTimeout: 5000};

const EventEmitter = require('events');  
const typeCheck = require('type-check').typeCheck;  
const W3CWebSocket = require('websocket').w3cwebsocket;
    
class Red5JsBridge extends EventEmitter {
     
    
        constructor(opts) {
            
            super();
            
            this._options = {}, 
            this._sessionId = undefined, 
            this._appScope = undefined,
            this._connected = false, 
            this._ws = undefined,
            this._rmiPromises = {},
            this._messageCounter = 0,
            this._messageSweeper=undefined,
            this._messageSweepInterval=30000; 
            this._messageDecayTime=6000; 
            
            
            if(window) {
                this._supportsWebSockets = 'WebSocket' in window || 'MozWebSocket' in window;
            }else {
                this._supportsWebSockets = false;
            }

            
            
            /* Copying defaults */
            console.log("defaults : " + defaults);
            for (var j in defaults) {
               this._options[j]=defaults[j];
            }
            
            
            /* Copying custom */
            if (opts) {
                for (var k in opts) {
                    this._options[k]=opts[k];
                }
            } 

            
            /* Listen for events */
            this.on("session.id", function(obj){
                if(this.options.debug) {
                    console.log("Setting session." + obj.sessionId);
                }
                
                this._sessionId = obj.sessionId;
                this._appScope = obj.scope;
                
                this.emit("bridge.ready", obj);
            });
            
            this.on("session.closing", function(reason){
                if(this.options.debug) {
                    console.log("Session closing." + reason);
                    this.emit("bridge.closing", reason);
                }
            });
            
            
            
            /* Initialize complete */
            if(this.options.debug) {
                console.log("Initialized " + this);
            }
            
            
            /* Initialize socket connection */
            if(this.options.autoConnect === true) {
                connect();
            }
        }
    
    
    
    
    
    /*
    * Check if value is numeric
    */

    static isNumeric(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    } 




    /*
    * Get connection URL
    */
    
    static getConnectionURL(opts) {
        return opts.protocol +"://" + opts.host + ":" + opts.port + "/" + opts.app;
    }
    
    
    
    
    
    /*
    * Get connection URL
    */
    
    static getConnectionChannel(opts) {
        return opts.channel;
    }   
    
    
    
    
    
    /*
    * Check if we have valid options for websocket connection to red5 app
    */

    static isValidOptions(opts)
    {    
        var valid = true;
        if(opts)
        {
            if(!opts.port || !Red5JsBridge.isNumeric(opts.port))
            {
               valid = false; 
            }
            else if(opts.protocol !== "ws" && opts.protocol !== "wss" )
            {
               valid = false; 
            }
            else if(!opts.app)
            {
               valid = false; 
            }
            else if(!opts.host)
            {
               valid = false; 
            }
            else if(!opts.channel)
            {
               valid = false; 
            }
        }

        return valid;
    }
    
    
    
    
        /*
        * Get Current Session Id
        */
        get sessionId() {
            return this._sessionId;
        }
    
    
    
    
        /*
        * Get application scope
        */
        get appScope() {
            return this._appScope;
        }
    
    
    
    
        /*
        * Return options
        */
        get options() {
            return this._options;
        }
    
    
    
    
        /*
        * Clean message register
        */
        _cleanMessageRegister(){
            
            let now = new Date().getTime();
            let obj = undefined;
            let rmi_time = undefined;
            let rmi_reject = undefined;
            
            for(var requestId in this._rmiPromises){
                
                /* Get the object */
                obj = this._rmiPromises[requestId];
                rmi_time = obj['time'];
                rmi_reject = obj['reject'];
                
                
                /* If RMI call is dead */
                if(now - rmi_time > this._messageDecayTime){
                    delete this._rmiPromises[requestId];
                    
                    if(rmi_reject){
                        rmi_reject("rmi timeout for requestId " + requestId);
                    }
                }
            }
        }
    
    
    
    
    
        /*
        * Start message sweeper
        */
        _startRMIIndexCleaner(){
            this._stopRMIIndexCleaner();
            this._rmiSweeper = setInterval(this._cleanMessageRegister, this._messageSweepInterval);
        }
    
    
    
    
        /*
        * Stop message sweeper
        */
        _stopRMIIndexCleaner(){
            if(this._messageSweeper){
                clearInterval(this._messageSweeper);
                _messageSweeper = undefined;
            }
        }
    
    
    
    
    
        /*
        * handle websocket messages
        */
        _handleMessage(data) {
            
            var obj = JSON.parse(data);
            var messageId = obj.id;
        
            if(obj.hasOwnProperty("type"))
            {
                if(obj["type"] === "EVENT")
                {
                    if(this.options.debug){
                        console.log("Event received");
                    }
                    
                    // handle event
                    var evt = obj.data;
                    this.emit(evt.name, evt.data);
                }
                else if(obj["type"] === "RMI" || obj["type"] === "API") 
                {
                    if(this.options.debug){
                        console.log(obj["type"] + " response received");
                    }

                    // handle RMI response
                    if(this._rmiPromises[messageId])
                    {
                        var promise = this._rmiPromises[messageId];
                        if(obj["status"] === "DATA")
                        {
                            promise.resolve(obj.data);
                        }
                        else
                        {
                            promise.reject(obj.data);
                        }
                        delete this._rmiPromises[messageId];
                    }
                    else
                    {
                        if(this.options.debug){
                            console.log("No promise found for response");
                        }
                    }
                }
            }
        }
    
    
    
    
    
        /*
        * handle websocket error
        */
        _handleError(err) {
            this.emit("bridge.error", err);
        }
    
    
    
    
    
        /*
        * handle websocket messages
        */
        _handleClose(data) {
            this._connected = false; 
            this._stopRMIIndexCleaner();
            this.emit("bridge.close", {sessionId: this._sessionId, scope: this._appScope});
        }
    
    
    
    
        /*
        * handle websocket connect
        */
        _handleConnect(evt){
            this._connected = true; 
            this._startRMIIndexCleaner();
        }    
    
    
    
    
    
        /*
        * Connect to server
        */
        connect() {
            var that = this;
            
            if(Red5JsBridge.isValidOptions(this.options))
            {
                
                if(!this._ws) 
                {
                    if(!this._connected)
                    {
                        if(this._supportsWebSockets) 
                        {
                            // For good browser
                            this._ws = new WebSocket(Red5JsBridge.getConnectionURL(this.options), Red5JsBridge.getConnectionChannel(this.options));
                        }
                        else
                        {
                            // For bad browser and nodejs
                            this._ws = new W3CWebSocket(Red5JsBridge.getConnectionURL(this.options), Red5JsBridge.getConnectionChannel(this.options));
                        }
                        
                        
                        this._ws.onopen = function(evt){
                            
                            if(that.options.debug) {
                                console.log("onopen " + that);
                            }
                            
                            that._handleConnect(evt);
                            
                        };
                        this._ws.onmessage = function(evt){
                            
                            if(that.options.debug) {
                                console.log("onmessage " + evt.data);
                            }
                            
                            that._handleMessage(evt.data);
                            
                        };
                        this._ws.onerror = function(err){
                            
                            if(that.options.debug) {
                                console.log("onerror " + err);
                            }  
                            
                            that._handleError(err);
                        };
                        this._ws.onclose = function(evt){
                            
                            if(that.options.debug) {
                                console.log("onclose " + that);
                            }
                            
                            that._handleClose(evt);
                        };
                    }
                    else
                    {
                        throw new Error("Websocket already connected");
                    }
                }
                else
                {
                    throw new Error("Websocket was already initialized");
                }
            }
            else
            {
                throw new Error("Invalid options for websocket connection");
            }
        }
    
    
    
        
    
        /*
        * Identify parameter types smartly and package them for sending over the bridge
        */
        static preprocess(parameters) {
            var processedParameters = [];

            parameters.forEach(function (param) {

                    if(typeCheck('String', param)) 
                    {
                        if(param.startsWith("{")) 
                        {
                            try 
                            {
                                JSON.parse(param);
                                processedParameters.push({value:param, type: "JSONObject"});
                            } 
                            catch (e) 
                            {
                                console.error("Invalid json string!");
                            }   
                        }
                        else
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
                    else
                    {
                        //throw new Error("Unknown parameter type");
                        processedParameters.push(param);
                    }
            });


            return processedParameters;
        }
    
    
    
    
        /*
        * Generate a unique id for RMI requests
        */
        _generateUniqueId() 
        {    
            if(this._messageCounter > Number.MAX_VALUE) {
                this._messageCounter = 0;
            }

            return this._sessionId + "-" + (this._messageCounter++);
        }





        /*
        * Create request packet for RMI call
        */
        _createRMIRequest(method, parameters) {
            var packet = {};
            packet.id = this._generateUniqueId();
            packet.timestamp = new Date().getTime();
            packet.type = "RMI";
            packet.method = method;
            packet.data = Red5JsBridge.preprocess(parameters);
            return packet;
        }
    
    
    
    
    
        /*
        * Raw send via websocket
        */
        _send(request) {
            
            var that = this;

             return new Promise(function(resolve, reject) {

                var requestId = request.id;

                if (that._connected) 
                {

                    // Storing reference to resolve and reject
                    that._rmiPromises[requestId] = {
                        resolve: resolve,
                        reject: reject,
                        time: request.timestamp
                    };

                    if(that.options.debug){
                        console.log("Sending request " +  request);
                    }
                    
                    that._ws.send(JSON.stringify(request));
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
        invoke() {

            if(arguments.length == 0) return;

            var args = Array.prototype.slice.call(arguments);        
            var method = args[0];
            var params = (args.length>1)?args.slice(1, args.length):[];

            var request = this._createRMIRequest(method, params);        
            return this._send(request);
        }
    
    
    
    
    
    
    
        /*
        * Destroy bridge
        */
        destroy(){

            if(this._ws) {           
                if(this._connected) {
                    this._ws.close();
                    this. ws.onopen = undefined;
                    this._ws.onmessage = undefined;
                    this._ws.onerror = undefined;
                    this._ws.onclose = undefined;
                    this._ws = undefined
                    this._connected = false;
                }
            }


            if(this._rmiPromises) {
                for(var i in this._rmiPromises){
                    delete this._rmiPromises[i];
                }
            }
            this._rmiPromises = {};

            
            this.sessionId = undefined
        }
        
}
    
    
    
    
class Red5JsBridgedApplication extends Red5JsBridge {

    constructor(opts, handler) {
        super(opts);

        this._appHandler = undefined;

        if(handler){
            this._appHandler = handler;
        }


        this._handleApplicationAdpterEvents();               

    }
    
    
    
    
    
    
    /*
    * Create request packet for API call
    */
    _createAPIRequest(method, parameters) {
        var packet = {};
        packet.id = this._generateUniqueId();
        packet.timestamp = new Date().getTime();
        packet.type = "API";
        packet.method = method;
        packet.data = Red5JsBridge.preprocess(parameters);
        return packet;
    }
    
    
    
    
    
    /**
     * Convert a `Map` to a standard
     * JS object recursively.
     * 
     * @param {Map} map to convert.
     * @returns {Object} converted object.
     */
     _map_to_object(map) {
        const out = Object.create(null)
        map.forEach((value, key) => {
          if (value instanceof Map) {
            out[key] = _map_to_object(value)
          }
          else {
            out[key] = value
          }
        })
        return out
      }
    
    
    
    
    /*
    * Get all connections
    */
    getConnections() {
        var method = "getConnections";
        var parameters = [];
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }    
    
    
    
    
     /*
    * Get connection by sessionId
    */
    getConnection(sessionId) {
        this._validateParameters(arguments, ['String'], 1, 1);
        var parameters = [arguments[0]];
        var method = "getConnection";        
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }    
    
    
    
    
    /*
    * Get connection statistics
    */
    getConnectionStatistics(connection){
        this._validateParameters(arguments, ['{sessionId: String, ...}'], 1, 1);
        var parameters = [arguments[0].sessionId];
        var method = "getConnectionStatistics";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get connection attributes
    */
    getAtrributes(connection) {
        this._validateParameters(arguments, ['{sessionId: String, ...}'], 1, 1);
        var parameters = [arguments[0].sessionId];
        var method = "getAtrributes";        
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get connection attribute
    */
    getAtrribute(connection, key) {
        this._validateParameters(arguments, ['{sessionId: String, ...}', 'String'], 2, 2);
        var parameters = [arguments[0].sessionId, arguments[1]];
        var method = "getAtrribute";        
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Set connection attribute
    */
    addAtrribute(connection, key, value) {
        this._validateParameters(arguments, ['{sessionId: String, ...}', 'String', 'Number | String | Boolean'], 3, 3);
        var parameters = [arguments[0].sessionId, arguments[1], arguments[2]];
        var method = "addAtrribute";        
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Set connection attributes
    */
    addAtrributes(connection, map) {
        this._validateParameters(arguments, ['{sessionId: String, ...}', 'Map'], 2, 2);
        var parameters = [arguments[0].sessionId, {type:"Map", value: this._map_to_object(arguments[1])}];
        var method = "addAtrributes";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    
    

    
    
    
    /*
    * Disconnect connection
    */
    disconnect(connection) {
        this._validateParameters(arguments, ['{sessionId: String, ...}'], 1, 1);
        var parameters = [arguments[0].sessionId];
        var method = "disconnect";        
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Ping connection
    */
    ping(connection) {
        this._validateParameters(arguments, ['{sessionId: String, ...}'], 1, 1);
        var parameters = [arguments[0].sessionId];
        var method = "ping";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
     /*
    * Get child scope names inside a scope
    */
    getChildScopeNames(){
        var method = "getChildScopeNames";
        var parameters = [];
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get application scope
    */
    getApplicationScope() {
        var method = "getApplicationScope";
        var parameters = [];
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get scope object for path
    */
    getScope(path) {
        this._validateParameters(arguments, ['String'], 1, 1);
        var parameters = [arguments[0]];
        var method = "getScope";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    /*
    * Get root scope object for a given scope
    */
    getRootScope(scope) {
        this._validateParameters(arguments, ['{name: String, path: String}'], 1, 1);
        var method = "getRootScope";
        var parameters = [this._getFullScopePath(arguments[0])];
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }

    
    
    
    /*
    * Check if broadcast stream exists at a particular scope
    */
    hasBroadcastStream(name, scope=undefined) {
        
        this._validateParameters(arguments, ['String', '{name: String, path: String}'], 1, 2);
        
        var parameters;
        if(!scope){
            parameters = [arguments[0]];
        }else{
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "hasBroadcastStream";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }  
    
    
    
    
    /*
    * Get broadcast stream
    */
    getBroadcastStream(name, scope=undefined) {
        
        this._validateParameters(arguments, ['String', '{name: String, path: String}'], 1, 2);
        
        var parameters;
        if(!scope){
            parameters = [arguments[0]];
        }else{
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "getBroadcastStream";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get all broadcast streams in a scope
    */
    getBroadcastStreamNames(scope=undefined) {
        
        var parameters;
        if(!scope){
            parameters = [];
        }else{
            this._validateParameters(arguments, ['{name: String, path: String}'], 1, 1);
            parameters = [this._getFullScopePath(arguments[0])];    
        }
        var method = "getBroadcastStreamNames";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Record a stream
    */
    recordStart(name, scope=undefined, saveAs=name, overWrite=true) {
        
        this._validateParameters(arguments, ['String', '{name: String, path: String}', 'String', 'Boolean'], 4, 4);
        
        var parameters = [arguments[0], arguments[1], arguments[2], arguments[3]];
        var method = "recordStart";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    
    /*
    * Stop Record of a stream
    */
    recordStop(name, scope=undefined) {
        
        var parameters;
        if(!scope){
            this._validateParameters(arguments, ['String'], 1, 1);
            parameters = [arguments[0]];
        }else{
            this._validateParameters(arguments, ['String', '{name: String, path: String}'], 2, 2);
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "recordStop";        
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Close stream
    */
    closeStream(name, scope=undefined) {
        
        var parameters;
        if(!scope){
            this._validateParameters(arguments, ['String'], 1, 1);
            parameters = [arguments[0]];
        }else{
            this._validateParameters(arguments, ['String', '{name: String, path: String}'], 2, 2);
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "closeStream";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Check if stream is recording
    */
    isRecording(name, scope=undefined){
        
        var parameters;
        if(!scope){
            this._validateParameters(arguments, ['String'], 1, 1);
            parameters = [arguments[0]];
        }else{
            this._validateParameters(arguments, ['String', '{name: String, path: String}'], 2, 2);
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "isRecording";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get connection of stream
    */
    getStreamConnection(name, scope=undefined){
        
        var parameters;
        if(!scope){
            this._validateParameters(arguments, ['String'], 1, 1);
            parameters = [arguments[0]];
        }else{
            this._validateParameters(arguments, ['String', '{name: String, path: String}'], 2, 2);
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "getStreamConnection";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    
    /*
    * Get stream stats
    */
    getStreamStatistics(name, scope=undefined){
        
        var parameters;
        if(!scope){
            this._validateParameters(arguments, ['String'], 1, 1);
            parameters = [arguments[0]];
        }else{
            this._validateParameters(arguments, ['String', '{name: String, path: String}'], 2, 2);
            parameters = [arguments[0], this._getFullScopePath(arguments[1])];    
        }
        
        var method = "getStreamStatistics";
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Get stream length
    */
    getStreamLength(name) {
        
        this._validateParameters(arguments, ['String'], 1, 1);
        var method = "getStreamLength";
        var parameters = [arguments[0]];
        var request = this._createAPIRequest(method, parameters);
        return this._send(request);
    }
    
    
    
    
    /*
    * Validate parameters for overloaded function
    */
    _validateParameters(params, allowedTypes, minParamCount, maxParamCount){
        
        if(params.length < minParamCount){
            throw new Error("Minimum of " + minParamCount + " parameters required.");
        }
        
        
        for(let i=0;i < maxParamCount; i++) {   
            var p = params[i];
            var t = allowedTypes[i];
            
            if(!t){
                continue;
            }
            
            if(t === 'Map'){
                // DO SPECIAL Check later
            }
            
             // Check if param is of type t => { String | Number | Boolean }
            if(!typeCheck(t, p)) {
                throw new Error("Unexpected param type.");
            }
        }
    }
    
    
    
    
    
    /* Returns full scope path with name */
    _getFullScopePath(scope){
        return scope.path + "/" + scope.name;
    }


    
    
    /* Returns Delayed Promnise */
    delayPromise(ms) {
      return function(x) {
        return new Promise(resolve => setTimeout(() => resolve(x), ms));
      };
    }

    

    /*
    * Handling application adapter events
    */
    _handleApplicationAdpterEvents() {
        
        const appHandler = this._appHandler;
        const that = this;
        
        /* Listen for application events */
        this.on("application.appStart", function(data){
            if(this.options.debug) {
                console.log("Application started.");
            }
            
            const scope = data;
            
            if(appHandler) {
                const fn = appHandler.appStart;
                if(typeof fn === 'function') {
                    fn.call(appHandler, scope);
                } 
            }
        });

        this.on("application.appConnect", function(data){
            if(that.options.debug) {
                console.log("appConnect");
            }
            
            const connection = data.connection;
            const params = data.params;
            
            if(appHandler) {
                const fn = appHandler.appConnect;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, params);
                } 
            }
        });

        this.on("application.appJoin", function(data){
            if(that.options.debug) {
                console.log("appJoin");
            }
            
            const connection = data.connection;
            const scope = data.scope;
            
            if(appHandler) {
                const fn = appHandler.appJoin;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, scope);
                } 
            }
        });


        this.on("application.appDisconnect", function(data){
            if(that.options.debug) {
                console.log("appDisconnect");
            }
            
            const connection = data;
            
            if(appHandler) {
                const fn = appHandler.appDisconnect;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection);
                } 
            }
        });
        

        this.on("application.appLeave", function(data){
            if(that.options.debug) {
                console.log("appLeave");
            }
            
            const connection = data.connection;
            const scope = data.scope;
            
            if(appHandler) {
                const fn = appHandler.appLeave;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, scope);
                } 
            }
        });


        this.on("application.appStop", function(data){
            if(that.options.debug) {
                console.log("appStop");
            }
            
            const scope = data;
            
            if(appHandler) {
                const fn = appHandler.appStop;
                if(typeof fn === 'function') {
                    fn.call(appHandler, scope);
                } 
            }
        });


        this.on("application.roomStart", function(data){
            if(that.options.debug) {
                console.log("roomStart");
            }
            
            const scope = data;            
            
            if(appHandler) {
                const fn = appHandler.roomStart;
                if(typeof fn === 'function') {
                    fn.call(appHandler, scope);
                } 
            }
        });


        this.on("application.roomConnect", function(data){
            if(that.options.debug) {
                console.log("roomConnect");
            }
            
            const connection = data.connection;
            const params = data.params;
            
            if(appHandler) {
                const fn = appHandler.roomConnect;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, params);
                } 
            }
        });


        this.on("application.roomJoin", function(data){
            if(that.options.debug) {
                console.log("roomJoin");
            }
            
            const connection = data.connection;
            const scope = data.scope;
            
            if(appHandler) {
                const fn = appHandler.roomJoin;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, scope);
                } 
            }
        });


        this.on("application.roomDisconnect", function(data){
            if(that.options.debug) {
                console.log("roomDisconnect");
            }
            
            const connection = data;
            
            if(appHandler) {
                const fn = appHandler.roomDisconnect;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection);
                } 
            }
        });


        this.on("application.roomLeave", function(data){
            if(that.options.debug) {
                console.log("roomLeave");
            }            
            
            const connection = data.connection;
            const scope = data.scope;
            
            if(appHandler) {
                const fn = appHandler.roomLeave;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, scope);
                } 
            }
        });


        this.on("application.roomStop", function(data){
            if(that.options.debug) {
                console.log("roomStop");
            }
            
            const scope = data;
            
            if(appHandler) {
                const fn = appHandler.roomStop;
                if(typeof fn === 'function') {
                    fn.call(appHandler, scope);
                } 
            }
        });
        
        
        this.on("stream.publishStart", function(data){
            if(that.options.debug) {
                console.log("publishStart");
            }
            
            const connection = data.connection;
            const stream = data.stream;                
            
            if(appHandler) {
                const fn = appHandler.streamBroadcastStart;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, stream);
                } 
            }
        });
        
        
        this.on("stream.publishStop", function(data){
            if(that.options.debug) {
                console.log("publishStop");
            }
            
            const connection = data.connection;
            const stream = data.stream;            
            
            if(appHandler) {
                const fn = appHandler.streamBroadcastClose;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, stream);
                } 
            }
        });
        
        
        this.on("stream.subscribeStart", function(data){
            if(that.options.debug) {
                console.log("subscribeStart");
            }
            
            const connection = data.connection;
            const stream = data.stream;    
            
            if(appHandler) {
                const fn = appHandler.streamSubscriberStart;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, stream);
                } 
            }
        });
        
        
        this.on("stream.subscribeStop", function(data){
            if(that.options.debug) {
                console.log("subscribeStop");
            }
            
            const connection = data.connection;
            const stream = data.stream;    
            
            if(appHandler) {
                const fn = appHandler.streamSubscriberClose;
                if(typeof fn === 'function') {
                    fn.call(appHandler, connection, stream);
                } 
            }
        });
    }

}
    
    
    /* Module Export Object */
    
    const Red5JsModules = {
        Red5JsBridge : Red5JsBridge,
        Red5JsBridgedApplication : Red5JsBridgedApplication
    }; 
    
    return Red5JsModules;
                    
})(window);


/* Exporting this module for others */
module.exports = red5Js;