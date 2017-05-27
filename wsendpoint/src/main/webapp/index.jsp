<html>

<script language="javascript" type="text/javascript">


function testWebSocket()
{
  websocket = new WebSocket("ws://localhost:8081/wsendpoint", "jsbridge");
  websocket.onopen = function(evt) { onOpen(evt) };
  websocket.onclose = function(evt) { onClose(evt) };
  websocket.onmessage = function(evt) { onMessage(evt) };
  websocket.onerror = function(evt) { onError(evt) };
}


function onOpen(evt)
{
  console.log("onOpen");
}

function onClose(evt)
{
	console.log("onClose");
}

function onMessage(evt)
{
	console.log("onMessage");
	console.log(JSON.stringify(evt.data));
}

function onError(evt)
{
	console.log("onError");
}


window.addEventListener("load", testWebSocket, false);

</script>


<body>
<h2>Red5 JS Bridge!</h2>
</body>
</html>
