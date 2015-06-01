package com.wjch.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.wjch.mp.ListAllActivity;
import com.wjch.mp.OneActivity;
import com.wjch.mp.R;
import com.wjch.util.MyApp;

public class MusicService extends Service {
	private MediaPlayer mediaPlayer;
	private static final int SET_SEEKBAR_MAX = 3;// setup SeeBar max length
	private static final int UPDATE_PROGRESS = 1;// refrash SeekBar progress
	private static final int LOADING_DATA = 2;// loading data
	private NotificationManager notificationManager;
	private Notification notification;
	private boolean mResumeAfterCall = false;
	private Intent intent;
	private NotificationManager manager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

	}

	@SuppressLint("ShowToast")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String action = intent.getAction();
		if (action.equals("play")) {
			try {
				playMusic(intent.getStringExtra("uri"));
//				showNotification(intent.getStringExtra("title"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (action.equals("changed")) {
			if (mediaPlayer != null) {
				mediaPlayer.seekTo(intent.getIntExtra("seekbarprogress", 0));
			}
		} else if (action.equals("clickplay")) {
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					this.intent = new Intent("pause");
					sendBroadcast(this.intent);
				} else {
					mediaPlayer.start();
					this.intent = new Intent("setplay");
					sendBroadcast(this.intent);
				}
			}
		}
//		else if (action.equals("loops")) {
//			if (mediaPlayer != null) {
//				if (mediaPlayer.isLooping()) {
//					mediaPlayer.setLooping(false);
//					this.intent = new Intent("stoploop");
//					sendBroadcast(this.intent);
//				} else {
//					mediaPlayer.setLooping(true);
//					this.intent = new Intent("startloop");
//					sendBroadcast(this.intent);
//				}
//			}
//		}
		else if (action.equals("stopnotification")) {
			notificationManager.cancelAll();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		killAll();
	}

	public void killAll() {
		handler.removeMessages(UPDATE_PROGRESS);
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;
		notificationManager.cancelAll();
	}

	private void playMusic(String path) throws Exception {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}
		mediaPlayer.reset();
		mediaPlayer.setDataSource(path);
		mediaPlayer.prepareAsync();
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				handler.sendEmptyMessage(SET_SEEKBAR_MAX);
				handler.sendEmptyMessage(UPDATE_PROGRESS);
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
//				if (mp.isLooping()) {
//					mp.start();
//				} else {
					intent = new Intent("playNextSong");
					sendBroadcast(intent);
//				}
			}
		});
	}
	//OneActivity will receive this message;
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_PROGRESS:
				intent = new Intent("seekbarprogress");
				intent.putExtra("seekbarprogress",
						mediaPlayer.getCurrentPosition());
				sendBroadcast(intent);
				handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
				break;
			case LOADING_DATA:
				// adapter.notifyDataSetInvalidated();
				break;
			case SET_SEEKBAR_MAX:
				intent = new Intent("seekbarmaxprogress");
				intent.putExtra("seekbarmaxprogress", mediaPlayer.getDuration());
				sendBroadcast(intent);
				//needn't  send message all the time  because it's enough to set up progressbar's maxprogress just once; 
				break;
			default:
				break;
			}
		};
	};

//	private void showNotification(String title) {
//		notification = new Notification(R.drawable.app_icon, "我的音乐",
//				System.currentTimeMillis());
//
//		notification.flags |= Notification.FLAG_NO_CLEAR;
//		notification.flags |= Notification.FLAG_ONGOING_EVENT;
//
//		Intent intent = new Intent(MusicService.this, ListAllActivity.class);
//		intent.setAction(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		// Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//				intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(this, "playing", title, pendingIntent);
//		notificationManager.notify(1, notification);
//	}

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				int ringvolume = audioManager
						.getStreamVolume(AudioManager.STREAM_RING);
				if (ringvolume > 0) {
					mResumeAfterCall = (mediaPlayer.isPlaying() || mResumeAfterCall);
					mediaPlayer.pause();
					Intent intent = new Intent("pause");
					sendBroadcast(intent);
				}
			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				// pause the music while a conversation is in progress
				mResumeAfterCall = (mediaPlayer.isPlaying() || mResumeAfterCall);
				mediaPlayer.pause();
				Intent intent = new Intent("pause");
				sendBroadcast(intent);
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				// start playing again
				if (mResumeAfterCall) {
					mediaPlayer.start();
					Intent intent = new Intent("setplay");
					sendBroadcast(intent);
					mResumeAfterCall = false;
				}
			}
		}
	};
}