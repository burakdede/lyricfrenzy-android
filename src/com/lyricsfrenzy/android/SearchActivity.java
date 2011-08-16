package com.lyricsfrenzy.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.lyricsfrenzy.android.utilities.MusixMatchData;

/**
 * 
 * @author burak
 * @since February 5 2011
 */
public class SearchActivity extends Activity {
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";
	private static String level = "ARTISTNAME";
	private static String code = "CODE";
	private static String code2 = "CODE2";
	
	private EditText searchBar;
	private Button searchButton;
	private TabHost tabs;
	private ProgressDialog loadingDialog;
	private ListView chartList;
	private ListView songList;
	private Spinner countrySpinner;
	private Spinner songCountrySpinner;
	private String countryCodeArtist = "gb";
	private String countryCodeSong = "gb";
	private ResultAdapter charListAdapter;
	private ResultAdapter songListAdapter;
	
	private boolean firstTimeChart= true;
	private boolean firstTimeLyric= true;
	private int whichTab = 1;
	
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tabs = (TabHost) findViewById(R.id.tabHost);
		tabs.setup();
		
		TabSpec spec1 = tabs.newTabSpec("Tab1");
		spec1.setIndicator("Search Lyric",getResources().getDrawable(R.drawable.music));
		spec1.setContent(R.id.lyrics);
		tabs.addTab(spec1);
		
		TabSpec spec2 = tabs.newTabSpec("Tab2");
		spec2.setIndicator("Top Artists",getResources().getDrawable(R.drawable.artist));
		spec2.setContent(R.id.charts);
		tabs.addTab(spec2);
		
		TabSpec spec3 = tabs.newTabSpec("Tab3");
		spec3.setIndicator("Top Lyrics",getResources().getDrawable(R.drawable.chart));
		spec3.setContent(R.id.topLyrics);
		tabs.addTab(spec3);
		
		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("Tab2")) {
					
					whichTab = 2;
					
					if(firstTimeChart){
						new GetMetadata().execute(null);
					}
					
					
				}else if(tabId.equals("Tab1")){
					
					whichTab = 1;
					
				}else if(tabId.equals("Tab3")){
					
					whichTab = 3;
					if(firstTimeLyric){
						new GetMetadata().execute(null);
					}
				}
			}
		});
		
        searchBar = (EditText) findViewById(R.id.searchBar);
        searchBar.requestFocus();

        songList = (ListView) findViewById(R.id.songList);
        songList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String artistName = (String) songList.getAdapter().getItem((int)arg3);
				String lyricId = MusixMatchData.lyricId.get((int) arg3);
				String [] parts = artistName.split("-");
				
				Log.d(LYRIC_FRENZY, "Part 1 : "+parts[0]+ "\nPart 2 :"+parts[1]);
				
				Intent myIntent = new Intent();
				myIntent.putExtra(code, parts);
				myIntent.putExtra(code2, lyricId);
				myIntent.setClass(getApplicationContext(), ShowResultListActivity.class);
				startActivity(myIntent);
			}
		});
        
        chartList = (ListView) findViewById(R.id.chartList);
        chartList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String artistName = (String) chartList.getAdapter().getItem((int)arg3);
				Log.d(LYRIC_FRENZY, artistName);
				
				//call to entry under title activity with intent
				Intent myIntent = new Intent();
				myIntent.putExtra(level, artistName);
				myIntent.setClass(getApplicationContext(),ArtistInfoActivity.class);
				startActivity(myIntent);
			}
		});
        
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
        
        countrySpinner = (Spinner) findViewById(R.id.countrySelector);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.countryArray));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
        
        songCountrySpinner = (Spinner) findViewById(R.id.songCountrySelector);
        songCountrySpinner.setAdapter(adapter);
        
        
        songCountrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

        	
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				MusixMatchData.songList.clear();
				if (!firstTimeLyric) {
					switch (arg2) {
					case 0:
						countryCodeSong ="gb";
						new GetMetadata().execute(null);
						break;
					case 1:
						countryCodeSong = "it";
						new GetMetadata().execute(null);
						break;
					case 2:
						countryCodeSong = "fr";
						new GetMetadata().execute(null);
						break;
					case 3:
						countryCodeSong = "nl";
						new GetMetadata().execute(null);
						break;
					case 4:
						countryCodeSong = "es";
						new GetMetadata().execute(null);
						break;
					case 5:
						countryCodeSong = "gr";
						new GetMetadata().execute(null);
						break;
					case 6:
						countryCodeSong = "us";
						new GetMetadata().execute(null);
						break;
						
					default:
						break;
					}
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				MusixMatchData.chartList.clear();
				if(!firstTimeChart){
					switch (arg2) {
					case 0:
						countryCodeArtist ="gb";
						new GetMetadata().execute(null);
						break;
					case 1:
						countryCodeArtist = "it";
						new GetMetadata().execute(null);
						break;
					case 2:
						countryCodeArtist = "fr";
						new GetMetadata().execute(null);
						break;
					case 3:
						countryCodeArtist = "nl";
						new GetMetadata().execute(null);
						break;
					case 4:
						countryCodeArtist = "es";
						new GetMetadata().execute(null);
						break;
					case 5:
						countryCodeArtist = "gr";
						new GetMetadata().execute(null);
						break;
					case 6:
						countryCodeArtist = "us";
						new GetMetadata().execute(null);
						break;
					default:
						break;
					}
					
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub

		loadingDialog = ProgressDialog.show(SearchActivity.this, "", "Loading.Please wait...",true);
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
				
				if(whichTab == 1){
					
					MusixMatchData.artistSearch(searchBar.getText().toString().trim());
					
				}else if (whichTab == 2){
				
					
					MusixMatchData.getLocaleCharts(countryCodeArtist);
					Log.d(LYRIC_FRENZY, "Calling chart call");
				}else if(whichTab == 3){
					
					MusixMatchData.getLocaleSongs(countryCodeSong);	
					Log.d(LYRIC_FRENZY, "Calling song chart call");
				}
				
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
			if(whichTab == 1){
				
				Log.d(LYRIC_FRENZY, "Now results calling another activity");
				Intent showResultIntent = new Intent();
				showResultIntent.setClass(getApplicationContext(), ShowResultListActivity.class);
				startActivity(showResultIntent);
				
			}else if(whichTab == 2){
				
				charListAdapter = new ResultAdapter(getApplicationContext(), R.layout.results, MusixMatchData.chartList);
				chartList.setAdapter(charListAdapter);
				firstTimeChart = false;
				charListAdapter.notifyDataSetChanged();
				
				
			}else if(whichTab == 3){
				
				songListAdapter = new ResultAdapter(getApplicationContext(), R.layout.results, MusixMatchData.songList);
				songList.setAdapter(songListAdapter);
				firstTimeLyric = false;
				songListAdapter.notifyDataSetChanged();
			}
			
		}
    }
    
    private class ResultAdapter extends ArrayAdapter<String>{

		private ArrayList<String> musicData;
		
		public ResultAdapter(Context context,
				int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			context = getContext();
			musicData = (ArrayList<String>) objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			
			if(v == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.rows, null);
			}
			
			String artistName = musicData.get(position);
			
			if(artistName != null){
				
				TextView trackData = (TextView) v.findViewById(R.id.track);
				trackData.setText(artistName);
			}
			
			return v;
		}
	}
}