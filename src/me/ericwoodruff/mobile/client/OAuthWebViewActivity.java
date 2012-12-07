package me.ericwoodruff.mobile.client;

import java.io.Serializable;

import me.ericwoodruff.mobile.background.SpinnerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import me.ericwoodruff.mobile.R;

/**
 * Note the activity needs to register <data android:scheme="http" android:host="android.callback.ericwoodruff.me" />
 * or whatever R.string.twitter_callback_url is
 */
public abstract class OAuthWebViewActivity extends Activity {
	protected abstract String getCallbackURL ();
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.oauth_webview);
		
		intent = getIntent ();

		final WebView webView = (WebView) findViewById (R.id.webview);
		
		webView.setWebViewClient (new WebViewClient () {
			@Override
			public boolean shouldOverrideUrlLoading (WebView view, String url) {
				if (url.contains (getCallbackURL ())) {
					Uri uri = Uri.parse (url);
					String oauthVerifier = uri.getQueryParameter ("oauth_verifier");
					handleOAuthVerifier (oauthVerifier);
					return true;
				}
				return false;
			}
		});
		
		
		new SpinnerTask<Object, Integer, Object> (this) {
			@Override
			protected Object run (Object... params) throws Exception {
				Log.d (TAG, "fetchRequestToken");
				return fetchRequestToken (getCallbackURL ());
			}
			
			protected void onException (Exception e) {
				Log.d (TAG, "fetchRequestToken - exception", e);
				Toast.makeText (getApplicationContext (), R.string.authentication_failure, Toast.LENGTH_LONG).show ();
				finish ();
			}
			
			protected void onSuccess (Object requestToken) {
				Log.d (TAG, "fetchRequestToken - success: " + requestToken.toString ());
				webView.loadUrl (getAuthenticationURL (requestToken));
			}
		}.execute ();
	}
	
	protected abstract String getAuthenticationURL (Object requestToken);
	
	protected void handleOAuthVerifier (final String oauthVerifier) {
		if (null == oauthVerifier) {
			setResult (RESULT_CANCELED, intent);
			finish ();
			return;
		}
		
		new SpinnerTask<Object, Integer, Serializable> (this) {
			@Override
			protected Serializable run (Object... params) throws Exception {
				Log.d (TAG, "fetchAccessToken");
				return fetchAccessToken (oauthVerifier);
			}
			
			protected void onException (Exception e) {
				Log.d (TAG, "fetchAccessToken - exception", e);
				intent.putExtra ("exception", e);
				setResult (RESULT_CANCELED, intent);
			}
			
			protected void onSuccess (Serializable result) {
				Log.d (TAG, "fetchAccessToken - success: " + result.toString ());
				intent.putExtra ("token", result);
				setResult (RESULT_OK, intent);
			}
			
			protected void onPostExecute(Serializable result) {
				super.onPostExecute (result);
				finish ();
			}
		}.execute ();
	}
	
	protected abstract Serializable fetchRequestToken (String callbackURL) throws Exception;
	
	protected abstract Serializable fetchAccessToken (String oauthVerifier) throws Exception;

	protected Intent intent;
	
	private static final String TAG = "OAuthWebViewActivity";
}
