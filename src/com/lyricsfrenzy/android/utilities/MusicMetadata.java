package com.lyricsfrenzy.android.utilities;

/**
 * 
 * @author burak
 *	hold the general data about app to use
 */
public class MusicMetadata {

	private String track_id;
	
	private String lyric_id;
	
	private String track_mbid;
	
	private String subtitle_id;
	
	private String track_name;
	
	private String artist_id;
	
	private String artist_mbid;
	
	private String artist_name;

	public MusicMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	public MusicMetadata(String trackId, String lyricId, String trackMbid,
			String subtitleId, String trackName, String artistId,
			String artistMbid, String artistName) {
		super();
		track_id = trackId;
		lyric_id = lyricId;
		track_mbid = trackMbid;
		subtitle_id = subtitleId;
		track_name = trackName;
		artist_id = artistId;
		artist_mbid = artistMbid;
		artist_name = artistName;
	}

	public String getTrack_id() {
		return track_id;
	}

	public void setTrack_id(String trackId) {
		track_id = trackId;
	}

	public String getLyric_id() {
		return lyric_id;
	}

	public void setLyric_id(String lyricId) {
		lyric_id = lyricId;
	}

	public String getTrack_mbid() {
		return track_mbid;
	}

	public void setTrack_mbid(String trackMbid) {
		track_mbid = trackMbid;
	}

	public String getSubtitle_id() {
		return subtitle_id;
	}

	public void setSubtitle_id(String subtitleId) {
		subtitle_id = subtitleId;
	}

	public String getTrack_name() {
		return track_name;
	}

	public void setTrack_name(String trackName) {
		track_name = trackName;
	}

	public String getArtist_id() {
		return artist_id;
	}

	public void setArtist_id(String artistId) {
		artist_id = artistId;
	}

	public String getArtist_mbid() {
		return artist_mbid;
	}

	public void setArtist_mbid(String artistMbid) {
		artist_mbid = artistMbid;
	}

	public String getArtist_name() {
		return artist_name;
	}

	public void setArtist_name(String artistName) {
		artist_name = artistName;
	}
	
}
