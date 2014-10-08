package net.cstong.android.ui;

import net.cstong.android.MyApplication;
import net.cstong.android.R;
import net.cstong.android.api.UserApi;
import net.cstong.android.util.Constant;
import net.cstong.android.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ab.activity.AbActivity;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;

public class RegisterActivity extends AbActivity {
	private final String TAG = "RegisterActivity";
	private MyApplication mApplication;
	private EditText userName = null;
	private EditText userPwd = null;
	private EditText userMobile = null;
	private EditText userEmail = null;
	//private CheckBox checkBox = null;

	private AbStringHttpResponseListener registerListener;
	private ImageButton mClear1;
	private ImageButton mClear2;
	private ImageButton mClear3;
	private ImageButton mClear4;
	private boolean registerSuccess = false;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_register);
		mApplication = (MyApplication) abApplication;

		initTitleBar();

		userName = (EditText) findViewById(R.id.userName);
		userPwd = (EditText) findViewById(R.id.userPwd);
		userMobile = (EditText) findViewById(R.id.userMobile);
		userEmail = (EditText) findViewById(R.id.userEmail);
		//checkBox = (CheckBox) findViewById(R.id.register_check);
		mClear1 = (ImageButton) findViewById(R.id.clearName);
		mClear2 = (ImageButton) findViewById(R.id.clearPwd);
		mClear3 = (ImageButton) findViewById(R.id.clearMobile);
		mClear4 = (ImageButton) findViewById(R.id.clearEmail);
		//Button agreementBtn = (Button) findViewById(R.id.agreementBtn);

		/**
		agreementBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(RegisterActivity.this, AgreementActivity.class);
				startActivity(intent);
			}
		});
		**/

		Button registerBtn = (Button) findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(new RegisterOnClickListener());

		/**
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {

			}
		});
		**/

		userName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				String str = userName.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear1.setVisibility(View.VISIBLE);
					if (!(AbStrUtil.isNumberLetter(str)||AbStrUtil.isContainChinese(str))) {
						str = str.substring(0, length - 1);
						userName.setText(str);
						String str1 = userName.getText().toString().trim();
						userName.setSelection(str1.length());
						showToast(R.string.error_name_expr);
					}
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
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {

			}
		});

		userPwd.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				String str = userPwd.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear2.setVisibility(View.VISIBLE);
					if (!AbStrUtil.isNumberLetter(str)) {
						str = str.substring(0, length - 1);
						userPwd.setText(str);
						String str1 = userPwd.getText().toString().trim();
						userPwd.setSelection(str1.length());
						showToast(R.string.error_name_expr);
					}

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
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {

			}
		});

		userMobile.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				String str = userMobile.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear3.setVisibility(View.VISIBLE);
					if (!AbStrUtil.isNumber(str)) {
						str = str.substring(0, length - 1);
						userMobile.setText(str);
						String str1 = userMobile.getText().toString().trim();
						userMobile.setSelection(str1.length());
						showToast(R.string.error_name_expr);
					}
					mClear3.postDelayed(new Runnable() {
						@Override
						public void run() {
							mClear3.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClear3.setVisibility(View.INVISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {

			}
		});

		userEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before,
					final int count) {
				String str = userEmail.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					mClear4.setVisibility(View.VISIBLE);
					if (AbStrUtil.isContainChinese(str)) {
						str = str.substring(0, length - 1);
						userEmail.setText(str);
						String str1 = userEmail.getText().toString().trim();
						userEmail.setSelection(str1.length());
						showToast(R.string.error_email_expr2);
					}
					mClear4.postDelayed(new Runnable() {

						@Override
						public void run() {
							mClear4.setVisibility(View.INVISIBLE);
						}

					}, 5000);
				} else {
					mClear4.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count,
					final int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {

			}
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

		mClear3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				userMobile.setText("");
			}
		});

		mClear4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				userEmail.setText("");
			}
		});

		registerListener = new AbStringHttpResponseListener() {
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
				String statu = UserApi.parseRegisterResponse(content);
				if (statu != null) {
					registerSuccess = true;
					//Constant.myApp.mUser.cookie = statu;
				}
				// 更新cookie
				mApplication.updateLoginParams();
				mApplication.saveUserData();
				setResult(RESULT_OK, getIntent());
				registerSuccess = true;
				showToast(R.string.msg_register_success);
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
				showToast(R.string.msg_register_fail);
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				//移除进度框
				removeProgressDialog();
				if (registerSuccess) {
					Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			};
		};

		initTitleRightLayout();
	}

	private void initTitleRightLayout() {

	}

	public class RegisterOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(final View v) {
			final String mStr_name = userName.getText().toString().trim();
			final String mStr_pwd = userPwd.getText().toString().trim();
			final String mStr_mobile = userMobile.getText().toString().trim();
			final String mStr_email = userEmail.getText().toString().trim();
			if (TextUtils.isEmpty(mStr_name)) {
				showToast(R.string.error_name);
				userName.setFocusable(true);
				userName.requestFocus();
				return;
			}

			if (!(AbStrUtil.isNumberLetter(mStr_name)||AbStrUtil.isContainChinese(mStr_name))) {
				showToast(R.string.error_name_expr);
				userName.setFocusable(true);
				userName.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_name) < 3) {
				showToast(R.string.error_name_length1);
				userName.setFocusable(true);
				userName.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_name) > 15) {
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

			if (AbStrUtil.strLength(mStr_pwd) > 20) {
				showToast(R.string.error_pwd_length2);
				userPwd.setFocusable(true);
				userPwd.requestFocus();
				return;
			}

			if (TextUtils.isEmpty(mStr_mobile)) {
				showToast(R.string.error_mobile);
				userMobile.setFocusable(true);
				userMobile.requestFocus();
				return;
			}

			if (AbStrUtil.strLength(mStr_mobile) != 11) {
				showToast(R.string.error_mobile);
				userMobile.setFocusable(true);
				userMobile.requestFocus();
				return;
			}

			if (AbStrUtil.isEmpty(mStr_email)) {
				showToast(R.string.error_email);
				userEmail.setFocusable(true);
				userEmail.requestFocus();
				return;
			}

			if (!AbStrUtil.isEmail(mStr_email)) {
				showToast(R.string.error_email_expr);
				userEmail.setFocusable(true);
				userEmail.requestFocus();
				return;
			}

			/**
			if (!checkBox.isChecked()) {
				showToast(R.string.error_agreement);
				return;
			}
			**/

			showProgressDialog();

			Constant.myApp.mUser.username = mStr_name;
			Constant.myApp.mUser.password = mStr_pwd;
			Constant.myApp.mUser.email = mStr_email;
			Constant.myApp.mUser.mobile = mStr_mobile;
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					register();
				}
			});
			
		}
	}

	protected void register() {
		AbRequestParams params = new AbRequestParams();
		params.put("username", Constant.myApp.mUser.username);
		params.put("password", Constant.myApp.mUser.password);
		params.put("repassword", Constant.myApp.mUser.password);
		params.put("mobile", Constant.myApp.mUser.mobile);
		params.put("email", Constant.myApp.mUser.email);
		UserApi.register(this, params, registerListener);
	}

	private void initTitleBar() {
		Utils.initTitleBarLeft(this, R.drawable.button_selector_back, getResources().getString(R.string.register), new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		View loginView = mInflater.inflate(R.layout.btn_register, null);
		getTitleBar().addRightView(loginView);
		Button btnLogin = (Button) loginView.findViewById(R.id.btnRegister);
		btnLogin.setText(R.string.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
