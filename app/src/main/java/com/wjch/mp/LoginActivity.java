package com.wjch.mp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.wjch.util.MyDatabaseHelper;
import com.wjch.util.StringUtil;

public class LoginActivity extends Activity {

	private Button loginButton;
	private Button registerButton;
	private EditText emailEditText;
	private EditText passEditText;
	private TextView infoText;
	private String email;
	private String password;
	private Boolean flag;
	private MyDatabaseHelper myDatabaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		myDatabaseHelper = new MyDatabaseHelper(this, "User.db", null, 1);

		initAndSetupView();
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				email = emailEditText.getText().toString();
				password = passEditText.getText().toString();
				System.out.println(email + "======" + password);
				if (checkUser(email, password)) {
					Intent loginintent = new Intent(LoginActivity.this,
							ListAllActivity.class);
					startActivity(loginintent);
				} else {
//					return;
				}
			}
		});
	}

	public Boolean checkUser(String email, String pass) {
		if(StringUtil.isEmpty(email) ){
			infoText.setText("你的邮箱不能为空");
			return false;
		}	
		if(!StringUtil.isEmail(email)){
			infoText.setText("你的邮箱不符合格式");
			return false;
		}

		if(StringUtil.isEmpty(pass)){
			infoText.setText("你的密码不能为空");
			return false;			
		}
		if(password.length() < 6 || password.length() >20){
			infoText.setText("你的密码应大于6位,而小于20位");
			return false;	
		}
		SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
		System.out.println(email + "!!!!!");
		Cursor cursor = db.rawQuery("select password from user where email=?",
				new String[] { email });
		if (cursor.moveToNext()) {
			String password1 = cursor.getString(0);
			if (pass.equals(password1)) {
				return true;
			} else {
				emailEditText.setText("");
				passEditText.setText("");
				return false;
			}
		}
		return false;
	}

	private void initAndSetupView() {
		emailEditText = (EditText) findViewById(R.id.email);
		passEditText = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginbtn1);
		registerButton = (Button) findViewById(R.id.regbtn1);
		infoText = (TextView) findViewById(R.id.infotext);
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finish();
		System.exit(0);
	};
}
