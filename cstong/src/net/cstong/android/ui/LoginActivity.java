package net.cstong.android.ui;

import net.cstong.android.MyApplication;
import net.cstong.android.R;
import net.cstong.android.api.UserApi;
import net.cstong.android.api.UserApi.UserInfo;
import net.cstong.android.model.UserDao;
import net.cstong.android.util.Constant;
import net.cstong.android.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ab.activity.AbActivity;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;

public class LoginActivity extends AbActivity {
	private final String TAG = "LoginActivity";
	private EditText userName = null;
	private EditText userPwd = null;
	private String mStr_name = null;
	private String mStr_pwd = null;
	private ImageButton mClear1;
	private ImageButton mClear2;
	private MyApplication mApplication;
	private LinearLayout rightLayout;

	private AbStringHttpResponseListener loginListener;
	private Button loginBtn = null;
	private boolean loginSuccess = false;

	public UserDao mUserDao = null;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_login);

		//初始化数据库操作实现类
		mUserDao = new UserDao(LoginActivity.this);
		mApplication = (MyApplication) abApplication;
		rightLayout = getTitleBar().getRightLayout();

		initTitleBar();

		userName = (EditText) findViewById(R.id.userName);
		userPwd = (EditText) findViewById(R.id.userPwd);
		//CheckBox checkBox = (CheckBox) findViewById(R.id.login_check);
		mClear1 = (ImageButton) findViewById(R.id.clearName);
		mClear2 = (ImageButton) findViewById(R.id.clearPwd);

		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new LoginOnClickListener());

		/**
		Button pwdBtn = (Button) findViewById(R.id.pwdBtn);
		pwdBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(LoginActivity.this, FindPwdActivity.class);
				startActivity(intent);
			}
		});
		**/

		loginListener = new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(final int statusCode, final String content) {
//				Log.d(TAG, "onSuccess:" + content);
				/**
				Bitmap bitmap = AbImageUtil.bytes2Bimap(content);
				ImageView view = new ImageView(MainActivity.this);
				view.setImageBitmap(bitmap);

				showDialog("返回结果", view, new OnClickListener() {
					@Override
					public void onClick(final DialogInterface arg0, final int arg1) {}
				});
				**/

				UserInfo userInfo = UserApi.parseLoginResponse(content);
				if (userInfo.getResultCode() == 0) {
					Constant.myApp.mUser.username = userInfo.username;
					Constant.myApp.mUser.password = userInfo.password;
					Constant.myApp.mUser.email = userInfo.email;
					Constant.myApp.mUser.avatar = userInfo.avatar;
					Constant.myApp.mUser.avatar_m = userInfo.avatar_m;
					Constant.myApp.mUser.avatar_s = userInfo.avatar_s;
					//Constant.myApp.mUser.cookie = userInfo.cookie;
				} else {
					Constant.myApp.mUser.cookie = "";
				}
				mApplication.updateLoginParams();
				mApplication.saveUserData();
				setResult(RESULT_OK, getIntent());
				loginSuccess = true;
				if (userInfo.getResultCode() == 0) {
					showToast(R.string.msg_login_success);
				} else {
					showToast(userInfo.getResultMsg());
				}
			}

			// 开始执行前
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
				//显示进度框
				showProgressDialog();
			}

			// 失败，调用
			@Override
			public void onFailure(final int statusCode, final String content, final Throwable error) {
				showToast(R.string.msg_login_fail);
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				//移除进度框
				removeProgressDialog();
				if (loginSuccess) {
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			};
		};

		/**
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				Editor editor = abSharedPreferences.edit();
				editor.putBoolean(Constant.USERPASSWORDREMEMBERCOOKIE, isChecked);
				editor.commit();
				application.userPasswordRemember = isChecked;
			}
		});
		**/

		String name = abSharedPreferences.getString(Constant.USERNAMECOOKIE, "");
		String password = abSharedPreferences.getString(Constant.USERPASSWORDCOOKIE, "");
		boolean userPwdRemember = abSharedPreferences.getBoolean(Constant.USERPASSWORDREMEMBERCOOKIE, false);
		if (userPwdRemember) {
			userName.setText(name);
			userPwd.setText(password);
			//checkBox.setChecked(true);
		} else {
			userName.setText("");
			userPwd.setText("");
			//checkBox.setChecked(false);
		}

		userName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				String str = userName.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear1.setVisibility(View.VISIBLE);
					/**
					if (!AbStrUtil.isNumberLetter(str)) {
						str = str.substring(0, length - 1);
						userName.setText(str);
						String str1 = userName.getText().toString().trim();
						userName.setSelection(str1.length());
						showToast(R.string.error_name_expr);
					}
					**/
					mClear1.postDelayed(new Runnable() {
						@Override
						public void run() {
							mClear1.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClear1.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

			@Override
			public void afterTextChanged(final Editable s) {}
		});

		userPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				String str = userPwd.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear2.setVisibility(View.VISIBLE);
					/**
					if (!AbStrUtil.isNumberLetter(str)) {
						str = str.substring(0, length - 1);
						userPwd.setText(str);
						String str1 = userPwd.getText().toString().trim();
						userPwd.setSelection(str1.length());
						showToast(R.string.error_pwd_expr);
					}
					**/
					mClear2.postDelayed(new Runnable() {
						@Override
						public void run() {
							mClear2.setVisibility(View.INVISIBLE);
						}
					}, 5000);
				} else {
					mClear2.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

			@Override
			public void afterTextChanged(final Editable s) {}
		});

		mClear1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				userName.setText("");
			}
		});

		mClear2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				userPwd.setText("");
			}
		});
	}

	public class LoginOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(final View v) {
			if (v == loginBtn) {
				mStr_name = userName.getText().toString();
				mStr_pwd = userPwd.getText().toString();

				if (TextUtils.isEmpty(mStr_name)) {
					showToast(R.string.error_name);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				/**
				if (!AbStrUtil.isNumberLetter(mStr_name)) {
					showToast(R.string.error_name_expr);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}
				**/

				if (AbStrUtil.strLength(mStr_name) < 3) {
					showToast(R.string.error_name_length1);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_name) > 30) {
					showToast(R.string.error_name_length2);
					userName.setFocusable(true);
					userName.requestFocus();
					return;
				}

				if (TextUtils.isEmpty(mStr_pwd)) {
					showToast(R.string.error_pwd);
					userPwd.setFocusable(true);
					userPwd.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_pwd) < 6) {
					showToast(R.string.error_pwd_length1);
					userPwd.setFocusable(true);
					userPwd.requestFocus();
					return;
				}

				if (AbStrUtil.strLength(mStr_pwd) > 30) {
					showToast(R.string.error_pwd_length2);
					userPwd.setFocusable(true);
					userPwd.requestFocus();
					return;
				}

				showProgressDialog("正在登录...");
				login(mStr_name, mStr_pwd);

				//loginIMTask(mStr_name, mStr_pwd);

			}

		}
	}

	private void login(final String username, final String password) {
		AbRequestParams params = new AbRequestParams();
		params.put("username", username);
		params.put("password", password);
		UserApi.login(this, params, loginListener);
	}

	private void initTitleBar() {
		Utils.initTitleBarLeft(this, R.drawable.button_selector_back, getResources().getString(R.string.login), new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		View loginView = mInflater.inflate(R.layout.btn_register, null);
		getTitleBar().addRightView(loginView);
		Button btnRegister = (Button) loginView.findViewById(R.id.btnRegister);
		btnRegister.setText(R.string.register);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (Constant.myApp.mUser.cookie.length() == 0) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		} else {
			super.onBackPressed();
		}
	}
}