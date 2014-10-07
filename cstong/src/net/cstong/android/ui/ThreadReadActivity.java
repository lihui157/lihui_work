package net.cstong.android.ui;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.util.Constant;
import net.cstong.android.util.Utils;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;

import com.ab.activity.AbActivity;
import com.ab.http.AbRequestParams;

public class ThreadReadActivity extends AbActivity {
	protected String TAG = "ThreadReadActivity";
	protected ThreadInfo threadInfo = null;
	public AbRequestParams replyParams = new AbRequestParams();
	public ThreadReadFragment readFragment = null;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置布局
		setAbContentView(R.layout.activity_thread_read);

		Bundle bundle = getIntent().getExtras();
		// method 1
		// threadInfo = (ThreadInfo)
		// bundle.getSerializable(Constant.KEY_THREADINFO);

		// method 2
		threadInfo = (ThreadInfo) bundle.getSerializable(Constant.KEY_THREADINFO);
		replyParams.put("tid", String.valueOf(threadInfo.tid));
		replyParams.put("fid", String.valueOf(threadInfo.fid));

		initTitleBar();

		readFragment = ThreadReadFragment.newInstance(threadInfo);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.thread_content, readFragment);
		transaction.commit();
	}

	public void initTitleBar() {
		String title = Constant.forumNames.get(threadInfo.fid);
		if (title == null) {
			title = "长沙通";
		}
		Utils.initTitleBarLeft(this, R.drawable.button_selector_back, title, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});
	}

	public EditText getContentEdit() {
		return readFragment.postContent;
	}

//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		// TODO Auto-generated method stub
//		super.onConfigurationChanged(newConfig);
//	}
	
	
}
