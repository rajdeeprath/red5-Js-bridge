let Red5JsBridge = (function () {
    
let defaults = {port: 8081, protocol: "ws", host: "localhost", app: "wsendpoint", channel: "jsbridge", autoConnect: false, debug: true, rmiTimeout: 5000};

const EventEmitter = require('events');  
const Promise = require('promise');
const typeCheck = require('type-check').typeCheck;
    
class Red5JsBridge extends EventEmitter {
     
    
        constructor(opts) {
            
            super();
            
            this._options = {}, 
            this._sessionId = undefined, 
            this._connected = false, 
            this._ws = undefined,
            this._rmiPromises = {},
            this._messageCounter = 0; 
            
            
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
            this.on("session.id", function(id){
                if(this.options.debug) {
                    console.log("Setting session." + id);
                }
                
                this._sessionId = id;
                this.emit("bridge.ready", this.sessionId);
            });
            
            this.on("session.closing", function(reason){
                if(this.options.debug) {
                    console.log("Session closing." + reason);
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
        * Return options
        */
        get options() {
            return this._options;
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
                else if(obj["type"] === "RMI") 
                {
                    if(this.options.debug){
                        console.log("RMI response received");
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
                        throw new Error("No promise found for RMI response");
                    }
                }
            }
        }
    
    
    
    
    
        /*
        * handle websocket messages
        */
        _handleClose(data) {
            this._connected = false; 
        }
    
    
    
    
        /*
        * handle websocket connect
        */
        _handleConnect(evt){
            this._connected = true; 
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
                        this._ws = new WebSocket(Red5JsBridge.getConnectionURL(this.options), Red5JsBridge.getConnectionChannel(this.options));
                        
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

            //stopRMIRegistryMonitor();


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
    
    
    return Red5JsBridge;
})();



/*********************************************************************/


class Red5JsBridgedApplication extends Red5JsBridge {
    
    constructor(opts) {
        super(opts);
    }
    
}





/*
var bridge = new Red5JsBridge({debug: false});
bridge.on('bridge.ready', function(id){
    console.log("bridge - ready " + id);
    
    bridge.invoke('greet', "rajdeep")
    .then(function(result){
      console.log("result =>" + result);  
    })
    .catch(function(error){
      console.log("error =>" + error);  
    })
});
bridge.connect();
*/
    
    
    
var bridge = new Red5JsBridgedApplication({debug: false});
bridge.on('bridge.ready', function(id){
    console.log("bridge - ready " + id);
    
    bridge.invoke('greet', "rajdeep")
    .then(function(result){
      console.log("result =>" + result);  
    })
    .catch(function(error){
      console.log("error =>" + error);  
    })
});
bridge.connect();