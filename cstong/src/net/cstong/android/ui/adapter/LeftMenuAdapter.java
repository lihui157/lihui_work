package net.cstong.android.ui.adapter;

import java.util.ArrayList;

import net.cstong.android.R;
import net.cstong.android.ui.widget.CircularImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ab.model.AbMenuItem;

public class LeftMenuAdapter extends BaseExpandableListAdapter {

	private Context mContext = null;
	private ArrayList<String> mGroupName;
	private ArrayList<ArrayList<AbMenuItem>> mChilds;
	private LayoutInflater mInflater = null;

	public LeftMenuAdapter(final Context context, final ArrayList<String> groupName, final ArrayList<ArrayList<AbMenuItem>> childs) {
		mContext = context;
		mGroupName = groupName;
		mChilds = childs;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		return mChilds.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_menu_list_child, null);
			holder = new ViewHolder();
			holder.mChildIcon = (CircularImageView) convertView.findViewById(R.id.desktop_list_child_icon);
			holder.mChildName = (TextView) convertView.findViewById(R.id.desktop_list_child_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AbMenuItem m = mChilds.get(groupPosition).get(childPosition);
		holder.mChildIcon.setImageResource(m.getIconId());
		holder.mChildName.setText(m.getText());
		return convertView;
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		return mChilds.get(groupPosition).size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return mGroupName.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroupName.size();
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_menu_list_group, null);
			convertView.setVisibility(View.GONE);
			holder = new ViewHolder();
			holder.mGroupName = (TextView) convertView.findViewById(R.id.desktop_list_group_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String name = mGroupName.get(groupPosition);
		holder.mGroupName.setText(name);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return true;
	}

	private class ViewHolder {
		private TextView mGroupName;
		private CircularImageView mChildIcon;
		private TextView mChildName;
	}
}
