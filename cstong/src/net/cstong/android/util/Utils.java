package net.cstong.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.cstong.android.R;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;

public class Utils {
	public static String timestampToString(final long tm) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(new Date());
		int nowYear = calendar.get(Calendar.YEAR);
		int nowMonth = calendar.get(Calendar.MONTH);
		int nowDay = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.setTime(new Date(tm * 1000));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		String dateString;
		Date date = calendar.getTime();
		if (nowYear == year) {
			if (nowMonth == month) {
				if (nowDay == day) {
					SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm");
					dateString = formatter1.format(date);
				} else {
					SimpleDateFormat formatter2 = new SimpleDateFormat("MM-dd");
					dateString = formatter2.format(date);
				}
			} else {
				SimpleDateFormat formatter3 = new SimpleDateFormat("MM-dd");
				dateString = formatter3.format(date);
			}
		} else {
			SimpleDateFormat formatter4 = new SimpleDateFormat("yyyy-MM-dd");
			dateString = formatter4.format(date);
		}
		return dateString;
	}

	public static void initTitleBarLeft(final AbActivity activity, final int logoId, final String title, final OnClickListener logoClickListener) {
		Log.d("Utils", "initTitleBarLeft:"+title);
		AbTitleBar mAbTitleBar = activity.getTitleBar();
		mAbTitleBar.setTitleText(title);
		ImageView logoView = mAbTitleBar.getLogoView();
		LinearLayout.LayoutParams params = (LayoutParams) logoView.getLayoutParams();
		params.setMargins(20, 10, 20, 10);
		logoView.setLayoutParams(params);
		
		mAbTitleBar.setLogo(logoId);
		mAbTitleBar.setTitleBarBackgroundColor(activity.getResources().getColor(R.color.green));
		mAbTitleBar.setTitleTextMargin(0, 10, 0, 10);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		mAbTitleBar.getLogoView().setOnClickListener(logoClickListener);
		
	}

	public static void initTitleBarRight() {

	}
	
	public static String chinaToUnicode(String str){  
	     String result="";  
	     for (int i = 0; i < str.length(); i++){  
	          int chr1 = (char) str.charAt(i);  
	          if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)  
	              result+="\\u" + Integer.toHexString(chr1);  
	          }else{  
	              result+=str.charAt(i);  
	          }  
	     }  
	     return result;  
	}
}
