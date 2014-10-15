package net.cstong.android.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.api.PostApi;
import net.cstong.android.api.PostApi.PostResult;
import net.cstong.android.api.PostApi.UploadInfo;
import net.cstong.android.ui.photo.PostViewPhotoAdapter;
import net.cstong.android.ui.widget.ScrollGridView;
import net.cstong.android.util.Constant;
import net.cstong.android.util.FileUtil;
import net.cstong.android.util.ImageUtil;
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
import android.support.v4.app.Fragment;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDateUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;

public class ThreadReadPhotoFragment extends Fragment {
	private final String TAG = "ThreadReadEmotionFragment";
	private ThreadReadActivity mActivity = null;
	private ThreadInfo threadInfo = null;

	private boolean moveable = true;
	private float startX = 0;
	protected static int PHOTO_PER_PAGE = 4;
	protected String contentWithSign = null;

	private boolean isExpanded = false;
	ArrayList<ImageView> photopointList = null;
	ArrayList<ArrayList<HashMap<String, Object>>> listGrid = null;
	protected ViewFlipper viewFlipper = null;
	protected RelativeLayout faceLayout = null;
	protected LinearLayout photoButtonsLayout = null;
	protected LinearLayout pagePoint = null;
	protected LinearLayout fillGapLinear = null;
	protected String takenPhotoFilename = null;
	protected File mCurrentPhotoFile = null;
	protected PostResult postResult = null;
	protected PostViewPhotoAdapter adapter;
	protected boolean uploadResult = false;
	public ScrollGridView mGridview;

	protected Button takePhotoBtn;
	protected Button addPhotoBtn;

