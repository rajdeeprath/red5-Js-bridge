/**
* Red5 js bridge library
**/

var red5JsBridgeObject = (function (window) {
   
    'use strict';
    
    
    var ws, evtHandlers = {}, rmiPromises = {};
    
    
    
    /*
    * Initialize module
    */
    
    
    
    
    /*
    * Websocket events
    */
    
    
    
    
    function preprocess(parameters) {
        
        var processedParameters = [];
        
        parameters.forEach(function (param) {
           // TO DO 
        });
        
        
        return processedParameters;
    }
    
    
    
    
    function generateUniqueId() {
        
        return "12345";
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
        invoke: invoke,
        on: registerEventHandler,
        off: unregisterEventHandler,
        offAll: unregisterAllHandlers
    };
    
}(window));