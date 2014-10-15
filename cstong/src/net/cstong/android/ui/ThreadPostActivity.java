package net.cstong.android.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ForumInfo;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.api.PostApi;
import net.cstong.android.api.PostApi.PostResult;
import net.cstong.android.api.PostApi.TopicTypeList;
import net.cstong.android.api.PostApi.UploadInfo;
import net.cstong.android.ui.adapter.ListPopAdapter;
import net.cstong.android.ui.photo.PostViewPhotoAdapter;
import net.cstong.android.ui.widget.ScrollGridView;
import net.cstong.android.util.Constant;
import net.cstong.android.util.EmotionUtil;
import net.cstong.android.util.FileUtil;
import net.cstong.android.util.ImageUtil;
import net.cstong.android.util.Utils;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ab.activity.AbActivity;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.model.AbMenuItem;
import com.ab.util.AbDateUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;

public class ThreadPostActivity extends AbActivity {
	protected String TAG = "ThreadPostActivity";
	public HashMap<Integer, Integer> defaultTopicType = new HashMap<Integer, Integer>();

	protected Button publishBtn;
	protected Button takePhotoBtn;
	protected Button addPhotoBtn;
	protected Button emotionBtn;
	//protected Button selectSubtypeBtn;
	protected int defaultSubtypeBtn;
	protected EditText postContent;
	protected TextView postTitle;
	protected ScrollGridView mGridview;

	protected PostViewPhotoAdapter adapter;
	protected ForumInfo forumInfo = null;
	protected ArrayList<String> localImgs = new ArrayList<String>();
	protected ArrayList<String> imgUrls = new ArrayList<String>();
	protected String takenPhotoFilename = null;
	protected File mCurrentPhotoFile = null;
	protected Uri fileUri = null;
	protected ArrayList<File> tmpFileList = new ArrayList<File>();
	protected int uploadedFiles = 0;
	protected PostResult postResult = null;
	protected List<AbMenuItem> topicTypes = new ArrayList<AbMenuItem>();
	protected ListPopAdapter mListPopAdapter = null;
	protected TopicTypeList topicTypeList = null;
	protected int selectedTopicId = -1;

	ArrayList<ImageView> pointList = null;
	ArrayList<ArrayList<HashMap<String, Object>>> listGrid = null;
	protected ViewFlipper viewFlipper = null;
	protected RelativeLayout faceLayout = null;
	protected LinearLayout pagePoint = null;
	protected LinearLayout fillGapLinear = null;

