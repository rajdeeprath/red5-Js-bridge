let Red5JsBridge = (function () {
    
let defaults = {
    port: 8081,
    protocol: "ws",
    host: "localhost",
    app: "wsendpoint",
    channel: "jsbridge",
    autoConnect: false,
    debug: true,
    rmiTimeout: 5000
};


    
    
class Red5JsBridge {
     
    
        constructor(opts) {
            
            this.options = {}, 
            this.sessionId = undefined, 
            this._connected = false, 
            this.ws = undefined; 
            
            
            /* Copying defaults */
            console.log("defaults : " + defaults);
            for (var j in defaults) {
               this.options[j]=defaults[j];
            }
            
            
            /* Copying custom */
            if (opts) {
                for (var k in opts) {
                    this.options[k]=opts[k];
                }
            } 
            
            
            /* Initialize socket connection */
            if(this.options.autoConnect === true) {
                connect();
            }
            
            
            /* Initialize complete */
            if(this.options.debug) {
                console.log("Initialized " + this);
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
    
    static getConnectionURL(options) {
        return options.protocol +"://" + options.host + ":" + options.port + "/" + options.app;
    }
    
    
    
    
    
    /*
    * Get connection URL
    */
    
    static getConnectionChannel(options) {
        return options.channel;
    }   
    
    
    
    
    
    /*
    * Check if we have valid options for websocket connection to red5 app
    */

    static isValidOptions(options)
    {    
        var valid = true;
        if(options)
        {
            if(!options.port || !Red5JsBridge.isNumeric(options.port))
            {
               valid = false; 
            }
            else if(options.protocol !== "ws" && options.protocol !== "wss" )
            {
               valid = false; 
            }
            else if(!options.app)
            {
               valid = false; 
            }
            else if(!options.host)
            {
               valid = false; 
            }
            else if(!options.channel)
            {
               valid = false; 
            }
        }

        return valid;
    }
    
    

    
    
    
        /*
        * Session Id from server
        */
        setSessionId(id) {
            this.sessionId = id;
        }
    
    
    
    
    
        /*
        * Get Current Session Id
        */
        getSessionId() {
            return this.sessionId;
        }    
    
    
    
    
    
        /*
        * handle websocket messages
        */
        _handleMessage(data) {
            
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
                
                if(!this.ws) 
                {
                    if(!this._connected)
                    {
                        this.ws = new WebSocket(Red5JsBridge.getConnectionURL(this.options), Red5JsBridge.getConnectionChannel(this.options));
                        this.ws.onopen = function(evt){
                            
                            if(that.options.debug) {
                                console.log("onopen " + that);
                            }
                            
                            that._handleConnect(evt);
                            
                        };
                        this.ws.onmessage = function(evt){
                            
                            if(that.options.debug) {
                                console.log("onmessage " + evt.data);
                            }
                            
                            that._handleMessage(evt.data);
                            
                        };
                        this.ws.onerror = function(err){
                            
                            if(that.options.debug) {
                                console.log("onerror " + err);
                            }                            
                        };
                        this.ws.onclose = function(evt){
                            
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
    }
    
    
    
    return Red5JsBridge;
})();





var bridge = new Red5JsBridge();
bridge.connect();