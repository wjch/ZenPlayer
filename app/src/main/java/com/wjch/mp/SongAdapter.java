package com.wjch.mp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wjch.vo.Song;

public class SongAdapter extends ArrayAdapter<Song> {
	public static final String MYTAG = "com.wjch.mp";
	private ListView listView;
	private Long curpos;
	public void setSelectedPosition(int pos) {
		notifyDataSetChanged();
	}

	@Override
	public Song getItem(int position) {
		return super.getItem(position);
	}

	class ViewHolder {
		// TextView song_id;
		TextView song_name;
		TextView artist;
		ImageView iv;
	}

	private int resourceId;
	private List<Song> songList;

	public SongAdapter(Context context, int textViewResourceId,
			List<Song> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		this.songList = objects;
	}
	
	@Override
	public int getCount() {
		return songList.size();
	}

	@Override
	public int getPosition(Song item) {
		return songList.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Song song = getItem(position);
		setCurpos(getItemId(position));
		View view = null;
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId,
					null);
			// holder.song_id = (TextView)
			// convertView.findViewById(R.id.song_id);
			holder.song_name = (TextView) convertView.findViewById(R.id.name);
			holder.artist = (TextView) convertView.findViewById(R.id.artist);
			holder.iv=(ImageView) convertView.findViewById(R.id.sel1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// TextView album =(TextView) view.findViewById(R.id.album);
		// holder.song_id.setText(String.valueOf(getPosition(song)+1));
		holder.song_name.setText(String.valueOf(getPosition(song) + 1) + "."
				+ song.getSongname());
		//ListAllActivity.getPlaypos()
		holder.artist.setText(song.getArtist());
		// album.setText(song.getAlbum());
		if(OneActivity.getPlaypos()==position){
			holder.iv.setVisibility(View.VISIBLE);
		}else{
			holder.iv.setVisibility(View.GONE);
		}
		return convertView;
	}

	public Long getCurpos() {
		return curpos;
	}

	public void setCurpos(Long curpos) {
		this.curpos = curpos;
	}

}
