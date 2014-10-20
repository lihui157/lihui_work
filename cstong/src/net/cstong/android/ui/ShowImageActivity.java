package net.cstong.android.ui;

import java.util.ArrayList;
import java.util.List;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ForumInfo;
import net.cstong.android.ui.photo.ImageBean;
import net.cstong.android.ui.photo.PostListPhotoAdapter;
import net.cstong.android.util.Constant;
import net.cstong.android.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbBottomBar;

public class ShowImageActivity extends AbActivity {
	private static String TAG = "ShowImageActivity";
	private GridView mGridView;
	private PostListPhotoAdapter adapter;
	private ArrayList<String> imageList = new ArrayList<String>();
	private ArrayList<String> imageUrlList = new ArrayList<String>();
	private Button btnCancel;
	private Button btnComplete;
	protected ForumInfo forumInfo = null;

	public static final String KEY_INTENT_RESULT = "AddedPhotoList";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.MSG_PHOTO_SCAN_OK:
				//关闭进度条
				ShowImageActivity.this.removeProgressDialog();

				adapter = new PostListPhotoAdapter(ShowImageActivity.this, imageList, mGridView);
				mGridView.setAdapter(adapter);
				break;
			}
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_show_image);

		ImageBean.getImages(imageList, this, mHandler);

		mGridView = (GridView) findViewById(R.id.gridPhoto);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			forumInfo = (ForumInfo) bundle.getSerializable(Constant.KEY_FORUMINFO);
		}

		initTitleBar();
		initBottomBar();

		//显示进度条
		showProgressDialog("正在加载...");
	}

	public void initTitleBar() {
		Utils.initTitleBarLeft(this, R.drawable.button_selector_back, getResources().getString(R.string.photo_select), new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		View postView = mInflater.inflate(R.layout.btn_cancel, null);
		getTitleBar().addRightView(postView);
		btnCancel = (Button) postView.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				ShowImageActivity.this.finish();
			}
		});
	}

	public void initBottomBar() {
		AbBottomBar mAbBottomBar = getBottomBar();
		mAbBottomBar.setVisibility(View.VISIBLE);
		View view = mInflater.inflate(R.layout.thread_photo_bar, null);
		btnComplete = (Button) view.findViewById(R.id.btnComplete);
		btnComplete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {

				// 开始上传
				
				final List<String> selectedImages = adapter.getSelectItems();
				if (selectedImages.size() > Constant.PHOTO_UPLOAD_LIMIT) {
					showToast("一次只能上传" + Constant.PHOTO_UPLOAD_LIMIT + "张图片");
					return;
				}
				Intent intent = null;
				if (forumInfo == null) {
					intent = new Intent(ShowImageActivity.this, ThreadReadActivity.class);
				} else {
					intent = new Intent(ShowImageActivity.this, ThreadPostActivity.class);
				}
				String[] imageUrls = new String[selectedImages.size()];
				intent.putExtra(KEY_INTENT_RESULT, selectedImages.toArray(imageUrls));
				setResult(RESULT_OK, intent);
				ShowImageActivity.this.finish();
				
			}
		});
		mAbBottomBar.setBottomView(view);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public List<String> getUploadedImages() {
		return imageUrlList;
	}
}