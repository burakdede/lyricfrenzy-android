package com.lyricsfrenzy.android;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lyricsfrenzy.android.utilities.MusicMetadata;
import com.lyricsfrenzy.android.utilities.MusixMatchData;


public class ShowResultListActivity extends ListActivity {
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";
	private ResultAdapter trackAdapter;
	private ListView songList;
	private ProgressDialog loadingDialog;
	private String lyric_id;
	public static MusicMetadata data;
	private static String code = "CODE";
	private static String code2 = "CODE2";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.results);

		trackAdapter = new ResultAdapter(this, R.layout.rows,MusixMatchData.searchData);
		setListAdapter(trackAdapter);
		
		songList = getListView();
		
		Bundle extras = getIntent().getExtras(); 
		
		if(extras != null){
			
			String [] songData = extras.getStringArray(code);
			data = new MusicMetadata();
			Log.d(LYRIC_FRENZY, songData[0]);
			Log.d(LYRIC_FRENZY, songData[1]);
			data.setArtist_name(songData[0]);
			data.setTrack_name(songData[1]);
			
			lyric_id = extras.getString(code2);
			Log.d(LYRIC_FRENZY, lyric_id);
			new ShowLyricTask().execute(null);
			
		}else{
			if (MusixMatchData.searchData.isEmpty()) {
				
				MusixMatchData.searchData.add(new MusicMetadata("", "", "", "", "No Result Found", "", "", "Sorry"));			
				
			}else{
				songList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						Log.d(LYRIC_FRENZY, "On click...");
						 data = (MusicMetadata) songList.getAdapter().getItem((int)arg3);
						lyric_id = data.getTrack_id();
						
						new ShowLyricTask().execute(null);
					}
				});
			}
			
		}
		
	}
	
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
		loadingDialog = ProgressDialog.show(ShowResultListActivity.this, "", "Loading.Please wait...\nGetting videos may take some time",true);
		loadingDialog.setCancelable(true);
		
		return loadingDialog;
    }
    
	private class ShowLyricTask extends AsyncTask<ArrayList<String>, Void, String>{

		@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		showDialog(1);
    	}
    	
		@Override
		protected String doInBackground(ArrayList<String>... params) {
			// TODO Auto-generated method stub
			
			MusixMatchData.playerList.clear();
			MusixMatchData.actualImages.clear();
			MusixMatchData.thumbnailList.clear();
			MusixMatchData.lyricsGet(lyric_id);
			MusixMatchData.getArtistImage(data.getArtist_name());
			MusixMatchData.getSongVideos(data.getArtist_name()+" "+data.getTrack_name());
			
			return null;
		}
    	
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated constructor stub
			context = getContext();
			musicData = (ArrayList<MusicMetadata>) objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
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
