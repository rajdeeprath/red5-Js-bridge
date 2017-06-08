package com.flashvisions.server.red5.jsbridge.model;


public class IncomingMessage extends Message implements IIncomingMessage {
	
	private String method;
	
	public IncomingMessage()
	{
		super();
	}
	
	
	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IIncomingMessage#getMethod()
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/* (non-Javadoc)
	 * @see com.flashvisions.server.red5.jsbridge.model.IIncomingMessage#setMethod(java.lang.String)
	 */
	@Override
	public void setMethod(String method) {
		this.method = method;
	}
	
}