	private boolean expanded = false;
	private boolean moveable = true;
	private float startX = 0;
	protected static int EMOTION_PER_PAGE = 20;
	protected String contentWithSign = null;
	private boolean sending = false;
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
		// 设置布局
		setAbContentView(R.layout.activity_thread_post);
		getWindow().setBackgroundDrawableResource(R.color.white);
		Bundle bundle = getIntent().getExtras();
		// method 2
		forumInfo = (ForumInfo) bundle.getSerializable(Constant.KEY_FORUMINFO);
		initTitleBar();
		postTitle = (TextView) findViewById(R.id.title);
		postContent = (EditText) findViewById(R.id.content);
		postContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start,
					final int before, final int count) {
				String str = postContent.getText().toString().trim();
				int length = str.length();
				if (length > 0) {
				} else {
				}
			}

			@Override
			public void beforeTextChanged(final CharSequence s,
					final int start, final int count, final int after) {}

			@Override
			public void afterTextChanged(final Editable s) {}
		});

		mGridview = (ScrollGridView) findViewById(R.id.gridPhoto);
		mGridview.setVisibility(View.GONE);
		adapter = new PostViewPhotoAdapter(this, localImgs, mGridview);
		mGridview.setAdapter(adapter);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getPostVar();
			}
		}, 500);

		listGrid = new ArrayList<ArrayList<HashMap<String, Object>>>();
		pointList = new ArrayList<ImageView>();
		addFaceData();

		
		initPostBar();
	}

	public void initTitleBar() {
		String title = Constant.forumNames.get(forumInfo.fid);
		if (title == null) {
			title = "长沙通";
		}
		Utils.initTitleBarLeft(this, R.drawable.button_selector_back, title, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		View postView = mInflater.inflate(R.layout.btn_publish, null);
		getTitleBar().addRightView(postView);
		publishBtn = (Button) postView.findViewById(R.id.btnPublish);
		publishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				
				post();
			}
		});
	}

	public void initPostBar() {
		/**
		final AbBottomBar mAbBottomBar = getBottomBar();
		mAbBottomBar.setVisibility(View.VISIBLE);
		View view = mInflater.inflate(R.layout.thread_post_bar, null);
		mAbBottomBar.setBottomView(view);
		**/

		addPhotoBtn = (Button) findViewById(R.id.addphoto);
		addPhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				try {
					Intent intent = new Intent(ThreadPostActivity.this, ShowImageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(Constant.KEY_FORUMINFO, forumInfo);
					intent.putExtras(bundle);
					startActivityForResult(intent, Constant.INTENTRESULT_ADD_PHOTO);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		takePhotoBtn = (Button) findViewById(R.id.takephoto);
		takePhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				try {
					if (!AbFileUtil.isCanUseSD()) {
						showToast("请插入SD卡");
						return;
					}
					Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
					startActivityForResult(intent, Constant.INTENTRESULT_TAKE_PHOTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		emotionBtn = (Button) findViewById(R.id.emotion);
		emotionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				if (expanded) {
					setFaceLayoutExpandState(false);
					expanded = false;

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

					/**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
					*再次打开ViewFlipper时画面在动的结果,
					*为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout
					*最上面添加了一个1dip高的黑色色块
					*/
				} else {
					setFaceLayoutExpandState(true);
					expanded = true;
					setPointEffect(0);
				}
			}
		});

		/**
		selectSubtypeBtn = (Button) findViewById(R.id.select_subtype);
		selectSubtypeBtn.setText(forumInfo.title);
		final View popView = mInflater.inflate(R.layout.list_pop_post_topictype, null);
		mListPopAdapter = new ListPopAdapter(ThreadPostActivity.this, topicTypes, R.layout.item2_list_pop);
		final ListView popListView = (ListView) popView.findViewById(R.id.pop_list);
		popListView.setAdapter(mListPopAdapter);
		popListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {
				int position = arg2;
				selectedTopicId = (int) arg3;
				selectSubtypeBtn.setText(((AbMenuItem) mListPopAdapter.getItem(position)).getText());
			}
		});
		selectSubtypeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				getBottomBar().setDropDown(selectSubtypeBtn, popView);
			}
		});
		**/

		viewFlipper = (ViewFlipper) findViewById(R.id.faceFlipper);
		faceLayout = (RelativeLayout) findViewById(R.id.faceLayout);
		pagePoint = (LinearLayout) findViewById(R.id.pagePoint);
		fillGapLinear = (LinearLayout) findViewById(R.id.fill_the_gap);
		addGridView();
	}

	/**
	 * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent mIntent) {
		super.onActivityResult(requestCode, resultCode, mIntent);
		if (resultCode != RESULT_OK) {
			showToast("没有选中图片");
			return;
		}
		ArrayList<String> files = new ArrayList<String>();
		switch (requestCode) {
		case Constant.INTENTRESULT_ADD_PHOTO:
			Bundle bundle = mIntent.getExtras();
			String[] images = bundle.getStringArray(ShowImageActivity.KEY_INTENT_RESULT);
			for (String path : images) {
				String[] paths = path.split(Constant.PATH_SEPERATOR);
				files.add(paths[0]);
			}
			upload(files);
			break;
		case Constant.INTENTRESULT_TAKE_PHOTO:
			String[] projection = { MediaStore.MediaColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.Media.DATA };
			Cursor c = getContentResolver().query(fileUri, projection, null, null, null);
			c.moveToFirst();
			files.add(c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
			upload(files);
			break;
		/**
		 * case CAMERA_CROP_DATA: String path = mIntent.getStringExtra("PATH");
		 * if (D) { Log.d(TAG, "裁剪后得到的图片的路径是 = " + path); }
		 * mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, path);
		 * camIndex++; AbViewUtil.setAbsListViewHeight(mGridView, 3, 25); break;
		 **/
		}
	}

	protected void upload(final ArrayList<String> files) {
		AbRequestParams params = new AbRequestParams();
		params.put("fid", String.valueOf(forumInfo.fid));
		uploadedFiles = 0;
		for (int i = 0; i < files.size(); i++) {
			final String filename = files.get(i);
			File fileNew = null;
			File file = new File(filename);
			Bitmap bmp = null;
			try {
				Bitmap.CompressFormat compressFormat;
				if (filename.endsWith(".png") || filename.endsWith(".PNG")) {
					compressFormat = Bitmap.CompressFormat.PNG;
				} else {
					compressFormat = Bitmap.CompressFormat.JPEG;
				}
//				bmp = AbFileUtil.getBitmapFromSD(file, AbImageUtil.SCALEIMG,Constant.PHOTO_GENERAL_WIDTH,Constant.PHOTO_GENERAL_HEIGHT);
				bmp = FileUtil.getbitmap(file, Constant.PHOTO_GENERAL_WIDTH, Constant.PHOTO_GENERAL_HEIGHT);
				bmp = ImageUtil.compressImage(bmp,compressFormat,50);
				String filenameNew = filename.substring(0, filename.lastIndexOf(".")) + AbDateUtil.getCurrentDate("yyyyMMddHHmmss") + filename.substring(filename.lastIndexOf("."));
				byte[] bmpBytes = AbImageUtil.bitmap2Bytes(bmp, compressFormat, true);
				AbFileUtil.writeByteArrayToSD(filenameNew, bmpBytes, true);
				fileNew = new File(filenameNew);
				params.put("upload", fileNew, "multipart/form-data");
				tmpFileList.add(fileNew);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			PostApi.upload(ThreadPostActivity.this, params, new AbStringHttpResponseListener() {
				@Override
				public void onSuccess(final int statusCode, final String content) {
//					Log.d(TAG, "onSuccess:" + content);
					UploadInfo info = PostApi.parseUploadResponse(content);
					if (info != null && info.getResultCode() == 0) {
						imgUrls.add(info.path);
						localImgs.add(filename);
					}
				}

				// 开始执行前
				@Override
				public void onStart() {
					showToast("开始上传图片...");
				}

				@Override
				public void onFailure(final int statusCode, final String content, final Throwable error) {
//					showToast(error.getMessage());
					showToast("图片上传失败，请重试");
				}

				// 进度
				@Override
				public void onProgress(final int bytesWritten, final int totalSize) {}

				// 完成后调用，失败，成功，都要调用
				@Override
				public void onFinish() {
					Log.d(TAG, "onFinish");
					uploadedFiles++;
					if (uploadedFiles == files.size()) {
						showToast("图片上传完成");
						for (int i = 0; i < tmpFileList.size(); i++) {
							tmpFileList.get(i).delete();
						}
					}
					if (mGridview.getVisibility() != View.VISIBLE) {
						mGridview.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				};
			});
		}
	}

	protected void post() {
		if(sending){
			Log.w(TAG, "sending");
			return;
		}
		final AbRequestParams postParams = new AbRequestParams();
		// TODO: debug
		int fid = forumInfo.fid; // 209 回收站发帖测试
		postParams.put("fid", String.valueOf(fid));
		if (selectedTopicId == -1 && fid != 209) {
			sending = false;
			publishBtn.setEnabled(true);
			showToast("请选择主题分类");
			return;
		}
		if (fid != 209) {
			postParams.put("topictype", String.valueOf(selectedTopicId));
		}
		String title = postTitle.getText().toString().trim();
		if (title.length() == 0) {
			sending = false;
			publishBtn.setEnabled(true);
			showToast("发帖主题不能为空");
			return;
		}
		String content = postContent.getText().toString().trim();
		// TODO: 暂时将上传图片附加到内容尾部
		for (int i = 0; i < imgUrls.size(); i++) {
			content += "[img]" + imgUrls.get(i) + "[/img]";
		}
		if (content.length() == 0) {
			sending = false;
			publishBtn.setEnabled(true);
			showToast("发帖内容不能为空");
			return;
		}
		postParams.put("atc_content", content);
		postParams.put("atc_title", title);
		postParams.put("reply_notice", "1");
		
		postResult = null;
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				PostApi.post(ThreadPostActivity.this, postParams, new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(final int statusCode, final String content) {
//						Log.d(TAG, "onSuccess:" + content);
						postResult = PostApi.parsePostResponse(content);
						if (postResult.getResultCode() == 0) {
							ThreadPostActivity.this.showToast("发帖成功");
						} else {
							ThreadPostActivity.this.showToast(postResult.getResultMsg());
						}
					}

					// 开始执行前
					@Override
					public void onStart() {
						Log.d(TAG, "onStart");
						sending = true;
						publishBtn.setEnabled(false);
						
						// 显示进度框
						ThreadPostActivity.this.showProgressDialog();
					}

					// 失败，调用
					@Override
					public void onFailure(final int statusCode, final String content, final Throwable error) {
						ThreadPostActivity.this.showToast("发帖失败，请稍后再试");
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						Log.d(TAG, "onFinish");
						// 移除进度框
						ThreadPostActivity.this.removeProgressDialog();
						if (postResult!=null&&postResult.getResultCode() == 0) {
							Intent intent = new Intent(ThreadPostActivity.this, ThreadReadActivity.class);
							ThreadInfo threadInfo = new ThreadInfo();
							threadInfo.fid = postResult.fid;
							threadInfo.tid = postResult.tid;
							// method 2
							Bundle bundle = new Bundle();
							bundle.putSerializable(Constant.KEY_THREADINFO, threadInfo);
							intent.putExtras(bundle);
							startActivity(intent);
							ThreadPostActivity.this.finish();
						}
						sending = false;
						publishBtn.setEnabled(true);
					}
				});
				
			}
		});
		
	}

	protected void getPostVar() {
		PostApi.getPostVar(this, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(final int statusCode, final String content) {
//				Log.d(TAG, "onSuccess:" + content);
				topicTypeList = PostApi.parseGetPostVarResponse(content, forumInfo.fid);
			}

			// 开始执行前
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
			}

			@Override
			public void onFailure(final int statusCode, final String content, final Throwable error) {
				showToast(error.getMessage());
			}

			// 进度
			@Override
			public void onProgress(final int bytesWritten, final int totalSize) {}

			// 完成后调用，失败，成功，都要调用
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				/**
				if (topicTypeList.getResultCode() == 0) {
					for (int i = 0; i < topicTypeList.typeList.size(); i++) {
						AbMenuItem menu = new AbMenuItem();
						menu.setId(topicTypeList.typeList.get(i).topicId);
						menu.setText(topicTypeList.typeList.get(i).topicName);
						topicTypes.add(menu);
					}
				}
				**/
				if (topicTypeList!=null&&topicTypeList.getResultCode() == 0) {
					selectedTopicId = getDefaultTopicType();
				} else {
					showToast("获取板块分类失败");
				}
			};
		});
	}

	private void addGridView() {
		for (int i = 0; i < listGrid.size(); i++) {
			View view = LayoutInflater.from(this).inflate(R.layout.view_item, null);
			GridView gv = (GridView) view.findViewById(R.id.myGridView);
			//gv.setNumColumns(5);
			gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
			MyGridAdapter adapter = new MyGridAdapter(this, listGrid.get(i), R.layout.griditem_emotion, new String[] { "image" }, new int[] { R.id.emotionGridImage });
			gv.setAdapter(adapter);
			gv.setOnTouchListener(new MyTouchListener(viewFlipper));
			viewFlipper.addView(view);
			//	ImageView image=new ImageView(this);
			//	ImageView image=(ImageView)LayoutInflater.from(this).inflate(R.layout.image_point_layout, null);
			/**
			 * 这里不喜欢用Java代码设置Image的边框大小等，所以单独配置了一个Imageview的布局文件
			 */
			View pointView = LayoutInflater.from(this).inflate(R.layout.point_image_layout, null);
			ImageView image = (ImageView) pointView.findViewById(R.id.pointImageView);
			//image.setBackgroundResource(R.drawable.qian_point);
			pagePoint.addView(pointView);
			/**
			 * 这里验证了LinearLayout属于ViewGroup类型，可以采用addView 动态添加view
			 */

			pointList.add(image);
			/**
			 * 将image放入pointList，便于修改点的颜色
			 */
		}

	}

	private void setFaceLayoutExpandState(final boolean isexpand) {
		if (isexpand == false) {
			viewFlipper.setDisplayedChild(0);
			ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
			params.height = 1;
			faceLayout.setLayoutParams(params);
			/**
			 * height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
			 * 再次打开ViewFlipper时画面在动的结果, 为了避免因为1dip的高度产生一个白缝，
			 * 所以这里在ViewFlipper所在的RelativeLayout中ViewFlipper 上层添加了一个1dip高的黑色色块
			 * viewFlipper必须在屏幕中有像素才能执行setDisplayedChild()操作
			 */
			//emotionBtn.setBackgroundResource(R.drawable.chat_bottom_look);
		} else {
			/**
			 * 让软键盘消失
			 */
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ThreadPostActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			faceLayout.setLayoutParams(params);
			//emotionBtn.setBackgroundResource(R.drawable.chat_bottom_keyboard);
		}
	}

	/**
	 * 设置游标（小点）的显示效果
	 * 
	 * @param darkPointNum
	 */
	private void setPointEffect(final int darkPointNum) {
		for (int i = 0; i < pointList.size(); i++) {
			pointList.get(i).setBackgroundResource(R.drawable.point_gray);
		}
		pointList.get(darkPointNum).setBackgroundResource(R.drawable.point_black);
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

			/**
			 * 这里把表情对应的名字也添加进数据对象中，便于在点击时获得表情对应的名称
			 */
			listGrid.get(i / EMOTION_PER_PAGE).add(map);
		}
		System.out.println("listGrid size is " + listGrid.size());
	}

	/**
	 * GridViewAdapter
	 * @param textView
	 * @param text
	 */
	class MyGridAdapter extends BaseAdapter {
		Context context;
		ArrayList<HashMap<String, Object>> list;
		int layout;
		String[] from;
		int[] to;

		public MyGridAdapter(final Context context, final ArrayList<HashMap<String, Object>> list, final int layout, final String[] from, final int[] to) {
			super();
			this.context = context;
			this.list = list;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(final int position) {
			return null;
		}

		@Override
		public long getItemId(final int position) {
			return position;
		}

		class ViewHolder {
			ImageView image = null;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(layout, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(to[0]);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.image.setImageBitmap(EmotionUtil.getEmotionBitmap(context, (String) list.get(position).get("faceName")));
			class MyGridImageClickListener implements OnClickListener {
				int position;

				public MyGridImageClickListener(final int position) {
					super();
					this.position = position;
				}

				@Override
				public void onClick(final View v) {
					postContent.append((String) list.get(position).get("faceName"));
				}
			}
			//这里创建了一个方法内部类
			holder.image.setOnClickListener(new MyGridImageClickListener(position));

			return convertView;
		}
	}

	/**
	 * 用到的方法 viewFlipper.getDisplayedChild()  获得当前显示的ChildView的索引
	 * @author Administrator
	 *
	 */
	class MyTouchListener implements OnTouchListener {
		ViewFlipper viewFlipper = null;

		public MyTouchListener(final ViewFlipper viewFlipper) {
			super();
			this.viewFlipper = viewFlipper;
		}

		@Override
		public boolean onTouch(final View v, final MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				moveable = true;
				break;
			case MotionEvent.ACTION_MOVE:
				if (moveable) {
					if (event.getX() - startX > 60) {
						moveable = false;
						int childIndex = viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if (childIndex > 0) {
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ThreadPostActivity.this, R.anim.left_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ThreadPostActivity.this, R.anim.right_out));
							viewFlipper.showPrevious();
							setPointEffect(childIndex - 1);
						}
					} else if (event.getX() - startX < -60) {
						moveable = false;
						int childIndex = viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if (childIndex < listGrid.size() - 1) {
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ThreadPostActivity.this, R.anim.right_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ThreadPostActivity.this, R.anim.left_out));
							viewFlipper.showNext();
							setPointEffect(childIndex + 1);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				moveable = true;
				break;
			default:
				break;
			}
			return false;
		}
	}

	/**
	 * 如果APP无法绕过分类，就都默认发到某一个分类。

	购物情报站——>其他
	团购集中营——>杂类其它
	我的穿搭秀——>其他
	装修那些事——>其他
	亲子俱乐部——>其他
	我为结婚忙——>其他
	美食分享汇——>大杂烩
	越策越开心——>其他

	 * @return 分类id
	 */
	protected int getDefaultTopicType() {
		if(topicTypeList.typeList.size()>0){
			return topicTypeList.typeList.get(topicTypeList.typeList.size() - 1).topicId;
		}else{
			return -1;
		}
		
	}
}