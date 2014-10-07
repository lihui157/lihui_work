package net.cstong.android.ui;

import java.util.ArrayList;

import net.cstong.android.R;
import net.cstong.android.api.MessageApi;
import net.cstong.android.api.MessageApi.NoticeInfo;
import net.cstong.android.api.MessageApi.NoticeListInfo;
import net.cstong.android.ui.adapter.NoticeListAdapter;
import net.cstong.android.util.Constant;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;

public class NoticeListFragment extends Fragment implements OnHeaderRefreshListener, OnFooterLoadListener {
	private static final String TAG = "NoticeListFragment";
	public static int MSGTYPE_NOTICE = 0;
	public static int MSGTYPE_CHAT = 1;
	private MessageActivity mActivity = null;
	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;
	private NoticeListAdapter myListViewAdapter = null;

	private AbStringHttpResponseListener noticeListener = null;
	private NoticeListInfo noticeListInfo = null;
	private NoticeListInfo noticeListInfoNew = null;
	private int pageIndex = 1;
	private int pageSize = 20;
	// 记录列表第一个帖子id,下拉时更新
	protected int firstTid = 0;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		mActivity = (MessageActivity) getActivity();

		View view = inflater.inflate(R.layout.fragment_notices, null);

		//获取ListView对象
		mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.pulllistNoticeList);
		mListView = (ListView) view.findViewById(R.id.noticeList);

		//设置进度条的样式
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(getResources().getDrawable(R.drawable.progress_circular));
		mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(getResources().getDrawable(R.drawable.progress_circular));

		noticeListInfo = new NoticeListInfo();
		noticeListInfo.noticeData = new ArrayList<NoticeInfo>();
		noticeListInfoNew = new NoticeListInfo();
		noticeListInfoNew.noticeData = new ArrayList<NoticeInfo>();
		//使用自定义的Adapter
		myListViewAdapter = new NoticeListAdapter(mActivity, noticeListInfo, R.layout.fragment_notice);
		mListView.setAdapter(myListViewAdapter);

		//item被点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				Log.d(TAG, "id:" + id + " position:" + position + " view:" + view.toString());
				Intent intent = new Intent(getActivity(), ThreadReadActivity.class);
				Bundle bundle = getArguments();
				intent.putExtra(Constant.KEY_THREADINFO, bundle);
				startActivity(intent);
			}
		});

		//设置监听器
		mAbPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			@Override
			public void onHeaderRefresh(final AbPullToRefreshView view) {
				getMessageList();
				mAbPullToRefreshView.onHeaderRefreshFinish();
			}
		});
		mAbPullToRefreshView.setOnFooterLoadListener(new OnFooterLoadListener() {
			@Override
			public void onFooterLoad(final AbPullToRefreshView view) {
				getMessageList();
				mAbPullToRefreshView.onFooterLoadFinish();
			}
		});

		noticeListener = new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(final int statusCode, final String content) {
//				Log.d(TAG, "onSuccess:" + content);
				noticeListInfoNew = MessageApi.parseNoticeListResponse(content);
			}

			// 开始执行前
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
				//显示进度框
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
				//移除进度框
				mActivity.removeProgressDialog();
				if ((noticeListInfoNew != null) && (noticeListInfoNew.noticeData.size() > 0)) {
					noticeListInfo.noticeData.addAll(noticeListInfoNew.noticeData);
					noticeListInfo.unreadCount += noticeListInfoNew.unreadCount;
					myListViewAdapter.notifyDataSetChanged();
					noticeListInfoNew.noticeData.clear();
					//firstTid = noticeListInfo.noticeData.get(0).tid;
				}

				mAbPullToRefreshView.onFooterLoadFinish();
			}
		};
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
		queryData(0);

		//第一次下载数据
		noticeListInfo.noticeData.clear();
		getMessageList();
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onHeaderRefresh(final AbPullToRefreshView view) {
		//pageNum = 1;
		//list.clear();
		noticeListInfo.noticeData.clear();
		queryData(0);
	}

	@Override
	public void onFooterLoad(final AbPullToRefreshView view) {
		//pageNum++;
		queryData(1);
	}

	public void getMessageList() {
		if (Constant.myApp.mUser.cookie.length() > 0) {
			AbRequestParams params = new AbRequestParams();
			params.put(Constant.KEY_USER_COOKIE, Constant.myApp.mUser.cookie);
			params.put("page", String.valueOf(pageIndex));
			params.put("pageSize", String.valueOf(pageSize));

			MessageApi.noticeList(getActivity(), params, noticeListener);
		}
	}

	private void queryData(final int query) {
		//查询数据
		/**
		AbStorageQuery mAbStorageQuery = new AbStorageQuery();
		mAbStorageQuery.equals("type", IMMessage.SYS_MSG);

		AbStorageQuery mAbStorageQuery2 = new AbStorageQuery();
		mAbStorageQuery2.equals("type", IMMessage.ADD_FRIEND_MSG);
		mAbStorageQuery.or(mAbStorageQuery2);
		mAbStorageQuery.setLimit(pageSize);
		mAbStorageQuery.setOffset((pageNum - 1) * pageSize);
		**/
		//无sql存储的查询
		/**
		mAbSqliteStorage.findData(mAbStorageQuery, mIMMsgDao, new AbDataInfoListener() {

			@Override
			public void onFailure(final int errorCode, final String errorMessage) {
				showToast(errorMessage);
			}

			@Override
			public void onSuccess(final List<?> paramList) {
				if (query == 0) {
					if ((paramList != null) && (paramList.size() > 0)) {
						list.addAll((List<IMMessage>) paramList);
						myListViewAdapter.notifyDataSetChanged();
					}
					mAbPullToRefreshView.onHeaderRefreshFinish();
				} else {
					if (paramList != null) {
						list.addAll((List<IMMessage>) paramList);
						myListViewAdapter.notifyDataSetChanged();
					}
					mAbPullToRefreshView.onFooterLoadFinish();
				}

			}

		});
		**/
	}
}
