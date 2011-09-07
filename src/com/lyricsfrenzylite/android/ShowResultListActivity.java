package com.lyricsfrenzylite.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lyricsfrenzylite.android.R;
import com.lyricsfrenzylite.android.utilities.MusicMetadata;
import com.lyricsfrenzylite.android.utilities.MusixMatchData;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;


public class ShowResultListActivity extends ListActivity {
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";
	private ResultAdapter trackAdapter;
	private ListView songList;
	private ProgressDialog loadingDialog;
	private static ActionBar actionBar;
	private String lyric_id;
	public static MusicMetadata data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);

		setUpActionBar(actionBar);
		trackAdapter = new ResultAdapter(this, R.layout.rows,MusixMatchData.searchData);
		setListAdapter(trackAdapter);
		
		songList = getListView();
		
		if (MusixMatchData.searchData.isEmpty()) {
				
			MusixMatchData.searchData.add(new MusicMetadata("", "", "", "", "No Result Found", "", "", "Sorry"));			
				
		}else{
			songList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					
					Log.d(LYRIC_FRENZY, "On click...");
					data = (MusicMetadata) songList.getAdapter().getItem((int)arg3);
					lyric_id = data.getTrack_id();
					new ShowLyricTask().execute(null);
				}
			});
		}
		
	}
	
    public void setUpActionBar(ActionBar actionBar){
    	
    	//set actionbar intents and activities accordingly
        actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this),R.drawable.home));
    }
    
    public static Intent createIntent(Context context) {

    	Intent i = new Intent(context, SearchActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
	
    @Override
    protected Dialog onCreateDialog(int id) {
		loadingDialog = ProgressDialog.show(ShowResultListActivity.this, "", "Loading lyric of the song.",true);
		loadingDialog.setCancelable(true);
		
		return loadingDialog;
    }
    
	private class ShowLyricTask extends AsyncTask<ArrayList<String>, Void, String>{

		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		showDialog(1);
    	}
    	
		
		@Override
		protected String doInBackground(ArrayList<String>... params) {
			
			MusixMatchData.playerList.clear();
			MusixMatchData.actualImages.clear();
			MusixMatchData.thumbnailList.clear();
			MusixMatchData.lyricsGet(lyric_id);
			MusixMatchData.getArtistImage(data.getArtist_name());
			if(SearchActivity.videoResults){
				Log.d(LYRIC_FRENZY, "Getting videos from youtube");
				MusixMatchData.getSongVideos(data.getArtist_name()+" "+data.getTrack_name());
			}
				
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			trackAdapter.notifyDataSetChanged();
			loadingDialog.dismiss();
			/*
			 * show results in another activity
			 * after dialog dismissed
			 */
			Intent lyricIntent = new Intent();
			lyricIntent.setClass(getApplicationContext(), LyricViewActivity.class);
			startActivity(lyricIntent);
		}
	}
	
	
	private class ResultAdapter extends ArrayAdapter<MusicMetadata>{

		private ArrayList<MusicMetadata> musicData;
		
		public ResultAdapter(Context context,
				int textViewResourceId, List<MusicMetadata> objects) {
			
			super(context, textViewResourceId, objects);
			context = getContext();
			musicData = (ArrayList<MusicMetadata>) objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			
			if(v == null){
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.rows, null);
			}
			
			MusicMetadata track = musicData.get(position);
			
			if(track != null){
				
				TextView trackData = (TextView) v.findViewById(R.id.track);
				trackData.setText(track.getArtist_name()+" - "+track.getTrack_name());
			}
			
			return v;
		}
	}

}
