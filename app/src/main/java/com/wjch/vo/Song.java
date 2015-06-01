package com.wjch.vo;

import java.io.Serializable;

import android.os.Parcel;
/*
 * Song Value Object
 */
public class Song<T> implements Serializable{
	private int id;
	private String songname;
	private String artist;
	private long albumId;
	private String album;
	private String url;
	private String duration;
	private String albumpic;
	public Song(int id,String songname, String artist,long albumId, String album,String url,String duration) {
		super();
		this.id= id;
		this.songname = songname;
		this.artist = artist;
		this.album = album;
		this.url=url;
		this.duration=duration;
		this.setAlbumId(albumId);
	}

	public Song(Parcel arg0) {
		id =arg0.readInt();
		songname=arg0.readString();
		artist=arg0.readString();
		album=arg0.readString();
		url=arg0.readString();
		duration=arg0.readString();
	}

	public String getSongname() {
		return songname;
	}

	public void setSongname(String songname) {
		this.songname = songname;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getAlbumpic() {
		return albumpic;
	}

	public void setAlbumpic(String albumpic) {
		this.albumpic = albumpic;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel parcel, int arg1) {
//		parcel.writeInt(id);
//		parcel.writeString(songname);
//		parcel.writeString(artist);
//		parcel.writeString(album);
//		parcel.writeString(url);
//		parcel.writeString(duration);
//	}
//	
//	private Parcelable.Creator<Song> creator = new Creator<Song>() {
//
//		@Override
//		public Song<?> createFromParcel(Parcel arg0) {
//			return new Song(arg0);
//		}
//
//		@Override
//		public Song[] newArray(int arg0) {
//			// TODO Auto-generated method stub
//			return new  Song[arg0];
//		}
//	};	
}
