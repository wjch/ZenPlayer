package com.wjch.mp;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wjch.mp.R.color;
import com.wjch.util.MediaUtil;
import com.wjch.util.MyApp;
import com.wjch.vo.Song;

public class ListAllActivity extends Activity {
	public static final String MYTAG = "com.wjch.mp";
	@SuppressWarnings("rawtypes")
	private List<Song> songlist;
	private int pos;// 文件删除position
	private SongAdapter adapter;
	private ListView listView;
	private TextView infoTextView;
	private AlertDialog dialog;
	private Button okButton;
	private Button cancelButton;
	private TextView dialoginfoTextView;
	private int curposition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApp.getInstance().addActivity(this);
		setContentView(R.layout.activity_list_all);
		songlist = MediaUtil.getSonglist(this);
		System.out.println((songlist==null)+"是否为空");
		System.out.println(songlist.size());
		infoTextView = (TextView) findViewById(R.id.display_info);
		infoTextView.setVisibility(View.GONE);
		if (songlist.size()==0) {
			infoTextView.setVisibility(View.VISIBLE);
			infoTextView.setText("没有歌曲");
			infoTextView.setTextColor(Color.parseColor("#CA6157"));
			return;
		}
		setUpSongListView();
	}

	private void setUpSongListView() {
		adapter = new SongAdapter(ListAllActivity.this, R.layout.sing_item,
				songlist);
		listView = (ListView) findViewById(R.id.cards_list);
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Song<?> song = songlist.get(position);
//				setPlaypos(position);
				Intent intent = new Intent(ListAllActivity.this,
						OneActivity.class);
				intent.putExtra("song", song);
				intent.putExtra("position", position);
				intent.putExtra("songlist", (Serializable) songlist);
				startActivity(intent);
				
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				pos = position;
				showDialog();
				return true;
			}
		});
	}

	private void showDialog() {
		dialog = new AlertDialog.Builder(ListAllActivity.this).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.alertdialog_style);
		
		okButton = (Button) window.findViewById(R.id.btn_ok);
		cancelButton = (Button) window.findViewById(R.id.btn_cancel);
		dialoginfoTextView = (TextView) window.findViewById(R.id.showdialog);
		dialoginfoTextView.setText("您真的想删除吗？");
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deleteTheSong(pos);
				dialog.dismiss();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_all, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemid = item.getItemId();
		switch (itemid) {
		case R.id.action_scan:
			Intent intent = new Intent(ListAllActivity.this,
					ScanMusicActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteTheSong(int position) {
		Song<?> song = songlist.remove(position);
		MediaUtil.removeSong(ListAllActivity.this, position);
		String path = song.getUrl();
		System.out.println("path is" + path);
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
		adapter.notifyDataSetChanged();
		listView.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		dialog = new AlertDialog.Builder(ListAllActivity.this).create();
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.quitdialog_style);

		okButton = (Button) window.findViewById(R.id.btn_ok);
		cancelButton = (Button) window.findViewById(R.id.btn_cancel);
		dialoginfoTextView = (TextView) window
				.findViewById(R.id.showdialog);
		dialoginfoTextView.setText("亲，您真的想离开嘛？");
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent stopnotif_intent = new Intent("stopnotification");
				dialog.dismiss();
	 			stopService(stopnotif_intent);
				MyApp.getInstance().exit();
				System.exit(0);
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
	}
	
}
