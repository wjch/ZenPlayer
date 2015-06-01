package com.wjch.mp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;


public class ScanSdReceiver extends BroadcastReceiver {
    private int count1;
    private int count2;
    private int count;
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("******action"+action+"******");
		if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
			Cursor c1 = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.AudioColumns.TITLE,
							MediaStore.Audio.Media.DURATION,
							MediaStore.Audio.AudioColumns.ARTIST,
							MediaStore.Audio.AudioColumns._ID,
							MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.DURATION}, null, null,
					MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			count1 = c1.getCount();
			System.out.println("count:" + count);

//			Toast.makeText(context, "正在扫描存储卡...",Toast.LENGTH_SHORT).show();

		} else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
			System.out.println("正在扫描存储卡...");
			Cursor c2 = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.AudioColumns.TITLE,
							MediaStore.Audio.Media.DURATION,
							MediaStore.Audio.AudioColumns.ARTIST,
							MediaStore.Audio.AudioColumns._ID,
							MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.DURATION},null,
					null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			count2 = c2.getCount();
			count = count2 - count1;
			if (count >= 0) {
				Toast.makeText(context, "共增加" + count + "首歌曲",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "共减少" + count + "首歌曲",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}