package com.lyricsfrenzy.android;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lyricsfrenzy.android.utilities.MusixMatchData;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class LyricViewActivity extends Activity{

	private WebView lyricView;
	private TextView trackInfo;
	private ImageView albumImg;
	private Gallery videoGalery;
	private Spinner translateButton;
	private boolean check = false;
	
	private String langTo;
	private String langNow = "en";
	
	
	private TextView up;
	private TextView down;
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";	
	private ProgressDialog loadingDialog;
	private static ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lyric);
		setUpActionBar(actionBar);
		
		lyricView = (WebView) this.findViewById(R.id.lyricview);
		
		//Log.d(LYRIC_FRENZY, MusixMatchData.tempLyric.getLyric_body());
		lyricView.loadData("<font color='black'>"+Html.toHtml(new SpannableString( MusixMatchData.tempLyric.getLyric_body()))+"</font>", "text/html", "utf-8");
		
		lyricView.getSettings().setJavaScriptEnabled(true);
		lyricView.getSettings().setSupportZoom(true);
		lyricView.getSettings().setBuiltInZoomControls(true);
		lyricView.getSettings().setDefaultFontSize(13);
		lyricView.setBackgroundColor(0);
		
		up = (TextView) findViewById(R.id.up_text);
		up.setText(MusixMatchData.thumbsup);
		
		down = (TextView) findViewById(R.id.down_text);
		down.setText(MusixMatchData.thumbsdown);
		
		trackInfo = (TextView) this.findViewById(R.id.trackInfo);
		trackInfo.setText(ShowResultListActivity.data.getArtist_name()+" - "+ShowResultListActivity.data.getTrack_name());

		albumImg = (ImageView) this.findViewById(R.id.albumImage);
		
			if(MusixMatchData.bmImg != null){
				albumImg.setImageBitmap(MusixMatchData.bmImg);
			}else{
				albumImg.setImageResource(R.drawable.noimage);
			}
			
		if(SearchActivity.videoResults){
			
			videoGalery = (Gallery) findViewById(R.id.ytubeVideo);
			
			if(MusixMatchData.actualImages.size()!=0){
				videoGalery.setAdapter(new ImageAdapter(this));
			}else{
				videoGalery.setBackgroundResource(R.drawable.novideo);
			}
		
			videoGalery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
				
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MusixMatchData.playerList.get((int) arg3))));
				}
			});
		}
		
		
		translateButton = (Spinner) findViewById(R.id.translate);
		ArrayAdapter spinAdapter = ArrayAdapter.createFromResource(this, R.array.translateArray, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        translateButton.setAdapter(spinAdapter);
        translateButton.setSelection(-1);
        
        translateButton.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				if(check){
					switch (arg2) {
					case 0:
						langTo = "en";
						new TranslateTask().execute(null);
						break;
					case 1:
						langTo = "tr";
						new TranslateTask().execute(null);
						break;
					case 2:
						langTo = "fr";
						new TranslateTask().execute(null);
						break;
					case 3:
						langTo = "de";
						new TranslateTask().execute(null);
						break;
					case 4:
						langTo = "it";
						new TranslateTask().execute(null);
						break;
					
					default:
						break;
					}
				}
				
				check=true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
	}
	
	public void generateNoteOnSD(String sFileName, String sBody){
		
		
	    try{
	        File root = new File(Environment.getExternalStorageDirectory(), "LyricFrenzy");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	        Toast.makeText(this, "Saved lyric successfully to SD card.", Toast.LENGTH_SHORT).show();
	    }
	    catch(IOException e){
	         e.printStackTrace();
	    }
	}
	
	
    public void setUpActionBar(ActionBar actionBar){
    	
    	//set actionbar intents and activities accordingly
        actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this),R.drawable.home));
        actionBar.setTitle(ShowResultListActivity.data.getArtist_name()+" - "+ShowResultListActivity.data.getTrack_name());
        
        actionBar.addAction(new WriteSDCardAction());
    }
    
    public static Intent createIntent(Context context) {

    	Intent i = new Intent(context, SearchActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
    /*
     * write lyric as a text file to
     * user sd card
     */
    private class WriteSDCardAction implements Action{

		@Override
		public int getDrawable() {
			return R.drawable.download;
		}

		@Override
		public void performAction(View view) {
			if(Environment.getExternalStorageState() != null){
				generateNoteOnSD(ShowResultListActivity.data.getArtist_name()+" - "+ShowResultListActivity.data.getTrack_name()
					,MusixMatchData.tempLyric.getLyric_body());
			}else{
				Toast.makeText(getApplicationContext(), "Can not write SD card not mounted", Toast.LENGTH_LONG);
			}
		}
    }
	
    @Override
    protected Dialog onCreateDialog(int id) {
		loadingDialog = ProgressDialog.show(LyricViewActivity.this, "", "Translating lyrics...",true);
		loadingDialog.setCancelable(true);
		
		return loadingDialog;
    }
	
	public class TranslateTask extends AsyncTask<String, Void, String>{

		String translation;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(1);
		}
		
		@Override
		protected String doInBackground(String... params) {
			translation = MusixMatchData.translateLyrics(MusixMatchData.tempLyric.getLyric_body(), langNow, langTo);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			lyricView.loadData("<font color='white'>"+Html.toHtml(new SpannableString(translation))+"</font>", "text/html", "utf-8");
			loadingDialog.dismiss();
			langNow = langTo;
		}
	}
	
	
	public class ImageAdapter extends BaseAdapter{

		private Context context;
		private int itemBackground;
	
		
		public ImageAdapter(Context c) {
			this.context = c;

			TypedArray a = obtainStyledAttributes(R.styleable.galeryTheme);
            itemBackground = a.getResourceId(
                R.styleable.galeryTheme_android_galleryItemBackground, 0);
            a.recycle();
            
		}
		
		@Override
		public int getCount() {
			return MusixMatchData.actualImages.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			 ImageView imageView = new ImageView(context);
	         imageView.setImageBitmap(MusixMatchData.actualImages.get(position));
	         imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	         imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
	         imageView.setBackgroundResource(itemBackground);
	         
	         return imageView;
		}	
	}
}
