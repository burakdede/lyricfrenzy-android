package com.lyricsfrenzylite.android.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class MusixMatchData {

	public static List<MusicMetadata> searchData = new ArrayList<MusicMetadata>();
	
	private final static String LYRIC_FRENZY = "Lyric Frenzy";

	//private final static String API_KEY = "d0f4f58572e24885311f6ab8b7ab62ce";
	private final static String API_KEY = "c1951c27717c99b7add9b2028e6c09b6";
	private final static String API_KEY_LASTFM = "49f6b21cab1c48100ee59f216645275e";
	private final static String API_KEY_GOOGLE = "AIzaSyBdozoMO9b-mxGAorOApLGD6c_D7ZBBfnI";
	
	public static Bitmap bmImg;
	public static String thumbsup;
	public static String thumbsdown;
	public static List<String> thumbnailList = new ArrayList<String>();
	public static List<Bitmap> actualImages = new ArrayList<Bitmap>();
	public static List<String> playerList = new ArrayList<String>();
	public static List<String> chartList  = new ArrayList<String>();
	public static List<String> songList = new ArrayList<String>();
	public static List<String> lyricId = new ArrayList<String>();
	
	
	private static String url  = "http://api.musixmatch.com/ws/1.1/";
	private static HttpClient httpClient;
	private static BasicResponseHandler handler;
	private static HttpGet httpGet;
	public static Lyric tempLyric = new Lyric();
	/*
	 * search the name of the artist
	 */
	public static synchronized void artistSearch(String artistName){
		
		String response = null;
		MusicMetadata tempTrack;
		
		url = "http://api.musixmatch.com/ws/1.1/track.search?apikey="+API_KEY+"&q_lyrics="+URLEncoder.encode(artistName)+"&format=json&page_size=50&f_has_lyrics=1";
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		
		httpGet = new HttpGet(url);
		
		try {
			response = httpClient.execute(httpGet,handler);
			
			Log.d(LYRIC_FRENZY, response);
			
			JSONObject musicData = new JSONObject(response);
			JSONObject message = musicData.getJSONObject("message");
			JSONObject header = message.getJSONObject("header");
			String status_code = header.getString("status_code");
			Log.d(LYRIC_FRENZY, "Status code :" + status_code);
			
			if(status_code.equalsIgnoreCase("200")){ //meaning success
				
				JSONObject body = message.getJSONObject("body");
				//JSONObject track_list = body.getJSONObject("track_list");
				JSONArray track_list = body.getJSONArray("track_list");
				
				int length = track_list.length();
				
				for (int i = 0; i < length ; i++) {
					
					JSONObject track = track_list.getJSONObject(i).getJSONObject("track");
					
					tempTrack = new MusicMetadata(track.getString("track_id"), track.getString("lyrics_id"), track.getString("track_mbid"), 
							track.getString("subtitle_id"), track.getString("track_name"), track.getString("artist_id"),
							track.getString("artist_mbid"), track.getString("artist_name"));
					
					searchData.add(tempTrack);
				}
			}
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * get the lyrics of the given song
	 */
	public static synchronized Lyric lyricsGet(String song_id){
		
		String response = null;
		url = "http://api.musixmatch.com/ws/1.1/track.lyrics.get?track_id="+URLEncoder.encode(song_id)+"&format=json&apikey="+API_KEY;
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		
		httpGet = new HttpGet(url);
		
		try {
			response = httpClient.execute(httpGet,handler);
			
			Log.d(LYRIC_FRENZY, response);
			
			JSONObject musicData = new JSONObject(response);
			JSONObject message = musicData.getJSONObject("message");
			JSONObject header = message.getJSONObject("header");
			String status_code = header.getString("status_code");
			
			if(status_code.equalsIgnoreCase("200")){ //meaning success
				
				JSONObject body = message.getJSONObject("body");
				//JSONObject track_list = body.getJSONObject("track_list");
				JSONObject lyrics = body.getJSONObject("lyrics");
				
				tempLyric.setLyric_body(lyrics.getString("lyrics_body"));
				tempLyric.setLyric_lang(lyrics.getString("lyrics_language"));
				tempLyric.setTrack_url(lyrics.getString("pixel_tracking_url"));
				tempLyric.setLyric_copy(lyrics.getString("lyrics_copyright"));
				
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tempLyric;
	}
	
	/*
	 * get the image of the artist
	 * from last.fm api
	 */
	public static synchronized void getArtistImage(String artistName){
		
		String response = null;
	
		url = "http://ws.audioscrobbler.com/2.0/?method=artist.getimages&artist="+URLEncoder.encode(artistName)+"&api_key="+API_KEY_LASTFM+"&format=json";
		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		
		httpGet = new HttpGet(url);
		
		try {
			response = httpClient.execute(httpGet,handler);
			
			Log.d(LYRIC_FRENZY, response);
			
			JSONObject musicData = new JSONObject(response);
			JSONObject images = musicData.getJSONObject("images");
			JSONArray image = images.getJSONArray("image");
			
			
			JSONObject sizes = image.getJSONObject(0).getJSONObject("sizes");			
			JSONArray size = sizes.getJSONArray("size");
			
			String imageUrlString = size.getJSONObject(1).getString("#text");
			
			Log.d(LYRIC_FRENZY, imageUrlString);
			
			JSONObject votes = image.getJSONObject(0).getJSONObject("votes");
			Log.d(LYRIC_FRENZY, votes.toString());
			thumbsup = votes.getString("thumbsup");
			thumbsdown = votes.getString("thumbsdown");

			
			/*
			 * get the image from the given url and download it
			 */
			URL imageUrl = new URL(imageUrlString);
			HttpURLConnection conn= (HttpURLConnection)imageUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			
			InputStream is = conn.getInputStream();

			bmImg = BitmapFactory.decodeStream(is);
			
			/*
			 * end of the image download
			 */
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * fetches top tags from last fm scrobble api
	 * to detect the mood of the user from song lyrics
	 */
	public static void getTopTagsFromLastFm(String artistName ,String trackName){
		
		String response = null;
		
		url = "http://ws.audioscrobbler.com/2.0/?method=track.gettoptags&artist="+URLEncoder.encode(artistName)+"&track="+URLEncoder.encode(trackName)+"&api_key="+API_KEY_LASTFM+"&format=json";
		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		
		httpGet = new HttpGet(url);
		
		try {
			response = httpClient.execute(httpGet,handler);
			
			Log.d(LYRIC_FRENZY, response);
			
			
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * get related youtube videos from youtube api
	 */
	public static synchronized void getSongVideos(String songName){
		
		String response = null;
		url = "http://gdata.youtube.com/feeds/api/videos?q="+URLEncoder.encode(songName)+"&v=2&alt=jsonc";
		Bitmap videoThumb;
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		
		httpGet = new HttpGet(url);
		
		try {
			response = httpClient.execute(httpGet,handler);
			
			Log.d(LYRIC_FRENZY, response);
			
			JSONObject musicData = new JSONObject(response);
			JSONObject data = musicData.getJSONObject("data");
			
			JSONArray items = data.getJSONArray("items");
			
			for (int i = 0; i < 5; i++) {
				
				JSONObject thumbnail = items.getJSONObject(i).getJSONObject("thumbnail");
				JSONObject player = items.getJSONObject(i).getJSONObject("player");
				
				
				String thumbnailUrl = thumbnail.getString("sqDefault");
				String playerUrl = player.getString("default");
				Log.d(LYRIC_FRENZY, thumbnailUrl);
				Log.d(LYRIC_FRENZY, playerUrl);
				
				playerList.add(playerUrl);
				thumbnailList.add(thumbnailUrl);
				
				/*
				 * download image
				 */
				URL imageUrl = new URL(thumbnailUrl);
				HttpURLConnection conn= (HttpURLConnection)imageUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();

				InputStream is = conn.getInputStream();
				videoThumb = BitmapFactory.decodeStream(is);
				actualImages.add(videoThumb);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized String translateLyrics(String lyric,String source,String target){
		
		String response = null;
		url = "https://www.googleapis.com/language/translate/v2?key="+API_KEY_GOOGLE+"&q="+URLEncoder.encode(lyric)+"&source="+source+"&target="+target;
		String translatedText=null;
		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		httpGet = new HttpGet(url);

		try {
			response = httpClient.execute(httpGet,handler);
			Log.d(LYRIC_FRENZY, response);
			
			JSONObject gTranslate = new JSONObject(response);
			JSONObject data = gTranslate.getJSONObject("data");
			JSONArray translation = data.getJSONArray("translations");
			translatedText = translation.getJSONObject(0).getString("translatedText");
			Log.d(LYRIC_FRENZY, "Translated Text : "+translatedText);
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return translatedText;
	}
	
	public static synchronized void getLocaleCharts(String country){
		
		String response = null;
		url = "http://api.musixmatch.com/ws/1.1/artist.chart.get?format=json&apikey="+ API_KEY +"&country="+ country +"&page=1&page_size=30";

		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		httpGet = new HttpGet(url);

		try {
			response = httpClient.execute(httpGet,handler);
			//Log.d(LYRIC_FRENZY, response);
			
			JSONObject charts = new JSONObject(response);
			
			JSONObject message = charts.getJSONObject("message");
			JSONObject body = message.getJSONObject("body");
			JSONArray artistList = body.getJSONArray("artist_list");
			
			Log.d(LYRIC_FRENZY,"List : " + artistList);
			
			for (int i = 0; i < artistList.length(); i++) {
				
				JSONObject artist = artistList.getJSONObject(i).getJSONObject("artist");
				
				Log.d(LYRIC_FRENZY, artist.getString("artist_name"));
				chartList.add(artist.getString("artist_name"));
			}
			Log.d(LYRIC_FRENZY, String.valueOf(chartList.size()));
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static synchronized void getLocaleSongs(String country){
		
		String response = null;
		url = "http://api.musixmatch.com/ws/1.1/track.chart.get?&format=json&apikey="+API_KEY+"&country="+country+"&page=1&page_size=30&f_has_lyrics=1";

		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		httpGet = new HttpGet(url);

		try {
			response = httpClient.execute(httpGet,handler);
			//Log.d(LYRIC_FRENZY, response);
			
			JSONObject charts = new JSONObject(response);
			
			JSONObject message = charts.getJSONObject("message");
			JSONObject body = message.getJSONObject("body");
			JSONArray trackList = body.getJSONArray("track_list");
			
			Log.d(LYRIC_FRENZY,"List : " + trackList);
			
			for (int i = 0; i < trackList.length(); i++) {
				
				JSONObject artist = trackList.getJSONObject(i).getJSONObject("track");
				
				Log.d(LYRIC_FRENZY, artist.getString("artist_name"));
				songList.add(artist.getString("artist_name")+ " - " + artist.getString("track_name"));
				lyricId.add(artist.getString("lyrics_id"));
			}
			Log.d(LYRIC_FRENZY, String.valueOf(chartList.size()));
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized String getArtistPage(String artistName){
		
		String response = null;
		String pageUrl="";
		url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="+URLEncoder.encode(artistName)+"&api_key="+API_KEY_LASTFM+"&format=json";

		
		httpClient = new DefaultHttpClient();
		handler = new BasicResponseHandler();
		httpGet = new HttpGet(url);
		
		try {
			response = httpClient.execute(httpGet,handler);
			
			JSONObject artistInfo = new JSONObject(response);
			JSONObject message = artistInfo.getJSONObject("artist");
			pageUrl = message.getString("url");
			
			Log.d(LYRIC_FRENZY, "Url : "+pageUrl);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pageUrl;
	}
}
