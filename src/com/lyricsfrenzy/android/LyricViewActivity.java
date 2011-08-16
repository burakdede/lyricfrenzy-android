package com.lyricsfrenzy.android;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class LyricViewActivity extends Activity{

	private WebView lyricView;
	private TextView trackInfo;
	private ImageView albumImg;
	private Gallery videoGalery;
	private Spinner translateButton;
	private boolean check = false;
	
	/*
	 * twitter integration for sharing and authentication
	 */
	RequestToken requestToken;
	public final static String consumerKey = "qAAGJN5BWr7Gj0n27ukl6w"; // "your key here";
	public final static String consumerSecret = "nSQgqTQwrd1N5MNMvbHbveiN25PuEF6owf85l0pa28"; // "your secret key here";
	private final String CALLBACKURL = "T4JOAuth://main";  //Callback URL that tells the WebView to load this activity when it finishes with twitter.com. (see manifest)
	Twitter twitter;
	/*
	 * end of the twitter integration
	 */
	
	
	private String langTo;
	private String langNow = "en";
	
	
	private TextView up;
	private TextView down;
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";	
	private ProgressDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lyric);
	
		lyricView = (WebView) this.findViewById(R.id.lyricview);
		
		//Log.d(LYRIC_FRENZY, MusixMatchData.tempLyric.getLyric_body());
		lyricView.loadData("<font color='white'>"+Html.toHtml(new SpannableString( MusixMatchData.tempLyric.getLyric_body()))+"</font>", "text/html", "utf-8");
		
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
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	public void OAuthLogin(){
		
		try {
			
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			requestToken = twitter.getOAuthRequestToken(CALLBACKURL);
			String authUrl = requestToken.getAuthenticationURL();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(authUrl)));
			
		} catch (TwitterException ex) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("in Main.OAuthLogin", ex.getMessage());
		}	
	}
	
	
	
	/*
	 * - Called when WebView calls your activity back.(This happens when the user has finished signing in)
	 * - Extracts the verifier from the URI received
	 * - Extracts the token and secret from the URL 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		try {
			String verifier = uri.getQueryParameter("oauth_verifier");
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
					verifier);
			String token = accessToken.getToken(), secret = accessToken
					.getTokenSecret();
			displayTimeLine(token, secret); //after everything, display the first tweet 

		} catch (TwitterException ex) {
			Log.e("Main.onNewIntent", "" + ex.getMessage());
		}

	}
	
	
	
	/*
	 * Displays the timeline's first tweet in a Toast
	 */
	
	void displayTimeLine(String token, String secret) {
		if (null != token && null != secret) {
			List<Status> listStatus;
			try {
				AccessToken accessToken = new AccessToken(token, secret);
				twitter.setOAuthAccessToken(accessToken);
				Status status = twitter.updateStatus("@lyricfrenzy I've found "+ShowResultListActivity.data.getTrack_name()+" by "+ShowResultListActivity.data.getArtist_name()+
						" with #lyricfrenzy");
				listStatus = twitter.getFriendsTimeline();
				Toast.makeText(this,"Updated your status to "+"\""+listStatus.get(0).getText()+"\"", Toast.LENGTH_LONG)
					.show();
			} catch (Exception ex) {
				Toast.makeText(this, "Error:" + ex.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d("Main.displayTimeline",""+ex.getMessage());
			}
			
		} else {
			Toast.makeText(this, "Not Verified", Toast.LENGTH_LONG).show();
		}
	}
	
	
	/*
	 * make menu for user operations
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.lyric_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		case R.id.tweet:
			OAuthLogin();
			break;
		default:
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}

	
    @Override
    protected Dialog onCreateDialog(int id) {
    	// TODO Auto-generated method stub
		loadingDialog = ProgressDialog.show(LyricViewActivity.this, "", "Translating lyrics...",true);
		loadingDialog.setCancelable(true);
		
		return loadingDialog;
    }
	
	public class TranslateTask extends AsyncTask<String, Void, String>{

		String translation;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showDialog(1);
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			translation = MusixMatchData.translateLyrics(MusixMatchData.tempLyric.getLyric_body(), langNow, langTo);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated constructor stub
			this.context = c;

			TypedArray a = obtainStyledAttributes(R.styleable.galeryTheme);
            itemBackground = a.getResourceId(
                R.styleable.galeryTheme_android_galleryItemBackground, 0);
            a.recycle();
            
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return MusixMatchData.actualImages.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			 ImageView imageView = new ImageView(context);
	         imageView.setImageBitmap(MusixMatchData.actualImages.get(position));
	         imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	         imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
	         imageView.setBackgroundResource(itemBackground);
	         
	         return imageView;
		}	
	}
}