	public ArrayList<String> localImgs = new ArrayList<String>();
	public ArrayList<String> imgUrls = new ArrayList<String>();
	public Uri fileUri = null;
	public ArrayList<File> tmpFileList = new ArrayList<File>();
	public int uploadedFiles = 0;
	public int prevUploaded = 0;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};

	// 记录列表第一个帖子id,下拉时更新
	protected int firstTid = 0;

	public static ThreadReadPhotoFragment newInstance(final ThreadInfo info) {
		ThreadReadPhotoFragment newFragment = new ThreadReadPhotoFragment();
		if (info != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.KEY_THREADINFO, info);
			newFragment.setArguments(bundle);
		}
		return newFragment;
	}
	
	public void clearData(){
		if(tmpFileList!=null) tmpFileList.clear();
		if(localImgs!=null) localImgs.clear();
		if(imgUrls!=null) imgUrls.clear();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mActivity = (ThreadReadActivity) getActivity();

		View view = inflater.inflate(R.layout.tab_photo, null);

		Bundle bundle = getArguments();
		threadInfo = (ThreadInfo) bundle.getSerializable(Constant.KEY_THREADINFO);

		listGrid = new ArrayList<ArrayList<HashMap<String, Object>>>();
		photopointList = new ArrayList<ImageView>();
		viewFlipper = (ViewFlipper) view.findViewById(R.id.faceFlipper);
		faceLayout = (RelativeLayout) view.findViewById(R.id.photoLayout);
		pagePoint = (LinearLayout) view.findViewById(R.id.pagePoint);
		fillGapLinear = (LinearLayout) view.findViewById(R.id.fill_the_gap);
		photoButtonsLayout = (LinearLayout) view.findViewById(R.id.ll_photo_buttons);

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
		
		mGridview = (ScrollGridView) view.findViewById(R.id.gridPhoto);
		mGridview.setVisibility(View.GONE);
		adapter = new PostViewPhotoAdapter(getActivity(), localImgs, mGridview);
		mGridview.setAdapter(adapter);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	protected void addPhotoData() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<HashMap<String, Object>> list = null;
				for (int i = prevUploaded - 1; i < localImgs.size(); i++) {
					if (i % PHOTO_PER_PAGE == 0) {
						list = new ArrayList<HashMap<String, Object>>();
						listGrid.add(list);
					}
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("image", localImgs.get(i));

					/**
					 * 这里把表情对应的名字也添加进数据对象中，便于在点击时获得表情对应的名称
					 */
					listGrid.get(i / PHOTO_PER_PAGE).add(map);
				}
			}
		});
		
	}

	private void setPhotoPointEffect(final int darkPointNum) {
		for (int i = 0; i < photopointList.size(); i++) {
			photopointList.get(i).setBackgroundResource(R.drawable.point_gray);
		}
		photopointList.get(darkPointNum).setBackgroundResource(R.drawable.point_black);

	}

	protected void addPhotoGridView() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = prevUploaded / PHOTO_PER_PAGE; i < listGrid.size(); i++) {
					View view = LayoutInflater.from(mActivity).inflate(R.layout.view_item_photo, null);
					GridView gv = (GridView) view.findViewById(R.id.myGridView);
					// gv.setNumColumns(4);
					gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
					MyGridAdapter adapter = new MyGridAdapter(mActivity, listGrid.get(i), R.layout.griditem_photo,
							new String[] { "image" }, new int[] { R.id.photoGridImage });
					gv.setAdapter(adapter);
					gv.setOnTouchListener(new MyTouchListener(viewFlipper));
					viewFlipper.addView(view);

					/**
					 * 这里不喜欢用Java代码设置Image的边框大小等，所以单独配置了一个Imageview的布局文件
					 */
					View pointView = LayoutInflater.from(mActivity).inflate(R.layout.point_image_layout, null);
					ImageView image = (ImageView) pointView.findViewById(R.id.pointImageView);
					// image.setBackgroundResource(R.drawable.qian_point);
					pagePoint.addView(pointView);
					/**
					 * 这里验证了LinearLayout属于ViewGroup类型，可以采用addView 动态添加view
					 */

					photopointList.add(image);
					/**
					 * 将image放入pointList，便于修改点的颜色
					 */
				}
			}
		});
		
	}

	protected void upload(final ArrayList<String> files) {
		AbRequestParams params = new AbRequestParams();
		params.put("fid", String.valueOf(threadInfo.fid));
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
//				bmp = AbFileUtil.getBitmapFromSD(file, AbImageUtil.SCALEIMG,
//						Constant.PHOTO_GENERAL_WIDTH,
//						Constant.PHOTO_GENERAL_HEIGHT);
				bmp = FileUtil.getbitmap(file, Constant.PHOTO_GENERAL_WIDTH, Constant.PHOTO_GENERAL_HEIGHT);
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
			prevUploaded = localImgs.size();
			uploadResult = false;
			PostApi.upload(mActivity, params, new AbStringHttpResponseListener() {
				@Override
				public void onSuccess(final int statusCode, final String content) {
//					Log.d(TAG, "onSuccess:" + content);
					UploadInfo info = PostApi.parseUploadResponse(content);
					if (info != null && info.getResultCode() == 0) {
						imgUrls.add(info.path);
						localImgs.add(filename);
						uploadResult = true;
						prevUploaded++;
					}
				}

				// 开始执行前
				@Override
				public void onStart() {
					mActivity.showToast("开始上传图片...");
					
				}

				@Override
				public void onFailure(final int statusCode, final String content, final Throwable error) {
					
					mActivity.showToast(error.getMessage());
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
						mActivity.showToast("图片上传完成");
						for (int i = 0; i < tmpFileList.size(); i++) {
							tmpFileList.get(i).delete();
						}
					}

					if (uploadResult) {
						addPhotoData();
						addPhotoGridView();
					}
					
//					adapter.notifyDataSetChanged();
				};
			});
		}
	}
	
	protected void upload_(final ArrayList<String> files) {
		AbRequestParams params = new AbRequestParams();
		params.put("fid", String.valueOf(threadInfo.fid));
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
//				bmp = AbFileUtil.getBitmapFromSD(file, AbImageUtil.SCALEIMG,
//						Constant.PHOTO_GENERAL_WIDTH,
//						Constant.PHOTO_GENERAL_HEIGHT);
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
			prevUploaded = localImgs.size();
			uploadResult = false;
			PostApi.upload(mActivity, params, new AbStringHttpResponseListener() {
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
					mActivity.showProgressDialog();
					mActivity.showToast("开始上传图片...");
					
				}

				@Override
				public void onFailure(final int statusCode, final String content, final Throwable error) {
					
//					mActivity.showToast(error.getMessage());
					mActivity.showToast("图片上传失败，请重试");
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
						mActivity.removeProgressDialog();
						mActivity.showToast("图片上传完成");
						for (int i = 0; i < tmpFileList.size(); i++) {
							tmpFileList.get(i).delete();
						}
					}

//					if (uploadResult) {
//						addPhotoData();
//						addPhotoGridView();
//					}
					if (mGridview.getVisibility() != View.VISIBLE) {
						mGridview.setVisibility(View.VISIBLE);
					}
					
					adapter.notifyDataSetChanged();
				};
			});
		}
	}


	public void expand(final boolean isexpand) {
		if (isexpand == false) {
			viewFlipper.setDisplayedChild(0);
			/**
			 * ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
			 * params.height = 1; faceLayout.setLayoutParams(params);
			 **/
			faceLayout.setVisibility(View.GONE);
			/**
			ViewGroup.LayoutParams params2 = photoButtonsLayout.getLayoutParams();
			params2.height = 1;
			photoButtonsLayout.setLayoutParams(params2);
			**/
			/**
			 * height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
			 * 再次打开ViewFlipper时画面在动的结果, 为了避免因为1dip的高度产生一个白缝，
			 * 所以这里在ViewFlipper所在的RelativeLayout中ViewFlipper 上层添加了一个1dip高的黑色色块
			 * viewFlipper必须在屏幕中有像素才能执行setDisplayedChild()操作
			 */
			// emotionBtn.setBackgroundResource(R.drawable.chat_bottom_look);
			isExpanded = false;
		} else {
			/**
			 * 让软键盘消失
			 */
			((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			faceLayout.setLayoutParams(params);
			faceLayout.setVisibility(View.VISIBLE);
			/**
			ViewGroup.LayoutParams params2 = photoButtonsLayout.getLayoutParams();
			params2.height = LayoutParams.WRAP_CONTENT;
			photoButtonsLayout.setLayoutParams(params2);
			**/
			// emotionBtn.setBackgroundResource(R.drawable.chat_bottom_keyboard);
			isExpanded = true;
		}
	}

	protected void showPhotoButtons(final boolean show) {
		ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
		if (show) {
			params.height = 1;
		} else {
			params.height = LayoutParams.WRAP_CONTENT;
		}
		faceLayout.setLayoutParams(params);
	}

	/**
	 * 用到的方法 viewFlipper.getDisplayedChild() 获得当前显示的ChildView的索引
	 * 
	 * @author Administrator
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
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.left_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.right_out));
							viewFlipper.showPrevious();
							setPhotoPointEffect(childIndex - 1);
						}
					} else if (event.getX() - startX < -60) {
						moveable = false;
						int childIndex = viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if (childIndex < listGrid.size() - 1) {
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.right_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.left_out));
							viewFlipper.showNext();
							setPhotoPointEffect(childIndex + 1);
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
	 * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent mIntent) {
		super.onActivityResult(requestCode, resultCode, mIntent);
		if (resultCode != mActivity.RESULT_OK) {
			mActivity.showToast("没有选中图片");
			return;
		}
		final ArrayList<String> files = new ArrayList<String>();
		switch (requestCode) {
		case Constant.INTENTRESULT_ADD_PHOTO:
			Bundle bundle = mIntent.getExtras();
			String[] images = bundle.getStringArray(ShowImageActivity.KEY_INTENT_RESULT);
			for (String path : images) {
				String[] paths = path.split(Constant.PATH_SEPERATOR);
				files.add(paths[0]);
			}
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					upload_(files);
					
				}
			});
			Log.d(TAG, "upload");
			break;
		case Constant.INTENTRESULT_TAKE_PHOTO:
			String[] projection = { MediaStore.MediaColumns._ID, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.Media.DATA };
			Cursor c = mActivity.getContentResolver().query(fileUri, projection, null, null, null);
			c.moveToFirst();
			files.add(c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					upload_(files);
					
				}
			});
			Log.d(TAG, "upload");
			break;
			/**
			 * case CAMERA_CROP_DATA: String path = mIntent.getStringExtra("PATH");
			 * if (D) { Log.d(TAG, "裁剪后得到的图片的路径是 = " + path); }
			 * mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, path);
			 * camIndex++; AbViewUtil.setAbsListViewHeight(mGridView, 3, 25); break;
			 **/
		}
	}

	/**
	 * GridViewAdapter
	 * 
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
			holder.image.setImageBitmap(ImageUtil.getSmallBitmap((String) list.get(position).get("image")));

			return convertView;
		}
	}

}
