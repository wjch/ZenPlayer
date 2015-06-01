package com.wjch.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String CREATE_USER="create table user("
			+"id INTEGER PRIMARY KEY autoincrement,"
			+"username text,"
			+"password text,"
			+"email text UNIQUE,"
			+"avatar text)";
	private Context mContext;
	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.mContext =context;
	}

	@Override
	public void onCreate(SQLiteDatabase mSqLiteDatabase) {
		mSqLiteDatabase.execSQL(CREATE_USER);
		Toast.makeText(mContext, "create SUCCEED", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
