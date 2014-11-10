package com.jhgzs.mybox.view;


import com.jhgzs.mybox.view.TouchView.OnTouchCallBack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView.ScaleType;


public class ViewScroll extends AbsoluteLayout
{
	private int screenW;	//���õ���Ļ��
	private int screenH;	//���õ���Ļ��   �ܸ߶�-����������ܸ߶�?
	private int imgW;		//ͼƬԭʼ��
	private int imgH;		//ͼƬԭʼ��
	private TouchView tv;

	public ViewScroll(Context context,Bitmap img,View topView)
	{
		super(context);
		screenW = ((Activity)
				context).getWindowManager().getDefaultDisplay().getWidth();
		//screenH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight()-(topView==null?190:topView.getBottom()+50);
		screenH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
		tv = new TouchView(context,screenW,screenH);
        //tv.setImageResource(resId);
        tv.setImageBitmap(img);
        //Bitmap img = BitmapFactory.decodeResource(context.getResources(), resId);
        imgW = img.getWidth();
        imgH = img.getHeight();
        int layout_w = imgW>screenW?screenW:imgW; //ʵ����ʾ�Ŀ�
        int layout_h = imgH>screenH?screenH:imgH; //ʵ����ʾ�ĸ�
        //if(layout_w==screenW||layout_h==screenH)
        //	tv.setScaleType(ScaleType.FIT_XY);
        //tv.setScaleType(ScaleType.CENTER_INSIDE);
        tv.setLayoutParams(new AbsoluteLayout.LayoutParams(layout_w,layout_h , layout_w==screenW?0:(screenW-layout_w)/2, layout_h==screenH?0:(screenH-layout_h)/2));
        this.addView(tv);
	}
	
	public void onZoomIn()
	{
		if(null != tv)
		{
			tv.onBigger();
		}
	}
	
	public void onZoomOut()
	{
		if(null != tv)
		{
			tv.onSmaller();
		}
	}
	
	public void SetOnTouchCallBack(OnTouchCallBack callback)
    {
		if(null != tv)
		{
			tv.SetOnTouchCallBack(callback);
		}
    }

	
}
