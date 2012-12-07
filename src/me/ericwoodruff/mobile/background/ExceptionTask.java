package me.ericwoodruff.mobile.background;

import android.os.AsyncTask;
import android.util.Log;

public abstract class ExceptionTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	protected void setException (Exception e) {
		this.exception = e;
	}
	
	public Exception getException () {
		return this.exception;
	}
	
	protected Result doInBackground (Params... params) {
		try {
			return this.run (params);
		} catch (Exception e) {
			this.setException (e);
			return null;
		}
	}
	
	protected abstract Result run (Params... params) throws Exception;
	
	protected void onPostExecute (Result result) {
		super.onPostExecute (result);
		if (null != getException ()) {
			Log.d ("ExceptionTask", "onPostExecute", getException ());
			onException (getException ());
		} else {
			onSuccess (result);
		}
	}
	
	protected void onSuccess (Result result) {
		
	}
	
	protected void onException (Exception e) {
		
	}
	
	private Exception exception;
}