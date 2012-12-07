package me.ericwoodruff.mobile.client;

import java.io.Serializable;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.os.Bundle;

import me.ericwoodruff.mobile.R;

/**
 * Note the activity needs to register <data android:scheme="http" android:host="android.callback.ericwoodruff.me" />
 * or whatever R.string.twitter_callback_url is
 */
public class TwitterOAuthWebViewActivity extends OAuthWebViewActivity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		twitter = TwitterFactory.getSingleton ();
		super.onCreate (savedInstanceState);
	}
	
	@Override
	protected String getCallbackURL () {
		return getResources ().getString (R.string.twitter_callback_url);
	}
	
	@Override
	protected String getAuthenticationURL (Object requestToken) {
		return ((RequestToken) requestToken).getAuthenticationURL ();
	}

	protected Serializable fetchRequestToken (String callbackURL) throws TwitterException {
		return twitter.getOAuthRequestToken (callbackURL);
	}
	
	protected Serializable fetchAccessToken (String oauthVerifier) throws TwitterException {
		return twitter.getOAuthAccessToken (oauthVerifier);
	}

	private Twitter twitter;
}
