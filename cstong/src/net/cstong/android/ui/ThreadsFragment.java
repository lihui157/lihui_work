package net.cstong.android.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.cstong.android.R;
import net.cstong.android.api.FocusApi;
import net.cstong.android.api.ForumApi;
import net.cstong.android.api.ForumApi.ForumInfo;
import net.cstong.android.api.ForumApi.ThreadInfo;
import net.cstong.android.ui.adapter.ThreadItemAdapter;
import net.cstong.android.util.Constant;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.dct.IFDCT;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;

public class ThreadsFragment extends Fragment {
	private final String TAG = "ThreadsFragment";
	private MainActivity mActivity = null;
	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;
	private ThreadItemAdapter myListViewAdapter = null;
	private ForumInfo forumInfo = null;
	private Button btRefresh;
	private TextView tvMsg;

	private AbStringHttpResponseListener threadsListener = null;
	private List<ThreadInfo> threads = new ArrayList<ThreadInfo>();
	private List<ThreadInfo> threadsNew = new ArrayList<ThreadInfo>();
	private int pageIndex = 1;
	private int pageSize = 10;
	// 记录列表第一个帖子id,下拉时更新
	protected int firstTid = 0;

	public static ThreadsFragment newInstance(final ForumInfo info) {
		ThreadsFragment newFragment = new ThreadsFragment();
		if (info != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.KEY_FORUMINFO, info);
			newFragment.setArguments(bundle);
		}
		return newFragment;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mActivity = (MainActivity) getActivity();

		View view = inflater.inflate(R.layout.fragment_thread_list, null);

		//获取ListView对象
		mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);
		mListView = (ListView) view.findViewById(R.id.mListView);

		Bundle bundle = getArguments();
		forumInfo = (ForumInfo) bundle.getSerializable(Constant.KEY_FORUMINFO);

		//设置进度条的样式
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(getResources().getDrawable(R.drawable.progress_circular));
		mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(getResources().getDrawable(R.drawable.progress_circular));

		//使用自定义的Adapter
		if(myListViewAdapter==null){
			myListViewAdapter = new ThreadItemAdapter(mActivity, threads, R.layout.fragment_threadinfo,
					new int[] { R.id.itemsAvatar, R.id.itemsTitle, R.id.itemsUsername, R.id.itemsCreatedTime, R.id.itemsViews });
		}
		
		mListView.setAdapter(myListViewAdapter);

		//item被点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				Log.d(TAG, "id:" + id + " position:" + position + " view:" + view.toString());
				Intent intent = new Intent(getActivity(), ThreadReadActivity.class);
				ThreadInfo threadInfo = (ThreadInfo) myListViewAdapter.getItem(position);
				// method 1
				//intent.putExtra(Constant.KEY_THREADINFO, threadInfo);

				// method 2
				Bundle bundle = new Bundle();
				bundle.putSerializable(Constant.KEY_THREADINFO, threadInfo);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		//设置监听器
		mAbPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(final AbPullToRefreshView view) {
				getThreadList();
				mAbPullToRefreshView.onHeaderRefreshFinish();
			}
		});
		mAbPullToRefreshView.setOnFooterLoadListener(new OnFooterLoadListener() {
			@Override
			public void onFooterLoad(final AbPullToRefreshView view) {
				getThreadList();
				mAbPullToRefreshView.onFooterLoadFinish();
			}
		});

		threadsListener = new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(final int statusCode, final String content) {
//				Log.d(TAG, "onSuccess:" + content);
				if (forumInfo.fid == 0) {
					threadsNew = FocusApi.parseFocusResponse(content);
				} else {
					threadsNew = ForumApi.parseThreadListResponse(content);
					try {
						JSONObject object = new JSONObject(content);
						if(object!=null&&!object.isNull("result")&&object.getInt("result")!=1){
							threadsNew = ForumApi.parseThreadListResponse(content);
							tvMsg.setVisibility(View.GONE);
						}else{
							tvMsg.setVisibility(View.VISIBLE);
							tvMsg.setText(object.getString("message"));
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				pageIndex++;
			}

			// 开始执行前
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
				//显示进度框 
//				if (forumInfo.fid != 0){
					mActivity.showProgressDialog();
					btRefresh.setVisibility(View.GONE);
					tvMsg.setVisibility(View.GONE);
//				}
				
			}

			// 失败，调用
			@Override
			public void onFailure(final int statusCode, final String content, final Throwable error) {
//				if (forumInfo.fid != 0){
//					getThreadList();
//				}
				mActivity.showToast("加载失败，请稍后再试");
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				
				if (threadsNew.size() > 0) {
					threads.addAll(threadsNew);
					myListViewAdapter.notifyDataSetChanged();
					threadsNew.clear();
					firstTid = threads.get(0).tid;
				}

				mAbPullToRefreshView.onFooterLoadFinish();
				//移除进度框
				mActivity.removeProgressDialog();
				
				setRefreshButtonVisibility();
				
				
			}
		};
		
		btRefresh = (Button) view.findViewById(R.id.bt_refresh);
		btRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getThreadList();
				
			}
		});
		tvMsg = (TextView) view.findViewById(R.id.tv_msg);
		
		setRefreshButtonVisibility();
		
		return view;
	}
	
	private void setRefreshButtonVisibility(){
		if(threads==null||threads.size()==0){
			btRefresh.setVisibility(View.VISIBLE);
		}else{
			btRefresh.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (pageIndex == 1) {
			getThreadList();
		}
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void getThreadList() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			AbRequestParams params = new AbRequestParams();
			params.put("page", String.valueOf(pageIndex));
			if (pageSize == 1) {
				params.put("pageSize", String.valueOf(10));
			} else {
				params.put("pageSize", String.valueOf(pageSize));
			}
			if (forumInfo.fid != 0) {
				params.put("fid", String.valueOf(forumInfo.fid));
				ForumApi.threadList(getActivity(), params, threadsListener);
			} else {
				FocusApi.index(getActivity(), params, threadsListener);
			}
		}
	}

	public void reset() {
		threads.clear();
		pageIndex = 1;
	}

	
}
