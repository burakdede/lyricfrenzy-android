package com.lyricsfrenzy.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lyricsfrenzy.android.utilities.MusixMatchData;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

/**
 * 
 * @author burak
 * @since February 5 2011
 */
public class SearchActivity extends Activity {
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";
	
	private EditText searchBar;
	private Button searchButton;
	private ProgressDialog loadingDialog;
	private static ActionBar actionBar;
	private ToggleButton videoButton;
	private TextView videoText;
	public static boolean videoResults = false;
	
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		setUpActionBar(actionBar);
        
        searchBar = (EditText) findViewById(R.id.searchBar);
        searchBar.requestFocus();

        
        searchButton = (Button) findViewById(R.id.searchButton);
    
        /*
         *  do some checking and call another activity to show results
         */
        searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//check to see if editext is empty or not
				if(searchBar.getText().toString().length() != 0){
					
					Log.d(LYRIC_FRENZY, "Edit text is not empty calling another activity");
					//call another activity
					MusixMatchData.searchData.clear();
			        new GetMetadata().execute(null);
				}else{
					//show a little toast to user
					Log.d(LYRIC_FRENZY, "Showing toast message to user");
					Toast emptyBar = Toast.makeText(getApplicationContext(),getResources().getString(R.string.warn_empty_search), Toast.LENGTH_LONG);
					emptyBar.show();
				}
			}
		});
        
        videoText = (TextView) findViewById(R.id.videoState);
        videoButton = (ToggleButton) findViewById(R.id.videoToggle);
        videoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(isChecked){
					videoResults = true;
					videoText.setText("Videos On");
				}else{
					videoResults = false;
					videoText.setText("Videos Off");
				}
			}
		});
    }
    
    public void setUpActionBar(ActionBar actionBar){
    	
    	//set actionbar intents and activities accordingly
        actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this),R.drawable.home));
        final Action addAction = new IntentAction(this, SearchActivity.createIntent(this), R.drawable.process);
        actionBar.addAction(addAction);
        
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"burakdede87@gmail.com"});
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, "[LyricFrenzy] FeedBack");
        
        final Action infoAction = new IntentAction(this, mailIntent, R.drawable.email);
        actionBar.addAction(infoAction);
    }
    
    public static Intent createIntent(Context context) {

    	Intent i = new Intent(context, SearchActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub

		loadingDialog = ProgressDialog.show(SearchActivity.this, "LyricFrenzy", "Getting search result. Please wait.",true);
    	loadingDialog.setCancelable(true);
    	
		return loadingDialog;
    }
    
    class GetMetadata extends AsyncTask<ArrayList<String>, Void, String>{

    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		showDialog(1);
    	}
    	
		@Override
		protected String doInBackground(ArrayList<String>... params) {
			
			Log.d(LYRIC_FRENZY, "Getting data over network");
			MusixMatchData.artistSearch(searchBar.getText().toString().trim());
				
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			loadingDialog.dismiss();
			
			/*
			 * show results in another activity
			 * after dialog dismissed
			 */
			Log.d(LYRIC_FRENZY, "Now results calling another activity");
			Intent showResultIntent = new Intent();
			showResultIntent.setClass(getApplicationContext(), ShowResultListActivity.class);
			startActivity(showResultIntent);
		}
    }
}