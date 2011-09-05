package com.lyricsfrenzy.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
public class SearchActivity extends Activity implements OnItemClickListener {
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private EditText searchBar;
	private Button searchButton;
	private ProgressDialog loadingDialog;
	private static ActionBar actionBar;
	private ToggleButton videoButton;
	private TextView videoText;
	public static boolean videoResults = false;
	private TextToSpeech ttsEng;
	private ListView mList;
	
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
        
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"burakdede87@gmail.com"});
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, "[LyricFrenzy] FeedBack");
        
        final Action infoAction = new IntentAction(this, mailIntent, R.drawable.email);
        actionBar.addAction(infoAction);
        
        actionBar.addAction(new TextToSpeechAction());
    }
    
    private class TextToSpeechAction implements Action,OnInitListener{

		@Override
		public int getDrawable() {
			// TODO Auto-generated method stub
			return R.drawable.mic;
		}

		@Override
		public void performAction(View view) {
			
			ttsEng = new TextToSpeech(getApplicationContext(), this);
	        // Check to see if a recognition activity is present
	        PackageManager pm = getPackageManager();
	        List<ResolveInfo> activities = pm.queryIntentActivities(
	                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	        Log.d(LYRIC_FRENZY, "Size of the acitivities : " + activities.size());
	        
	        if(activities.size()!=0){
	        	startVoiceRecognitionActivity();
	        }else{
	        	Toast.makeText(getApplicationContext(), "Seems like you dont have TextToSpeech recognizer",
	        			Toast.LENGTH_LONG).show();
	        }
		}
		

		@Override
		public void onInit(int status) {
			// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
	        if (status == TextToSpeech.SUCCESS) {
	            // Set preferred language to US english.
	            // Note that a language may not be available, and the result will indicate this.
	            int result = ttsEng.setLanguage(Locale.US);
	            // Try this someday for some interesting results.
	            // int result mTts.setLanguage(Locale.FRANCE);
	            if (result == TextToSpeech.LANG_MISSING_DATA ||
	                result == TextToSpeech.LANG_NOT_SUPPORTED) {
	               // Lanuage data is missing or the language is not supported.
	                Log.e(LYRIC_FRENZY, "Language is not available.");
	            } else {
	                // Check the documentation for other possible result codes.
	                // For example, the language may be available for the locale,
	                // but not for the specified country and variant.

	                // The TTS engine has been successfully initialized.
	                // Allow the user to press the button for the app to speak again.
	                // Greet the user.
	            	Log.e(LYRIC_FRENZY, "Language is available.");
	            }
	        } else {
	            // Initialization failed.
	            Log.e("SCORODROID", "Could not initialize TextToSpeech.");
	        }
		}
    }
    
    public static Intent createIntent(Context context) {

    	Intent i = new Intent(context, SearchActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
		loadingDialog = ProgressDialog.show(SearchActivity.this, "LyricFrenzy", "Getting search result. Please wait.",true);
    	loadingDialog.setCancelable(true);
    	
		return loadingDialog;
    }
    
    class GetMetadata extends AsyncTask<ArrayList<String>, Void, String>{

    	@Override
    	protected void onPreExecute() {
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
    
    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));
            mList.setOnItemClickListener(this);
           
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String selection = (String) mList.getAdapter().getItem((int)arg3);

		searchBar.setText(selection);
	}
    

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak in English for team name");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
}