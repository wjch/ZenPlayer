package com.wjch.mp;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjch.util.MediaUtil;
import com.wjch.util.MyApp;
import com.wjch.vo.Song;

public class OneActivity extends Activity implements OnClickListener {
	public static final String MYTAG = "com.wjch.mp";
	private Song song;
	private List<Song> songlist;
	private int currentPositionMusic = -1;// current playing song position
	private final int NO_PIC = 0;
	private ImageButton lastImageButton;
	private ImageButton playImageButton;
	private ImageButton nextImageButton;
	private ImageView imageView;
	private SeekBar playSeekBar;
	private TextView currentPlayingSong;// current playing song title
	private String songname;
	private Intent intent;
	private int curpos;
	private ImageView iv;
	private static  int playpos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApp.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_one);

		getSongInfoFromIntent();
		findAndInitPlayView();
		playMusic();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getSongInfoFromIntent() {
		Intent fromintent = getIntent();
		songlist = (List<Song>) fromintent.getSerializableExtra("songlist");
		currentPositionMusic = fromintent.getIntExtra("position", -1);
	}

	private void findAndInitPlayView() {
		lastImageButton = (ImageButton) findViewById(R.id.previous);
		playImageButton = (ImageButton) findViewById(R.id.play);
		nextImageButton = (ImageButton) findViewById(R.id.next);
		// loopImageButton = (ImageButton) findViewById(R.id.loops);
		currentPlayingSong = (TextView) findViewById(R.id.songname);
		imageView = (ImageView) findViewById(R.id.imageview);
		lastImageButton.setOnClickListener(this);
		playImageButton.setOnClickListener(this);
		nextImageButton.setOnClickListener(this);
		iv = (ImageView) findViewById(R.id.sel1);
		playSeekBar = (SeekBar) findViewById(R.id.seekbar);
		playSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					intent = new Intent("changed");
					intent.putExtra("seekbarprogress", progress);
					startService(intent);
				}
			}
		});
	}

	private void playMusic() {
		intent = new Intent("play");
		intent.putExtra("uri", songlist.get(currentPositionMusic).getUrl());
		intent.putExtra("id", songlist.get(currentPositionMusic).getId());
		intent.putExtra("title", songlist.get(currentPositionMusic)
				.getSongname());
		intent.putExtra("artist", songlist.get(currentPositionMusic)
				.getArtist());
		System.out.println("playmusic的position" + currentPositionMusic);
		startService(intent);
		playImageButton.setImageResource(R.drawable.ic_action_pause);
		// currentPlayingSong.setTextColor(R.color.myred);
		currentPlayingSong.setText(songlist.get(currentPositionMusic)
				.getSongname());
		setPlaypos(currentPositionMusic);
		new Thread(new Runnable() {
			@Override
			public void run() {
				songname = songlist.get(currentPositionMusic).getSongname();
				Bitmap bitmap = null;
				bitmap = new MediaUtil(getBaseContext()).getArtwork(
						OneActivity.this, true,
						(Song<?>) songlist.get(currentPositionMusic));
				Message msg = new Message();
				msg.what = NO_PIC;
				msg.obj = bitmap;
				handle.sendMessage(msg);
			}
		}).start();

	}

	private Handler handle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NO_PIC:
				Bitmap bmp = (Bitmap) msg.obj;
				imageView.setImageBitmap(bmp);
				if (bmp == null) {
					imageView.setImageResource(R.drawable.dr_1x);
				}
				break;
			}
		};
	};

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("seekbarmaxprogress");
		filter.addAction("seekbarprogress");
		filter.addAction("playNextSong");
		filter.addAction("pause");
		filter.addAction("setplay");
		filter.addAction("stoploop");
		filter.addAction("startloop");
		registerReceiver(broadcastReceiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previous:
			playLastSong();
			break;
		case R.id.play:
			intent = new Intent("clickplay");
			intent.putExtra("uri", songlist.get(currentPositionMusic).getUrl());
			intent.putExtra("title", songlist.get(currentPositionMusic)
					.getSongname());
			startService(intent);
			break;
		case R.id.next:
			playNextSong();
			break;
		default:
			break;
		}
	}

	private void playLastSong() {
		if (currentPositionMusic != -1) {
			if (currentPositionMusic == 0) {
				currentPositionMusic = songlist.size() - 1;
				try {
					playMusic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				currentPositionMusic--;
				try {
					playMusic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void playNextSong() {
		if (currentPositionMusic != -1) {
			if (currentPositionMusic == songlist.size() - 1) {
				currentPositionMusic = 0;
				try {
					playMusic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				currentPositionMusic = currentPositionMusic + 1;
				try {
					playMusic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent1) {
		super.onNewIntent(intent1);
		setIntent(intent1);
		curpos = intent1.getIntExtra("position", -1);
		if (curpos == currentPositionMusic) {
			System.out.println(" ========I WORKED!==========");
			getSongInfoFromIntent();
			findAndInitPlayView();
		} else {
			getSongInfoFromIntent();
			findAndInitPlayView();
			playMusic();
		}
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("seekbarmaxprogress")) {
				playSeekBar.setMax(intent
						.getIntExtra("seekbarmaxprogress", 100));
			} else if (intent.getAction().equals("seekbarprogress")) {
				int seekBarProgress = intent.getIntExtra("seekbarprogress", 0);
				playSeekBar.setProgress(seekBarProgress);
			} else if (intent.getAction().equals("playNextSong")) {
				playNextSong();
			} else if (intent.getAction().equals("pause")) {
				playImageButton.setImageResource(R.drawable.ic_action_play);
			} else if (intent.getAction().equals("setplay")) {
				playImageButton.setImageResource(R.drawable.ic_action_pause);
			}
		}
	};

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(OneActivity.this, ListAllActivity.class);
		startActivity(intent);
	};
	public static int getPlaypos() {
		return playpos;
	}

	public  void setPlaypos(int playpos1) {
		playpos = playpos1;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.one, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemid = item.getItemId();
		switch (itemid) {
		case R.id.share_to:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT,"分享");
			intent.putExtra(Intent.EXTRA_TEXT, "我在听"+songlist.get(currentPositionMusic)
					.getSongname()+",你要不要来听一下?");
			startActivity(Intent.createChooser(intent, "分享到"));
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}