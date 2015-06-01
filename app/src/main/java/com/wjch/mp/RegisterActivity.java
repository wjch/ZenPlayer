package com.wjch.mp;

import com.wjch.util.MyDatabaseHelper;
import com.wjch.util.StringUtil;
import com.wjch.vo.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private Button regbtn;
	private EditText emailEditText;
	private EditText passEditText;
	private EditText usernameEditText;
	private TextView infoTextView;
	private String email;
	private String password;
	private String username;
	private MyDatabaseHelper myDatabaseHelper;
	private User mUser;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		intiAndSetupView();
		try {
			myDatabaseHelper = new MyDatabaseHelper(this, "User.db", null, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		regbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (checkRegUser()) {
					System.out.println(email + "####" + password + "#####"
							+ username);
					mUser = new User(0, username, password, email, null, null);
					db = myDatabaseHelper.getWritableDatabase();
					try {
						ContentValues values = new ContentValues();
						values.put("email", mUser.getEmail());
						values.put("password", mUser.getPassword());
						values.put("username", mUser.getUsername());
						db.insert("user", null, values);
						values.clear();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						db.close();
						Toast.makeText(RegisterActivity.this, "注册成功",
								Toast.LENGTH_SHORT).show();
//						Intent intent = new Intent(RegisterActivity.this,
//								LoginActivity.class);
//						startActivity(intent);
					}
				} else {
					// infoTextView.setText("无法注册，请输入规范文本");
				}
			}
		});
	}

	private void intiAndSetupView() {
		emailEditText = (EditText) findViewById(R.id.regemail);
		passEditText = (EditText) findViewById(R.id.regpassword);
		usernameEditText = (EditText) findViewById(R.id.regusername);
		infoTextView = (TextView) findViewById(R.id.reginforegtext);
		regbtn = (Button) findViewById(R.id.regbtnreg);
	}

	private Boolean checkRegUser() {
		email = emailEditText.getText().toString();
		username = usernameEditText.getText().toString();
		password = passEditText.getText().toString();
		if (StringUtil.isEmpty(email)) {
			infoTextView.setText("你的邮箱不能为空");
			return false;
		}
		if (!StringUtil.isEmail(email)) {
			infoTextView.setText("你的邮箱不符合格式");
			infoTextView.setText("");
			return false;
		}
		if (StringUtil.isEmpty(username)) {
			infoTextView.setText("你的用户名不能为空");
			return false;
		}
		if (StringUtil.isEmpty(password)) {
			infoTextView.setText("你的密码不能为空");
			return false;
		}
		if (username.length() < 6 || username.length() > 20) {
			infoTextView.setText("你的用户名应大于6位,而小于20位");
			return false;
		}
		if (password.length() < 6 || password.length() >20) {
			infoTextView.setText("你的密码应大于6位,而小于20位");
			return false;
		}
		return true;
	}

}
