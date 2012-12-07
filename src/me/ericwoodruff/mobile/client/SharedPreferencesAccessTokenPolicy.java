package me.ericwoodruff.mobile.client;

import twitter4j.auth.AccessToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPreferencesAccessTokenPolicy implements AccessTokenPolicy {
	public SharedPreferencesAccessTokenPolicy (Context context) {
		this.context = context;
	}

	@Override
	public AccessToken getAccessToken () {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences (context);
		String tokenString = settings.getString (twitterAccessTokenKey, null);
		String secretString = settings.getString (twitterAccessTokenSecretKey, null);
		if (null == tokenString || null == secretString) {
			return null;
		}
		
		return new AccessToken (tokenString, secretString);
	}
	
	public void setAccessToken (AccessToken token) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences (context);
		SharedPreferences.Editor editor = settings.edit ();
		if (null != token) {
			editor.putString (twitterAccessTokenKey, token.getToken ());
			editor.putString (twitterAccessTokenSecretKey, token.getTokenSecret ());
		} else {
			editor.remove (twitterAccessTokenKey);
			editor.remove (twitterAccessTokenSecretKey);
		}
		editor.commit ();
	}

	private Context context;
	
	private static final String twitterAccessTokenSecretKey = "twitter_access_token_secret";
	private static final String twitterAccessTokenKey = "twitter_access_token";
}
