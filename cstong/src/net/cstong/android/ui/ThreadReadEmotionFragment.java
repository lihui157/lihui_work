package net.cstong.android.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.cstong.android.R;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.api.PostApi.PostResult;
import net.cstong.android.util.Constant;
import net.cstong.android.util.EmotionUtil;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class ThreadReadEmotionFragment extends Fragment {
	private final String TAG = "ThreadReadEmotionFragment";
	private ThreadReadActivity mActivity = null;
	private ThreadInfo threadInfo = null;

	private boolean moveable = true;
	private float startX = 0;
	protected static int EMOTION_PER_PAGE = 20;
	protected String contentWithSign = null;

	private boolean isExpanded = false;
	ArrayList<ImageView> pointList = null;
	ArrayList<ArrayList<HashMap<String, Object>>> listGrid = null;
	protected ViewFlipper viewFlipper = null;
	protected RelativeLayout faceLayout = null;
	protected LinearLayout pagePoint = null;
	protected LinearLayout fillGapLinear = null;
	protected PostResult postResult = null;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};

	// 记录列表第一个帖子id,下拉时更新
	protected int firstTid = 0;

	public static ThreadReadEmotionFragment newInstance(final ThreadInfo info) {
		ThreadReadEmotionFragment newFragment = new ThreadReadEmotionFragment();
		if (info != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.KEY_THREADINFO, info);
			newFragment.setArguments(bundle);
		}
		return newFragment;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mActivity = (ThreadReadActivity) getActivity();

		View view = inflater.inflate(R.layout.tab_emotion, null);

		Bundle bundle = getArguments();
		threadInfo = (ThreadInfo) bundle.getSerializable(Constant.KEY_THREADINFO);

		listGrid = new ArrayList<ArrayList<HashMap<String, Object>>>();
		pointList = new ArrayList<ImageView>();
		addFaceData();
		viewFlipper = (ViewFlipper) view.findViewById(R.id.faceFlipper);
		faceLayout = (RelativeLayout) view.findViewById(R.id.faceLayout);
		pagePoint = (LinearLayout) view.findViewById(R.id.pagePoint);
		fillGapLinear = (LinearLayout) view.findViewById(R.id.fill_the_gap);
		addGridView();

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

	private void addGridView() {
		for (int i = 0; i < listGrid.size(); i++) {
			View view = LayoutInflater.from(mActivity).inflate(R.layout.view_item, null);
			GridView gv = (GridView) view.findViewById(R.id.myGridView);
			try {
				// gv.setNumColumns(5);
				gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
				MyGridAdapter adapter = new MyGridAdapter(mActivity, listGrid.get(i), R.layout.griditem_emotion,
						new String[] { "image" }, new int[] { R.id.emotionGridImage });
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

				pointList.add(image);
				/**
				 * 将image放入pointList，便于修改点的颜色
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void expand(final boolean isexpand) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
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
					// emotionBtn.setBackgroundResource(R.drawable.chat_bottom_look);
					isExpanded = false;
				} else {
					/**
					 * 让软键盘消失
					 */
					((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							mActivity.getCurrentFocus().getWindowToken()
							, InputMethodManager.HIDE_NOT_ALWAYS);
					ViewGroup.LayoutParams params = faceLayout.getLayoutParams();
					params.height = LayoutParams.WRAP_CONTENT;
					faceLayout.setLayoutParams(params);
					// emotionBtn.setBackgroundResource(R.drawable.chat_bottom_keyboard);
					isExpanded = true;
				}
			}
		});
		
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
							setPointEffect(childIndex - 1);
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
			holder.image.setImageBitmap(EmotionUtil.getEmotionBitmap(context, (String) list.get(position).get("faceName")));
			class MyGridImageClickListener implements OnClickListener {
				int position;

				public MyGridImageClickListener(final int position) {
					super();
					this.position = position;
				}

				@Override
				public void onClick(final View v) {
					mActivity.getContentEdit().append((String) list.get(position).get("faceName"));
				}
			}
			// 这里创建了一个方法内部类
			holder.image.setOnClickListener(new MyGridImageClickListener(position));

			return convertView;
		}
	}

}
