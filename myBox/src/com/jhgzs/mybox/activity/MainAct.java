package com.jhgzs.mybox.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jhgzs.mybox.R;
import com.jhgzs.utils.Base64;
import com.jhgzs.utils.BoxUtil;
import com.jhgzs.utils.FileUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAct extends Activity {

	private TextView tvFilename;
	
	private ImageView ivImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);
		initUI();
		
		try {
//			String pathString = Environment.getExternalStorageDirectory().getCanonicalPath()+"/ªÈ«Ï’πº‹.jpg";
//			String txt = GetImageStr(pathString);
//			FileUtil.newFile(Environment.getExternalStorageDirectory().getCanonicalPath()+"/ªÈ«Ï’πº‹.txt", txt);
//			
//			BoxUtil.str2Img(FileUtil.readTextFile(Environment.getExternalStorageDirectory().getCanonicalPath()+"/ªÈ«Ï’πº‹.txt")
//					, Environment.getExternalStorageDirectory().getCanonicalPath()+"/ªÈ«Ï’πº‹22.jpg");
			ivImg.setImageBitmap(BoxUtil.getBitmap(this,Environment.getExternalStorageDirectory().getCanonicalPath()+"/ªÈ«Ï’πº‹22.jpg"));
			ivImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent();
					i.setClass(MainAct.this, ImagePlayerAct.class);
					startActivity(i);
					
				}
			});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initUI(){
//		tvFilename = (TextView) findViewById(R.id.tv_filename);
//		tvFilename.setText("test");
//		ivImg = (ImageView) findViewById(R.id.iv_img);
	}
	
	
		

}
