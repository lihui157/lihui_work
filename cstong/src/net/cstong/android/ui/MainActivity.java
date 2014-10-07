package net.cstong.android.ui;

import net.cstong.android.MyApplication;
import net.cstong.android.R;
import net.cstong.android.util.Constant;
import net.cstong.android.util.Utils;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;

import com.ab.activity.AbActivity;
import com.ab.view.slidingmenu.SlidingMenu;

public class MainActivity extends AbActivity {
	public static final String TAG = "MainActivity";
	private SlidingMenu menu;
	private boolean mDoubleBackQuit = false;
	private boolean postClicked = false;
	protected View btnPost;
	protected ForumsTabFragment tabFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_main);

		tabFragment = new ForumsTabFragment();

		initTitleBar();
		initTitleRightLayout();
		initAppConfig();
	}

	private void initAppConfig(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		MyApplication.MAX_IMAGE_WIDTH = dm.widthPixels;//宽度
		MyApplication.MAX_IMAGE_HEIGHT = dm.heightPixels ;//高度
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	private void initTitleRightLayout() {}

	private void initTitleBar() {
		Utils.initTitleBarLeft(this, R.drawable.button_selector_menu, getResources().getString(R.string.app_name), new View.OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				if (Constant.myApp.mUser.cookie.length() == 0) {
					MainActivity.this.gotoLoginView();
				} else {
					if (menu.isMenuShowing()) {
						menu.showContent();
					} else {
						menu.showMenu();
					}
				}
			}
		});
		View postView = mInflater.inflate(R.layout.btn_post, null);
		getTitleBar().addRightView(postView);
		btnPost = postView.findViewById(R.id.btnPost);
		btnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				new Thread(){
					public void run(){
						if (Constant.myApp.mUser.cookie.length() > 0) {
							postClicked = true;
							Intent intent = new Intent(MainActivity.this, ThreadPostActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							// method 2
							Bundle bundle = new Bundle();
							bundle.putSerializable(Constant.KEY_FORUMINFO, tabFragment.getTabData(tabFragment.getCurrentTabPosition()));
							intent.putExtras(bundle);
							startActivity(intent);
						} else {
							btnPost.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									showToast("请先登陆...");
								}
							});
							
							MainActivity.this.gotoLoginView();
						}
					}
				}.start();
				
			}
		});
		btnPost.setVisibility(View.INVISIBLE);

		// SlidingMenu的配置
		final MainMenuFragment mainMenuFragment = new MainMenuFragment();
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

		// menu视图的Fragment添加
		menu.setMenu(R.layout.menu_main);
		getSupportFragmentManager().beginTransaction().replace(R.id.main_menu, mainMenuFragment).commit();

		//主视图的Fragment添加
		getSupportFragmentManager().beginTransaction().replace(R.id.main_content, tabFragment).commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		int currentTabPosition = tabFragment.getCurrentTabPosition();
		showPostButton(currentTabPosition == 0 ? false : true);
		if (postClicked) {
			tabFragment.reloadTab(currentTabPosition);
			postClicked = false;
		}
	}

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			if (mDoubleBackQuit) {
				finish();
				return;
			}

			mDoubleBackQuit = true;
			showToast("再点一次返回键退出");

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mDoubleBackQuit = false;
				}
			}, 4000);
		}
	}

	protected void showPostButton(final boolean show) {
		if (show) {
			btnPost.setVisibility(View.VISIBLE);
		} else {
			btnPost.setVisibility(View.INVISIBLE);
		}
	}

	protected void gotoLoginView() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	
}
