package com.wjch.mp;

import com.wjch.util.MyApp;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ScanMusicActivity extends Activity {

	private Button scanButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApp.getInstance().addActivity(this);
		setContentView(R.layout.activity_scan_music);
		scanButton = (Button) findViewById(R.id.scanbtn);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
						scanMusic();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan_music, menu);
		return true;
	}

	private void scanMusic() {
		Toast.makeText(this, "正在扫描存储卡..."+"file:"
				+ Environment.getExternalStorageDirectory()
				.getAbsolutePath(),Toast.LENGTH_SHORT).show();
//		IntentFilter scanIntentFilter = new IntentFilter(
//				Intent.ACTION_MEDIA_SCANNER_STARTED);
//		scanIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//		scanIntentFilter.addDataScheme("file");
//
//		ScanSdReceiver scanSdReceiver = new ScanSdReceiver();
//		registerReceiver(scanSdReceiver, scanIntentFilter);
//
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//				Uri.parse("file://"
//						+ Environment.getExternalStorageDirectory()
//								.getAbsolutePath())));

//		Toast.makeText(getApplicationContext(),"扫描完成",Toast.LENGTH_SHORT).show();
		MediaScannerConnection.scanFile(this, new String[]{"file://"
				+ Environment.getExternalStorageDirectory()
				.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
					
					@Override
					public void onScanCompleted(String a, Uri uri) {
			            Toast.makeText(getApplicationContext(),"wdw",Toast.LENGTH_SHORT).show();
			           
					}
				});
	}

}
