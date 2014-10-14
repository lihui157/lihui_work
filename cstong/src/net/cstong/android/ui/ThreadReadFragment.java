package net.cstong.android.ui;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi;
import net.cstong.android.api.ForumApi.ThreadDetail;
import net.cstong.android.api.ForumApi.ThreadDetailList;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.api.PostApi;
import net.cstong.android.ui.adapter.ThreadDetailAdapter;
import net.cstong.android.util.Constant;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.model.AbResult;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.ab.view.sliding.AbBottomTabView;

public class ThreadReadFragment extends Fragment {
	protected static final String TAG = "ThreadReadFragment";
	protected ThreadReadActivity mActivity = null;
	public ThreadInfo threadInfo = null;
	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;
	private ThreadDetailAdapter myListViewAdapter = null;
	protected ThreadDetailList threadDetails = new ThreadDetailList();
	protected ThreadDetailList threadDetailsNew = new ThreadDetailList();
	protected int pageIndex = 1; // 下次翻页的页码
	protected int pageSize = 20; // 翻页条数
	protected int prevCount = 20;

	protected int currentTab = -1;
	protected static final int TAB_PHOTO = 0;
	protected static final int TAB_EMOTION = 1;
	protected boolean emotionExpanded = false;
	protected boolean photoExpanded = false;
	protected String replyPrefix = null;

	protected Button sendBtn;
	protected Button openPhotoBtn;
	protected Button emotionBtn;
	public EditText postContent;
	protected ImageButton clearContentBtn;

	private AbBottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;

	protected ThreadReadEmotionFragment emotionFragment = null;
	protected ThreadReadPhotoFragment photoFragment = null;

