package com.hui.mybox.view;

import java.io.File;
import java.util.List;



import com.hui.mybox.R;
import com.hui.mybox.model.MediaFileInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MediaFileAdapter extends BaseAdapter {
	
	private List<MediaFileInfo> data;
	
	private LayoutInflater listContainer;
	
	public MediaFileAdapter(Context context,List<MediaFileInfo> dataList){
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
			//自定义视图   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //获取list_item布局文件的视图   
	            convertView = listContainer.inflate(R.layout.img_list_item, null);   
	            //获取控件对象   
	            listItemView.name = (TextView)convertView.findViewById(R.id.tv_img_item_name);   
	            listItemView.path = (TextView)convertView.findViewById(R.id.tv_img_item_path);   
	            listItemView.size = (TextView)convertView.findViewById(R.id.tv_img_item_size);   
	            listItemView.time = (TextView)convertView.findViewById(R.id.tv_img_item_time);   
	            
	            //设置控件集到convertView   
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        } 
	        
	        listItemView.name.setText(data.get(arg0).getFileName());
	        listItemView.path.setText(data.get(arg0).getPath());
	        listItemView.size.setText((data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_FOLDER)? String.valueOf(data.get(arg0).getLength()):"");
	        listItemView.time.setText(data.get(arg0).getFileName());
	        
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
