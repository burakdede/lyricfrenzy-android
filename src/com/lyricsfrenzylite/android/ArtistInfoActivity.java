package com.lyricsfrenzylite.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.lyricsfrenzylite.android.R;
import com.lyricsfrenzylite.android.utilities.MusixMatchData;

public class ArtistInfoActivity extends Activity {

	private WebView artistView;
	private static String level = "ARTISTNAME";
	private String artistName;
	private ProgressDialog loadingDialog;
	private String pageUrl;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_info);
		artistView = (WebView) findViewById(R.id.artistView);
		Bundle extras = getIntent().getExtras(); 
		
		if(extras != null){
			
			artistName = extras.getString(level);
			Log.d(level, artistName);
			new GetMetadata().execute(null);
			
		}else{
			
			Toast makeToast = Toast.makeText(getApplicationContext(), "Problem occured.Can not get artist page.Sorry", Toast.LENGTH_LONG);
			makeToast.show();
		}
	}

	
	@Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
		loadingDialog = ProgressDialog.show(ArtistInfoActivity.this, "", "Loading.Please wait...\nGetting Artist Page",true);
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
			
			pageUrl = MusixMatchData.getArtistPage(artistName);
			Log.d("hebelek", "Getting PAge : " + pageUrl);
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d("hebelek", "Getting PAge : " + pageUrl);
			artistView.loadUrl(pageUrl);
			loadingDialog.dismiss();
			
		}
    }
}
