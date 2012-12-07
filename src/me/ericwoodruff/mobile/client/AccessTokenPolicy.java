package me.ericwoodruff.mobile.client;

import twitter4j.auth.AccessToken;

public interface AccessTokenPolicy {
	@SuppressWarnings("serial")
	class AccessTokenException extends Exception {
		public AccessTokenException (String message) {
			super (message);
		}
		
		public AccessTokenException (String message, Exception cause) {
			super (message, cause);
		}
	}
	
	AccessToken getAccessToken () throws AccessTokenException;
}
