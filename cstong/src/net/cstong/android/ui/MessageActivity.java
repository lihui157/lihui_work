package net.cstong.android.ui;

import net.cstong.android.R;
import net.cstong.android.util.Utils;
import android.os.Bundle;
import android.view.View;

import com.ab.activity.AbActivity;

/**
 * 消息列表界面
 *
 */
public class MessageActivity extends AbActivity {
	private static final String TAG = "MessageActivity";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_message);

		initTitleBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initTitleBar() {
		Utils.initTitleBarLeft(this, R.drawable.button_selector_back, getResources().getString(R.string.mymsg), new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		getSupportFragmentManager().beginTransaction().replace(R.id.activity_message, new MessageTabsFragment()).commit();
	}
}