	protected LinearLayout photoLayout;
	protected LinearLayout emotionLayout;
	private boolean sending = false;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};

	/**
	protected Button takePhotoBtn;
	protected Button addPhotoBtn;

	private boolean expanded = false;
	private boolean photoExpanded = false;
	 **/

	/**
		private boolean moveable = true;
		private float startX = 0;
		protected static int EMOTION_PER_PAGE = 20;
		protected static int PHOTO_PER_PAGE = 4;
		protected String contentWithSign = null;

		ArrayList<ImageView> pointList = null;
		ArrayList<ImageView> photopointList = null;
		ArrayList<ArrayList<HashMap<String, Object>>> listGrid = null;
		ArrayList<ArrayList<HashMap<String, Object>>> photolistGrid = null;
		protected ViewFlipper viewFlipper = null;
		protected ViewFlipper photoFlipper = null;
		protected LinearLayout faceLayout = null;
		protected LinearLayout photoLayout = null;
		protected LinearLayout photoButtonsLayout = null;
		protected LinearLayout emotionLayout = null;
		protected LinearLayout pagePoint = null;
		protected LinearLayout photoPagePoint = null;
		protected LinearLayout fillGapLinear = null;
		protected ScrollGridView mGridview;
		protected String takenPhotoFilename = null;
		protected File mCurrentPhotoFile = null;
		protected PostResult postResult = null;
		protected PostViewPhotoAdapter adapter;
		protected ForumInfo forumInfo = null;

		protected ArrayList<String> localImgs = new ArrayList<String>();
		protected ArrayList<String> imgUrls = new ArrayList<String>();
		public Uri fileUri = null;
		public ArrayList<File> tmpFileList = new ArrayList<File>();
		public int uploadedFiles = 0;
	 **/

	public static ThreadReadFragment newInstance(final ThreadInfo threadInfo) {
		ThreadReadFragment fragment = new ThreadReadFragment();
		fragment.threadInfo = threadInfo;
		if(threadInfo!=null){
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.KEY_THREADINFO, threadInfo);
			fragment.setArguments(bundle);
		}
		return fragment;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_thread_detail_list, null);
		mActivity = (ThreadReadActivity) getActivity();
		mActivity.getWindow().setBackgroundDrawableResource(R.color.white);

		photoLayout = (LinearLayout) view.findViewById(R.id.fragment_photo);
		emotionLayout = (LinearLayout) view.findViewById(R.id.fragment_emotion);

		FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
		photoFragment = ThreadReadPhotoFragment.newInstance(threadInfo);
		emotionFragment = ThreadReadEmotionFragment.newInstance(threadInfo);
		transaction.replace(R.id.fragment_photo, photoFragment);
		transaction.replace(R.id.fragment_emotion, emotionFragment);
		transaction.commit();

		// 获取ListView对象
		mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);
		mListView = (ListView) view.findViewById(R.id.mListView);

		// 设置进度条的样式
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(getResources().getDrawable(R.drawable.progress_circular));
		mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(getResources().getDrawable(R.drawable.progress_circular));

		// 使用自定义的Adapter
		myListViewAdapter = new ThreadDetailAdapter(mActivity, threadDetails,handler);
		mListView.setAdapter(myListViewAdapter);

		// item被点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				Log.d(TAG, "id:" + id + " position:" + position + " view:" + view.toString());
				ThreadDetail threadDetail = (ThreadDetail) myListViewAdapter.getItem(position);
				AbRequestParams params = mActivity.replyParams;
				if (position == 0) {
					params.put("_getHtml", String.valueOf(1));
					params.remove("pid");
				} else {
					params.put("_getHtml", String.valueOf(2));
					params.put("pid", String.valueOf(threadDetail.pid));
					replyPrefix = "回复" + threadDetail.createdUsername + ":";
					postContent.setText(replyPrefix);
					postContent.setSelection(replyPrefix.length());
				}
				params.put("lou", String.valueOf(position));
			}
		});

		// 设置监听器
		mAbPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(final AbPullToRefreshView view) {
				 getThreadDetail();
				 mAbPullToRefreshView.onHeaderRefreshFinish();
			}
		});
		mAbPullToRefreshView.setOnFooterLoadListener(new OnFooterLoadListener() {
			@Override
			public void onFooterLoad(final AbPullToRefreshView view) {
				getThreadDetail();
				mAbPullToRefreshView.onFooterLoadFinish();
			}
		});

		/**
		listGrid = new ArrayList<ArrayList<HashMap<String, Object>>>();
		photolistGrid = new ArrayList<ArrayList<HashMap<String, Object>>>();
		pointList = new ArrayList<ImageView>();
		photopointList = new ArrayList<ImageView>();
		addFaceData();
		 **/

		initFragmentTabs(view);

		return view;
	}
	
	public void onItemClick(final int position) {
//		Log.d(TAG, "id:" + id + " position:" + position + " view:" + view.toString());
		ThreadDetail threadDetail = (ThreadDetail) myListViewAdapter.getItem(position);
		AbRequestParams params = mActivity.replyParams;
		if (position == 0) {
			params.put("_getHtml", String.valueOf(1));
			params.remove("pid");
		} else {
			params.put("_getHtml", String.valueOf(2));
			params.put("pid", String.valueOf(threadDetail.pid));
			replyPrefix = "回复" + threadDetail.createdUsername + ":";
			postContent.setText(replyPrefix);
			postContent.setSelection(replyPrefix.length());
		}
		params.put("lou", String.valueOf(position));
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		getThreadDetail();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.gc();
		Runtime.getRuntime().gc();
	}

	public void initReplyBar(final View view) {
		/**
		AbBottomBar mAbBottomBar = getBottomBar();
		View view =mInflater.inflate(R.layout.thread_reply_bar, null);
		 **/

		/**
		openPhotoBtn = (Button) view.findViewById(R.id.reply_openphoto);
		openPhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				if (photoExpanded) {
					switchTab(0, false);
					InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				} else {
					switchTab(0, true);
					setPointEffect(0);
				}
			}
		});
		 **/

		/**
		addPhotoBtn = (Button) view.findViewById(R.id.reply_addphoto);
		addPhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				try {
					Intent intent = new Intent(mActivity, ShowImageActivity.class);
					startActivityForResult(intent, Constant.INTENTRESULT_ADD_PHOTO);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		takePhotoBtn = (Button) view.findViewById(R.id.reply_takephoto);
		takePhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				try {
					if (!AbFileUtil.isCanUseSD()) {
						mActivity.showToast("请插入SD卡");
						return;
					}
					Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
					startActivityForResult(intent, Constant.INTENTRESULT_TAKE_PHOTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		 **/

		/**
		emotionBtn = (Button) view.findViewById(R.id.reply_emotion);
		emotionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (expanded) {
					switchTab(1, false);
					InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					switchTab(1, true);
					setPointEffect(0);
				}
			}
		});
		postContent = (EditText) view.findViewById(R.id.reply_content);
		clearContentBtn = (ImageButton) view.findViewById(R.id.clearContent);
		postContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				String str = postContent.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearContentBtn.setVisibility(View.VISIBLE);
					clearContentBtn.postDelayed(new Runnable() {
						@Override
						public void run() {
							clearContentBtn.setVisibility(View.INVISIBLE);
						}
					}, 5000);
				} else {
					clearContentBtn.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

			@Override
			public void afterTextChanged(final Editable s) {}
		});
		clearContentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				postContent.setText("");
			}
		});
		 **/

		/**
		viewFlipper = (ViewFlipper) view.findViewById(R.id.faceFlipper);
		photoFlipper = (ViewFlipper) view.findViewById(R.id.photoFlipper);
		faceLayout = (LinearLayout) view.findViewById(R.id.faceLayout);
		pagePoint = (LinearLayout) view.findViewById(R.id.pagePoint);
		photoPagePoint = (LinearLayout) view.findViewById(R.id.photoPagePoint);
		fillGapLinear = (LinearLayout) view.findViewById(R.id.fill_the_gap);
		photoLayout = (LinearLayout) view.findViewById(R.id.ll_photo_flipper);
		photoButtonsLayout = (LinearLayout) view.findViewById(R.id.ll_photo_buttons);
		emotionLayout = (LinearLayout) view.findViewById(R.id.ll_emotion);
		addGridView();
		addPhotoGridView();
		 **/
		/**
		 * mAbBottomBar.setVisibility(View.VISIBLE);
		 * mAbBottomBar.setBottomView(view);
		 **/
	}


	/**
	public void reply() {
		String content = postContent.getText().toString().trim();
		if (mActivity.replyParams.getUrlParams().get("lou") != "0") {
			content = content.substring(content.indexOf(":") + 1);
		}
		mActivity.replyParams.put("atc_content", content);
		PostApi.reply(mActivity, mActivity.replyParams, new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(final int statusCode, final String content) {
				Log.d(TAG, "onSuccess:" + content);
				AbResult result = PostApi.parseReplyResponse(content);
				if (result.getResultCode() == 0) {
					mActivity.showToast("回复成功");
				} else {
					mActivity.showToast(result.getResultMsg());
				}
			}

			// 开始执行前
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
				// 显示进度框
				mActivity.showProgressDialog();
			}

			// 失败，调用
			@Override
			public void onFailure(final int statusCode, final String content, final Throwable error) {
				mActivity.showToast("回复失败，请稍后再试");
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				// 移除进度框
				mActivity.removeProgressDialog();
			}
		});
		postContent.setText("");
	}
	 **/
	/**
	 * 设置游标（小点）的显示效果
	 * 
	 * @param darkPointNum
	 */

	/**
	private void setPointEffect(final int darkPointNum) {
		for (int i = 0; i < pointList.size(); i++) {
			pointList.get(i).setBackgroundResource(R.drawable.point_gray);
		}
		pointList.get(darkPointNum).setBackgroundResource(R.drawable.point_black);
	}

	private void setPhotoPointEffect(final int darkPointNum) {
		for (int i = 0; i < photopointList.size(); i++) {
			photopointList.get(i).setBackgroundResource(R.drawable.point_gray);
		}
		photopointList.get(darkPointNum).setBackgroundResource(R.drawable.point_black);

	}

	protected void addPhotoData() {
		ArrayList<HashMap<String, Object>> list = null;
		for (int i = 0; i < localImgs.size(); i++) {
			if (i % PHOTO_PER_PAGE == 0) {
				list = new ArrayList<HashMap<String, Object>>();
				photolistGrid.add(list);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", localImgs.get(i));
			map.put("faceName", "");

			photolistGrid.get(i / PHOTO_PER_PAGE).add(map);
		}
	}

	private void addFaceData() {
		ArrayList<HashMap<String, Object>> list = null;
		for (int i = 0; i < EmotionUtil.signs.length; i++) {
			if (i % EMOTION_PER_PAGE == 0) {
				list = new ArrayList<HashMap<String, Object>>();
				listGrid.add(list);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", EmotionUtil.getDefaultEmotions().get(EmotionUtil.signs[i]).filename);
			map.put("faceName", EmotionUtil.signs[i]);

			listGrid.get(i / EMOTION_PER_PAGE).add(map);
		}
		System.out.println("listGrid size is " + listGrid.size());
	}
	 **/

	protected void getThreadDetail() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				AbRequestParams params = new AbRequestParams();
				params.put("page", String.valueOf(pageIndex));
				params.put("pageSize", String.valueOf(pageSize));
				params.put("tid", String.valueOf(threadInfo.tid));
				params.put("isubb", String.valueOf(1));
				ForumApi.readThread(getActivity(), params, new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(final int statusCode, final String content) {
//						Log.d(TAG, "onSuccess:" + content);
						threadDetailsNew = ForumApi.parseReadThreadResponse(content);
					}

					// 开始执行前
					@Override
					public void onStart() {
						Log.d(TAG, "onStart");
						// 显示进度框
						mActivity.showProgressDialog();
					}

					// 失败，调用
					@Override
					public void onFailure(final int statusCode, final String content, final Throwable error) {
						mActivity.showToast("加载失败，请稍后再试");
						if(pageIndex==1){
							mActivity.finish();
						}
						
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						Log.d(TAG, "onFinish");
						// 移除进度框
						mActivity.removeProgressDialog();
						if (threadDetailsNew.detailList.size() > 0 && threadDetailsNew.pageTotal >= pageIndex) {
							if (prevCount == threadDetails.pageSize) {
								threadDetails.detailList.addAll(threadDetailsNew.detailList);
							} else {
								// 上次整页条数没取满
								int count = threadDetailsNew.detailList.size() - prevCount;
								if (count > 0) {
//									threadDetails.detailList.addAll(threadDetailsNew.detailList.subList(count, threadDetailsNew.detailList.size() - 1));
									threadDetails.detailList.addAll(threadDetailsNew.detailList.subList(count, threadDetailsNew.detailList.size()));
								}
							}
							threadDetails.pageTotal = threadDetailsNew.pageTotal;
							threadDetails.pageIndex = threadDetailsNew.pageIndex;
							threadDetails.pageSize = threadDetailsNew.pageSize;
							threadDetails.totalCount = threadDetailsNew.totalCount;
							prevCount = threadDetailsNew.detailList.size();
							if (threadDetailsNew.detailList.size() == threadDetailsNew.pageSize) {
								pageIndex++;
							}
							threadDetailsNew.detailList.clear();
							mAbPullToRefreshView.onFooterLoadFinish();
							myListViewAdapter.notifyDataSetChanged();
						} else {
							Log.d(TAG, "exceed totol page");
						}
					}
				});
				
			}
		});
		
	}
	
	/**
	 * 回复以后调用
	 */
	private void getThreadDetailByReply() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				AbRequestParams params = new AbRequestParams();
				params.put("page", String.valueOf(pageIndex));
				params.put("pageSize", String.valueOf(pageSize));
				params.put("tid", String.valueOf(threadInfo.tid));
				params.put("isubb", String.valueOf(1));
				ForumApi.readThread(getActivity(), params, new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(final int statusCode, final String content) {
//						Log.d(TAG, "onSuccess:" + content);
						threadDetailsNew = ForumApi.parseReadThreadResponse(content);
					}

					// 开始执行前
					@Override
					public void onStart() {
						Log.d(TAG, "onStart");
						// 显示进度框
						mActivity.showProgressDialog();
					}

					// 失败，调用
					@Override
					public void onFailure(final int statusCode, final String content, final Throwable error) {
						mActivity.showToast("加载失败，请稍后再试");
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						Log.d(TAG, "onFinish");
						// 移除进度框
						mActivity.removeProgressDialog();
						if (threadDetailsNew.detailList.size() > 0 && threadDetailsNew.pageTotal >= pageIndex) {
//							threadDetails.detailList.clear();
							if (prevCount == threadDetails.pageSize) {
								threadDetails.detailList.addAll(threadDetailsNew.detailList);
							} else {
								// 上次整页条数没取满
								int count = threadDetailsNew.detailList.size() - prevCount;
								if (count > 0) {
//									threadDetails.detailList.addAll(threadDetailsNew.detailList.subList(count, threadDetailsNew.detailList.size() - 1));
//									threadDetails.detailList.addAll(threadDetailsNew.detailList.subList(count, threadDetailsNew.detailList.size()));
									threadDetails.detailList.add(threadDetailsNew.detailList.get(threadDetailsNew.detailList.size()-1));
									
								}
							}
							threadDetails.pageTotal = threadDetailsNew.pageTotal;
							threadDetails.pageIndex = threadDetailsNew.pageIndex;
							threadDetails.pageSize = threadDetailsNew.pageSize;
							threadDetails.totalCount = threadDetailsNew.totalCount;
							prevCount = threadDetailsNew.detailList.size();
							if (threadDetailsNew.detailList.size() == threadDetailsNew.pageSize) {
								pageIndex++;
							}
							threadDetailsNew.detailList.clear();
							mAbPullToRefreshView.onFooterLoadFinish();
							myListViewAdapter.notifyDataSetChanged();
							mListView.setSelection(threadDetails.detailList.size()-1);
						} else {
							Log.d(TAG, "exceed totol page");
						}
					}
				});
				
			}
		});
		
	}

	/**
	 * protected void switchTab(final int tabType, final boolean isexpand) { if
	 * (tabType == 0) { if (isexpand) { setPhotoLayoutExpandState(true);
	 * setFaceLayoutExpandState(false); } else {
	 * setPhotoLayoutExpandState(false); } } else { if (isexpand) {
	 * setPhotoLayoutExpandState(false); setFaceLayoutExpandState(true); } else
	 * { setFaceLayoutExpandState(false); } } }
	 **/

	protected void toggleFragment(final int position, final boolean expand) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
				ViewGroup.LayoutParams params;
				if (expand) {
					if (position == 0) {
						transaction.hide(emotionFragment).show(photoFragment);
						// emotionFragment.expand(false);
						/**
						 * ViewGroup.LayoutParams params =
						 * emotionLayout.getLayoutParams(); params.height = 1;
						 * emotionLayout.setLayoutParams(params);
						 **/
						emotionLayout.setVisibility(View.GONE);

						// photoFragment.expand(true);
						params = photoLayout.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						photoLayout.setLayoutParams(params);
						photoLayout.setVisibility(View.VISIBLE);
					} else {
						transaction.hide(photoFragment).show(emotionFragment);
						// emotionFragment.expand(true);
						params = emotionLayout.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						emotionLayout.setLayoutParams(params);
						emotionLayout.setVisibility(View.VISIBLE);

						// photoFragment.expand(false);
						/**
						 * params = photoLayout.getLayoutParams(); params.height = 1;
						 * photoLayout.setLayoutParams(params);
						 **/
						photoLayout.setVisibility(View.GONE);
					}
				} else {
//					if (position == 0) {
						transaction.hide(photoFragment);
//					} else {
						transaction.hide(emotionFragment);
						
//					}
				}
				try {
					transaction.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}

	protected void initFragmentTabs(final View view) {
		/**
		mBottomTabView = (AbBottomTabView) view.findViewById(R.id.photoTabs);

		//缓存数量
		mBottomTabView.getViewPager().setOffscreenPageLimit(0);

		//禁止滑动
		mBottomTabView.getViewPager().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				return true;
			}
		});

		//设置样式
		mBottomTabView.setTabTextColor(Color.BLACK);
		mBottomTabView.setTabSelectColor(Color.rgb(255, 255, 255));
		//mBottomTabView.setTabTextColor(getResources().getColor(R.color.black));
		//mBottomTabView.setTabSelectColor(getResources().getColor(R.color.green));
		//mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg);
		//mBottomTabView.setTabLayoutBackgroundResource(R.drawable.slide_top);
		//mBottomTabView.setTabPadding(80, 8, 80, 8);
		//mBottomTabView.setMinimumWidth(200);
		mBottomTabView.getViewPager().setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					if (x < 45) {
						// photo
					} else {
						// emotion
					}
					Log.d(TAG, event.toString());
				}
				return true;
			}
		});
		mBottomTabView.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(final int position) {
				Log.d(TAG, "page pos:" + String.valueOf(position));
				if (position == 0) {
					//mActivity.showPostButton(false);
				} else {
					//mActivity.showPostButton(true);
				}
			}

			@Override
			public void onPageScrollStateChanged(final int arg0) {
				//Log.d(TAG, "page arg0:" + String.valueOf(arg0));
			}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
				//Log.d(TAG, "page arg0:" + String.valueOf(arg0) + " arg1:" + String.valueOf(arg1) + " arg2:" + String.valueOf(arg2));
			}
		});

		ThreadReadPhotoFragment page1 = ThreadReadPhotoFragment.newInstance(threadInfo);
		ThreadReadEmotionFragment page2 = ThreadReadEmotionFragment.newInstance(threadInfo);

		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(page1);
		mFragments.add(page2);

		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("");
		tabTexts.add("");

		//注意图片的顺序
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(R.drawable.reply_addphoto));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.emotion));

		//演示增加一组
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);
		 **/
		emotionBtn = (Button) view.findViewById(R.id.reply_emotion);
		emotionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				handler.post(new Runnable() {
					public void run() {
						if (currentTab == -1 || currentTab != TAB_EMOTION) {
							toggleFragment(TAB_EMOTION, true);
							emotionExpanded = true;
							InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
						} else {
							if (emotionExpanded) {
								toggleFragment(TAB_EMOTION, false);
							} else {
								toggleFragment(TAB_EMOTION, true);
							}
							emotionExpanded = !emotionExpanded;
						}
						currentTab = TAB_EMOTION;
					}
				});
				
			}
		});

		openPhotoBtn = (Button) view.findViewById(R.id.reply_openphoto);
		openPhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				handler.post(new Runnable() {
					public void run() {
						if (currentTab == -1 || currentTab != TAB_PHOTO) {
							toggleFragment(TAB_PHOTO, true);
							photoExpanded = true;
							InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
						} else {
							if (photoExpanded) {
								toggleFragment(TAB_PHOTO, false);
							} else {
								toggleFragment(TAB_PHOTO, true);
							}
							photoExpanded = !photoExpanded;
						}
						currentTab = TAB_PHOTO;
					}
				});
				
			}
		});
		postContent = (EditText) view.findViewById(R.id.reply_content);
		clearContentBtn = (ImageButton) view.findViewById(R.id.clearContent);
		postContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				String str = postContent.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
					clearContentBtn.setVisibility(View.VISIBLE);
					clearContentBtn.postDelayed(new Runnable() {
						@Override
						public void run() {
							clearContentBtn.setVisibility(View.INVISIBLE);
						}
					}, 5000);
				} else {
					clearContentBtn.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

			@Override
			public void afterTextChanged(final Editable s) {}
		});
		clearContentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				postContent.setText("");
			}
		});
		sendBtn = (Button) view.findViewById(R.id.reply_send);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				String content = postContent.getText().toString().trim();
				if (content.length() == 0) {
					mActivity.showToast("请输入回复内容");
					return;
				}
				reply();
			}
		});
	}

	public void reply() {
		if (Constant.myApp.mUser.cookie.length() == 0) {
			mActivity.showToast("请先登录");
			return;
		}
		
		if(sending){
			Log.w(TAG, "sending is true");
			return;
		}
		
		String content = postContent.getText().toString().trim();
//		String content = "";
		if (replyPrefix != null&&content.length()>replyPrefix.length()) {
			content = content.substring(replyPrefix.length());
		}
		for (int i = 0; i < photoFragment.imgUrls.size(); i++) {
			content += "[img]" + photoFragment.imgUrls.get(i) + "[/img]";
		}
//		content += postContent.getText().toString().trim();
		mActivity.replyParams.put("atc_content",  content);
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				PostApi.reply(mActivity, mActivity.replyParams, new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(final int statusCode, final String content) {
//						Log.d(TAG, "onSuccess:" + content);
						AbResult result = PostApi.parseReplyResponse(content);
						if (result.getResultCode() == 0) {
							mActivity.showToast("回复成功");
							postContent.setText("");
							toggleFragment(-1,false);
							photoFragment.clearData();
						} else {
							mActivity.showToast(result.getResultMsg());
						}
					}

					// 开始执行前
					@Override
					public void onStart() {
						Log.d(TAG, "onStart");
						sending = true;
						sendBtn.setText("发送中");
						sendBtn.setEnabled(false);
						// 显示进度框
						mActivity.showProgressDialog();
					}

					// 失败，调用
					@Override
					public void onFailure(final int statusCode, final String content, final Throwable error) {
						mActivity.showToast("回复失败，请稍后再试");
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						Log.d(TAG, "onFinish");
						
						InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
						// 移除进度框
						mActivity.removeProgressDialog();
						sending = false;
						sendBtn.setText(R.string.reply_send);
						sendBtn.setEnabled(true);
						mActivity.replyParams.remove("lou");
						mActivity.replyParams.remove("pid");
						mActivity.replyParams.remove("_getHtml");
						getThreadDetailByReply();
					}
				});
				
			}
		});
	}
}