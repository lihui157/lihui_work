package net.cstong.android.ui;

import java.util.ArrayList;

import net.cstong.android.R;
import net.cstong.android.ui.adapter.LeftMenuAdapter;
import net.cstong.android.ui.widget.CircularImageView;
import net.cstong.android.util.Constant;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.model.AbMenuItem;
import com.ab.util.AbImageUtil;

public class MainMenuFragment extends Fragment {
	private MainActivity mActivity = null;
	private ExpandableListView mMenuListView;
	private ArrayList<String> mGroupName = null;
	private ArrayList<ArrayList<AbMenuItem>> mChilds = null;
	private ArrayList<AbMenuItem> mChild1 = null;
	//private ArrayList<AbMenuItem> mChild2 = null;
	private LeftMenuAdapter mAdapter;
	private OnChangeViewListener mOnChangeViewListener;
	private TextView mNameText;
	//private TextView mUserPoint;
	private CircularImageView mUserPhoto;
	//private ImageView sunshineView;
	private AbImageDownloader mAbImageDownloader = null;
	private LinearLayout loginLayout = null;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mActivity = (MainActivity) getActivity();

		View view = inflater.inflate(R.layout.menu_main_items, null);
		mMenuListView = (ExpandableListView) view.findViewById(R.id.menu_list);

		mNameText = (TextView) view.findViewById(R.id.user_name);
		mUserPhoto = (CircularImageView) view.findViewById(R.id.user_photo);
		//mUserPoint = (TextView) view.findViewById(R.id.user_point);
		//sunshineView = (ImageView) view.findViewById(R.id.sunshineView);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);
		//Button cacheClearBtn = (Button) view.findViewById(R.id.cacheClearBtn);

		/**
		cacheClearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				mActivity.showProgressDialog("正在清空缓存...");
				AbTask task = new AbTask();
				//定义异步执行的对象
				final AbTaskItem item = new AbTaskItem();
				item.setListener(new AbTaskListener() {

					@Override
					public void update() {
						mActivity.removeProgressDialog();
						mActivity.showToast("缓存已清空完成");
					}

					@Override
					public void get() {
						try {
							AbFileUtil.removeAllFileCache();
							AbImageCache.removeAllBitmapFromCache();
						} catch (Exception e) {
							mActivity.showToastInThread(e.getMessage());
						}
					};
				});
				task.execute(item);

			}
		});
		**/

		mGroupName = new ArrayList<String>();
		mChild1 = new ArrayList<AbMenuItem>();
		//mChild2 = new ArrayList<AbMenuItem>();

		mChilds = new ArrayList<ArrayList<AbMenuItem>>();
		mChilds.add(mChild1);
		//mChilds.add(mChild2);

		mAdapter = new LeftMenuAdapter(mActivity, mGroupName, mChilds);
		mMenuListView.setAdapter(mAdapter);
		for (int i = 0; i < mGroupName.size(); i++) {
			mMenuListView.expandGroup(i);
		}

		mMenuListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, final long id) {
				return true;
			}
		});

		mMenuListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(final ExpandableListView parent, final View v,
					final int groupPosition, final int childPosition, final long id) {
				if (mOnChangeViewListener != null) {
					mOnChangeViewListener.onChangeView(groupPosition, childPosition);
				}
				return true;
			}
		});

		//图片的下载
		mAbImageDownloader = new AbImageDownloader(mActivity);
		mAbImageDownloader.setWidth(150);
		mAbImageDownloader.setHeight(150);

		initMenu();

		//startAnimation(sunshineView);

		mAbImageDownloader.setErrorImage(R.drawable.image_error);
		mAbImageDownloader.setNoImage(R.drawable.image_no);

		return view;
	}

	public interface OnChangeViewListener {
		public abstract void onChangeView(int groupPosition, int childPosition);
	}

	public void setOnChangeViewListener(final OnChangeViewListener onChangeViewListener) {
		mOnChangeViewListener = onChangeViewListener;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void initMenu() {
		mGroupName.clear();
		mChild1.clear();
		//mChild2.clear();

		mGroupName.add("");
		//mGroupName.add("操作");

		AbMenuItem m0 = new AbMenuItem();
		m0.setIconId(R.drawable.xiaoxi);
		m0.setText("消息");
		mChild1.add(m0);

		AbMenuItem m1 = new AbMenuItem();
		m1.setIconId(R.drawable.xiaowo1);
		m1.setText("我的小窝");
		mChild1.add(m1);

		AbMenuItem m2 = new AbMenuItem();
		m2.setIconId(R.drawable.tiezi1);
		m2.setText("我的帖子");
		mChild1.add(m2);

		AbMenuItem m3 = new AbMenuItem();
		m3.setIconId(R.drawable.sousuo1);
		m3.setText("搜索");
		mChild1.add(m3);

		AbMenuItem m4 = new AbMenuItem();
		m4.setIconId(R.drawable.shezhi1);
		m4.setText("设置");
		mChild1.add(m4);

		AbMenuItem m5 = new AbMenuItem();
		m5.setIconId(R.drawable.tuichu1);
		m5.setText("退出当前帐号");
		mChild1.add(m5);

		mAdapter.notifyDataSetChanged();
		for (int i = 0; i < mGroupName.size(); i++) {
			mMenuListView.expandGroup(i);
		}

		if (Constant.myApp.mUser.cookie.length() == 0) {
			setNameText("登录");
			setUserPhoto(R.drawable.photo01);
			setUserPoint("0");
			mNameText.setCompoundDrawables(null, null, null, null);
			loginLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View arg0) {
					if (Constant.myApp.mUser.cookie.length() == 0) {
						//mActivity.toLogin(mActivity.LOGIN_CODE);
					}
				}
			});
		} else {
			setNameText(Constant.myApp.mUser.username);
			downSetPhoto(Constant.myApp.mUser.avatar);

			loginLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View arg0) {

				}
			});
		}
		setOnChangeViewListener(new OnChangeViewListener() {
			@Override
			public void onChangeView(final int groupPosition, final int childPosition) {
				onMenuChanged(groupPosition, childPosition);
			}

		});

	}

	/**
	 * 描述：用户名的设置
	 * @param mNameText
	 */
	public void setNameText(final String mNameText) {
		this.mNameText.setText(mNameText);
	}

	/**
	 * 描述：设置用户阳光
	 * @param mPoint
	 */
	public void setUserPoint(final String mPoint) {
		//mUserPoint.setText(mPoint);
		//startAnimation(sunshineView);
	}

	public void downSetPhoto(final String mPhotoUrl) {
		//缩放图片的下载
		mAbImageDownloader.setNoImage(R.drawable.photo01);
		mAbImageDownloader.setErrorImage(R.drawable.photo01_error);
		mAbImageDownloader.setType(AbImageUtil.SCALEIMG);
		mAbImageDownloader.display(mUserPhoto, mPhotoUrl);
	}

	public void startAnimation(final ImageView v) {

		//创建AnimationSet对象
		AnimationSet animationSet = new AnimationSet(true);
		//创建RotateAnimation对象
		RotateAnimation rotateAnimation = new RotateAnimation(0f, +360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		//设置动画持续
		rotateAnimation.setDuration(2000);
		rotateAnimation.setRepeatCount(5);
		//rotateAnimation.setRepeatMode(Animation.RESTART);
		//动画插入器
		rotateAnimation.setInterpolator(mActivity, android.R.anim.decelerate_interpolator);
		//添加到AnimationSet
		animationSet.addAnimation(rotateAnimation);

		//开始动画 
		v.startAnimation(animationSet);
	}

	/**
	 * 描述：设置头像
	 * @param drawable
	 */
	public void setUserPhoto(final int resId) {
		mUserPhoto.setImageResource(resId);
	}

	public void onMenuChanged(final int groupPosition, final int childPosition) {
		if (groupPosition == 0) {
			if (childPosition == 0) {
				//我的消息
				if (Constant.myApp.mUser.cookie.length() == 0) {
					Intent intent = new Intent(mActivity, LoginActivity.class);
					mActivity.startActivity(intent);
				} else {
					Intent intent = new Intent(mActivity, MessageActivity.class);
					mActivity.startActivity(intent);
				}
			} else if (childPosition == 1) {
				//
				/**
				Intent intent = new Intent(mActivity, MessageActivity.class);
				startActivity(intent);
				**/
			} else if (childPosition == 2) {
				//程序案例
				/**
				Intent intent = new Intent(mActivity, DemoMainActivity.class);
				startActivity(intent);
				**/
			} else if (childPosition == 3) {
				//应用游戏
				//mActivity.showApp();
			} else if (childPosition == 4) {
			} else if (childPosition == 5) {
				//注销
				Constant.myApp.clearLoginParams();
				mActivity.onBackPressed();
			}
		} else if (groupPosition == 1) {
			if (childPosition == 0) {
				//选项、赞助作者
				//mActivity.showApp();
			} else if (childPosition == 1) {
				//推荐
			} else if (childPosition == 2) {
				if (Constant.myApp.mUser.cookie.length() > 0) {
					mActivity.showDialog("注销", "确定要注销该用户吗?", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog, final int which) {
							//注销
							Constant.myApp.clearLoginParams();
							initMenu();
							//mActivity.stopIMService();
						}
					});

				} else {
					//关于
					/**
					Intent intent = new Intent(mActivity, AboutActivity.class);
					startActivity(intent);
					**/
				}
			} else if (childPosition == 3) {
				if (Constant.myApp.mUser.cookie.length() > 0) {
					//关于
					/**
					Intent intent = new Intent(mActivity, AboutActivity.class);
					startActivity(intent);
					**/
				} else {
					//无
				}
			}
		}
	}
}
