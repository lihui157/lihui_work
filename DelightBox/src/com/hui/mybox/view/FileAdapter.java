package com.hui.mybox.view;

import java.io.File;
import java.util.List;



import com.hui.mybox.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {
	
	private List<File> data;
	
	private LayoutInflater listContainer;
	
	public FileAdapter(Context context,List<File> dataList){
		this.data = dataList;
		listContainer = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(data==null) return 0;
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		try {
			//�Զ�����ͼ   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //��ȡlist_item�����ļ�����ͼ   
	            convertView = listContainer.inflate(R.layout.img_list_item, null);   
	            //��ȡ�ؼ�����   
	            listItemView.name = (TextView)convertView.findViewById(R.id.tv_img_item_name);   
	            listItemView.path = (TextView)convertView.findViewById(R.id.tv_img_item_path);   
	            listItemView.size = (TextView)convertView.findViewById(R.id.tv_img_item_size);   
	            listItemView.time = (TextView)convertView.findViewById(R.id.tv_img_item_time);   
	            
	            //���ÿؼ�����convertView   
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        } 
	        
	        listItemView.name.setText(data.get(arg0).getName());
	        listItemView.path.setText(data.get(arg0).getCanonicalPath());
	        listItemView.size.setText((data.get(arg0).isFile())? String.valueOf(data.get(arg0).length()):"");
	        listItemView.time.setText(data.get(arg0).getName());
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
        return convertView;   
    
	}
	
	class ListItemView{
		TextView time;
		TextView size;
		TextView path;
		TextView name;
	}

}
