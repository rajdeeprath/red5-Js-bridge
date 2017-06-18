package com.flashvisions.server.red5.jsbridge.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
	ElementType.METHOD,
	ElementType.TYPE
})

@Retention(RetentionPolicy.RUNTIME)
public @interface Invocable {
	
	/**
	 * Host of originating invocation
	 * @return
	 */
	String host() default "0.0.0.0";
	
	
	
	/**
	 * RemoteAddress of originating invocation
	 * @return
	 */
	String remoteAddress() default "0.0.0.0";

}
