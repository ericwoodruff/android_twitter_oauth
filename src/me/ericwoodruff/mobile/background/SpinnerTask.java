package me.ericwoodruff.mobile.background;

import android.app.ProgressDialog;
import android.content.Context;

public abstract class SpinnerTask<Params, Progress, Result> extends ExceptionTask<Params, Progress, Result> {
	public SpinnerTask (Context context) {
		this.context = context;
		spinner = new ProgressDialog (context);
	}
	
	@Override
	protected Result doInBackground (Params... params) {
		publishProgress ((Progress[]) null);
		return super.doInBackground (params);
	}
	
	@Override
	protected void onProgressUpdate (Progress... values) {
		super.onProgressUpdate (values);
		spinner = ProgressDialog.show (context, "Please wait...", "Loading...");
	}
	
	@Override
	protected void onPostExecute (Result result) {
		super.onPostExecute (result);
		spinner.cancel ();
	}
	
	private Context context;
	private ProgressDialog spinner;
}