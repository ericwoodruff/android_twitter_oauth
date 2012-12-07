package me.ericwoodruff.mobile;

import java.util.List;

import me.ericwoodruff.mobile.background.SpinnerTask;
import me.ericwoodruff.mobile.client.SharedPreferencesAccessTokenPolicy;
import me.ericwoodruff.mobile.client.TwitterOAuthWebViewActivity;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import me.ericwoodruff.mobile.R;

public class ListTweetsActivity extends ListActivity {
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);

		preferencesTokenPolicy = new SharedPreferencesAccessTokenPolicy (this);
		createClient (true);
	}

	private boolean createClient (boolean refresh) {
		Twitter twitter = TwitterFactory.getSingleton ();
		AccessToken accessToken = preferencesTokenPolicy.getAccessToken ();
		
		if (null == accessToken) {
			Intent intent = new Intent (getApplicationContext (), TwitterOAuthWebViewActivity.class);
			intent.putExtra ("refresh", refresh);
			startActivityForResult (intent, 99);
			return false;
		}
		
		twitter.setOAuthAccessToken (accessToken);
		if (refresh) {
			refresh ();
		}
		return true;
	}
	
	private void invalidateToken () {
		preferencesTokenPolicy.setAccessToken (null);
		TwitterFactory.getSingleton ().setOAuthAccessToken (null);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (99 == requestCode) {
			switch (resultCode) {
				case RESULT_CANCELED: {
					Exception e = (Exception) data.getExtras ().get ("exception");
					if (null == e) {
						Toast.makeText (getApplicationContext (), R.string.authentication_cancelled, Toast.LENGTH_LONG).show ();
					} else {
						Toast.makeText (getApplicationContext (), R.string.authentication_failure, Toast.LENGTH_LONG).show ();
					}
					break;
				}
				case RESULT_OK: {
					AccessToken accessToken = (AccessToken) data.getExtras ().get ("token");
					boolean refresh = data.getExtras ().getBoolean ("refresh");
					preferencesTokenPolicy.setAccessToken (accessToken);
					TwitterFactory.getSingleton ().setOAuthAccessToken (accessToken);
					if (refresh) {
						refresh ();
					}
					break;
				}
			}
		}
	}
	
	private List<Status> listTweets () throws TwitterException {
		return null;
	}
	
	private void displayTweets (List<twitter4j.Status> results) {
		setListAdapter (new ArrayAdapter<Status> (ListTweetsActivity.this, android.R.layout.simple_list_item_1, results));
	}
	
	private void showRefreshError () {
		Toast.makeText (getApplicationContext (), R.string.refresh_failure, Toast.LENGTH_LONG).show ();
	}
	
	public static boolean shouldInvalidateToken (TwitterException e) {
		if (e.getCause ().getMessage ().contains ("No authentication challenges found")) {
			return true;
		}
		
		return false;
	}
	
	private void refresh () {
		new SpinnerTask<Object, Integer, List<twitter4j.Status>> (this) {
			protected List<twitter4j.Status> run (Object... params) throws TwitterException {
				return listTweets ();
			}
			
			protected void onSuccess (List<twitter4j.Status> tweets) {
				displayTweets (tweets);
			}
			
			protected void onException (Exception e) {
				if (e instanceof TwitterException) {
					if (shouldInvalidateToken ((TwitterException) e)) {
						invalidateToken ();
						createClient (true);
					}
				}
				showRefreshError ();
			}
		}.execute ();
	}
	
	private SharedPreferencesAccessTokenPolicy preferencesTokenPolicy;
}
