package net.cstong.android.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

public class EmotionUtil {
	public static class EmotionConf {
		public EmotionConf(final String sign, final String filename, final String name) {
			this.sign = sign;
			this.filename = filename;
			this.name = name;
		}

		public String sign;
		public String filename;
		public String name;
	}

	public static String[] signs;
	public final static String regEmotionPattern = "\\[s:[0-9]{2}\\]";
	public final static HashMap<String, HashMap<String, EmotionConf>> emotionConfs = new HashMap<String, HashMap<String, EmotionConf>>();

	static {
		HashMap<String, EmotionConf> defaultEmotion = new HashMap<String, EmotionConf>() {
			{
				put("[s:117]", new EmotionConf("[s:117]", "emotion/default/705.gif", ""));
				put("[s:118]", new EmotionConf("[s:118]", "emotion/default/706.gif", ""));
				put("[s:116]", new EmotionConf("[s:116]", "emotion/default/704.gif", ""));
				put("[s:115]", new EmotionConf("[s:115]", "emotion/default/703.gif", ""));
				put("[s:60]", new EmotionConf("[s:60]", "emotion/default/701.gif", ""));
				put("[s:114]", new EmotionConf("[s:114]", "emotion/default/702.gif", ""));
				put("[s:79]", new EmotionConf("[s:79]", "emotion/default/206.gif", ""));
				put("[s:78]", new EmotionConf("[s:78]", "emotion/default/207.gif", ""));
				put("[s:68]", new EmotionConf("[s:68]", "emotion/default/57.gif", ""));
				put("[s:67]", new EmotionConf("[s:67]", "emotion/default/56.gif", ""));
				put("[s:66]", new EmotionConf("[s:66]", "emotion/default/55.gif", ""));
				put("[s:89]", new EmotionConf("[s:89]", "emotion/default/201.gif", ""));
				put("[s:61]", new EmotionConf("[s:61]", "emotion/default/50.gif", ""));
				put("[s:54]", new EmotionConf("[s:54]", "emotion/default/43.gif", ""));
				put("[s:53]", new EmotionConf("[s:53]", "emotion/default/42.gif", ""));
				put("[s:52]", new EmotionConf("[s:52]", "emotion/default/41.gif", ""));
				put("[s:51]", new EmotionConf("[s:51]", "emotion/default/40.gif", ""));
				put("[s:50]", new EmotionConf("[s:50]", "emotion/default/39.gif", ""));
				put("[s:49]", new EmotionConf("[s:49]", "emotion/default/38.gif", ""));
				put("[s:48]", new EmotionConf("[s:48]", "emotion/default/37.gif", ""));
				put("[s:65]", new EmotionConf("[s:65]", "emotion/default/54.gif", ""));
				put("[s:64]", new EmotionConf("[s:64]", "emotion/default/53.gif", ""));
				put("[s:63]", new EmotionConf("[s:63]", "emotion/default/52.gif", ""));
				put("[s:62]", new EmotionConf("[s:62]", "emotion/default/51.gif", ""));
				put("[s:59]", new EmotionConf("[s:59]", "emotion/default/48.gif", ""));
				put("[s:58]", new EmotionConf("[s:58]", "emotion/default/47.gif", ""));
				put("[s:57]", new EmotionConf("[s:57]", "emotion/default/46.gif", ""));
				put("[s:56]", new EmotionConf("[s:56]", "emotion/default/45.gif", ""));
				put("[s:55]", new EmotionConf("[s:55]", "emotion/default/44.gif", ""));
				put("[s:16]", new EmotionConf("[s:16]", "emotion/default/23.gif", ""));
				put("[s:27]", new EmotionConf("[s:27]", "emotion/default/13.gif", ""));
				put("[s:17]", new EmotionConf("[s:17]", "emotion/default/22.gif", ""));
				put("[s:34]", new EmotionConf("[s:34]", "emotion/default/3.gif", ""));
				put("[s:33]", new EmotionConf("[s:33]", "emotion/default/26.gif", ""));
				put("[s:32]", new EmotionConf("[s:32]", "emotion/default/25.gif", ""));
				put("[s:31]", new EmotionConf("[s:31]", "emotion/default/1.gif", ""));
				put("[s:30]", new EmotionConf("[s:30]", "emotion/default/10.gif", ""));
				put("[s:29]", new EmotionConf("[s:29]", "emotion/default/11.gif", ""));
				put("[s:28]", new EmotionConf("[s:28]", "emotion/default/12.gif", ""));
				put("[s:15]", new EmotionConf("[s:15]", "emotion/default/24.gif", ""));
				put("[s:18]", new EmotionConf("[s:18]", "emotion/default/2.gif", ""));
				put("[s:19]", new EmotionConf("[s:19]", "emotion/default/21.gif", ""));
				put("[s:25]", new EmotionConf("[s:25]", "emotion/default/15.gif", ""));
				put("[s:24]", new EmotionConf("[s:24]", "emotion/default/16.gif", ""));
				put("[s:23]", new EmotionConf("[s:23]", "emotion/default/17.gif", ""));
				put("[s:22]", new EmotionConf("[s:22]", "emotion/default/18.gif", ""));
				put("[s:21]", new EmotionConf("[s:21]", "emotion/default/19.gif", ""));
				put("[s:20]", new EmotionConf("[s:20]", "emotion/default/20.gif", ""));
				put("[s:26]", new EmotionConf("[s:26]", "emotion/default/14.gif", ""));
				put("[s:35]", new EmotionConf("[s:35]", "emotion/default/30.gif", ""));
				put("[s:47]", new EmotionConf("[s:47]", "emotion/default/36.gif", ""));
				put("[s:46]", new EmotionConf("[s:46]", "emotion/default/35.gif", ""));
				put("[s:45]", new EmotionConf("[s:45]", "emotion/default/34.gif", ""));
				put("[s:44]", new EmotionConf("[s:44]", "emotion/default/33.gif", ""));
				put("[s:43]", new EmotionConf("[s:43]", "emotion/default/32.gif", ""));
				put("[s:36]", new EmotionConf("[s:36]", "emotion/default/4.gif", ""));
				put("[s:37]", new EmotionConf("[s:37]", "emotion/default/5.gif", ""));
				put("[s:38]", new EmotionConf("[s:38]", "emotion/default/6.gif", ""));
				put("[s:39]", new EmotionConf("[s:39]", "emotion/default/7.gif", ""));
				put("[s:40]", new EmotionConf("[s:40]", "emotion/default/8.gif", ""));
				put("[s:41]", new EmotionConf("[s:41]", "emotion/default/9.gif", ""));
				put("[s:42]", new EmotionConf("[s:42]", "emotion/default/31.gif", ""));
				put("[s:90]", new EmotionConf("[s:90]", "emotion/default/402.gif", ""));
				put("[s:91]", new EmotionConf("[s:91]", "emotion/default/405.gif", ""));
				put("[s:92]", new EmotionConf("[s:92]", "emotion/default/407.gif", ""));
				put("[s:93]", new EmotionConf("[s:93]", "emotion/default/410.gif", ""));
				put("[s:77]", new EmotionConf("[s:77]", "emotion/default/67.gif", ""));
				put("[s:76]", new EmotionConf("[s:76]", "emotion/default/66.gif", ""));
				put("[s:75]", new EmotionConf("[s:75]", "emotion/default/63.gif", ""));
				put("[s:74]", new EmotionConf("[s:74]", "emotion/default/62.gif", ""));
				put("[s:73]", new EmotionConf("[s:73]", "emotion/default/65.gif", ""));
				put("[s:72]", new EmotionConf("[s:72]", "emotion/default/64.gif", ""));
				put("[s:71]", new EmotionConf("[s:71]", "emotion/default/61.gif", ""));
				// put("[s:70]", new EmotionConf("[s:70]", "emotion/default/60.gif", ""));
				put("[s:69]", new EmotionConf("[s:69]", "emotion/default/59.gif", ""));
				put("[s:83]", new EmotionConf("[s:83]", "emotion/default/303.gif", ""));
				put("[s:88]", new EmotionConf("[s:88]", "emotion/default/308.gif", ""));
				put("[s:87]", new EmotionConf("[s:87]", "emotion/default/307.gif", ""));
				put("[s:86]", new EmotionConf("[s:86]", "emotion/default/306.gif", ""));
				put("[s:80]", new EmotionConf("[s:80]", "emotion/default/300.gif", ""));
				put("[s:81]", new EmotionConf("[s:81]", "emotion/default/301.gif", ""));
				put("[s:82]", new EmotionConf("[s:82]", "emotion/default/302.gif", ""));
				put("[s:84]", new EmotionConf("[s:84]", "emotion/default/304.gif", ""));
				put("[s:85]", new EmotionConf("[s:85]", "emotion/default/305.gif", ""));
				put("[s:100]", new EmotionConf("[s:100]", "emotion/default/507.gif", ""));
				put("[s:101]", new EmotionConf("[s:101]", "emotion/default/508.gif", ""));
				put("[s:107]", new EmotionConf("[s:107]", "emotion/default/76.gif", ""));
				put("[s:104]", new EmotionConf("[s:104]", "emotion/default/73.gif", ""));
				put("[s:103]", new EmotionConf("[s:103]", "emotion/default/72.gif", ""));
				put("[s:102]", new EmotionConf("[s:102]", "emotion/default/71.gif", ""));
				put("[s:105]", new EmotionConf("[s:105]", "emotion/default/74.gif", ""));
				put("[s:106]", new EmotionConf("[s:106]", "emotion/default/75.gif", ""));
				put("[s:108]", new EmotionConf("[s:108]", "emotion/default/601.gif", ""));
				put("[s:113]", new EmotionConf("[s:113]", "emotion/default/606.gif", ""));
				put("[s:111]", new EmotionConf("[s:111]", "emotion/default/604.gif", ""));
				put("[s:112]", new EmotionConf("[s:112]", "emotion/default/605.gif", ""));
				put("[s:109]", new EmotionConf("[s:109]", "emotion/default/602.gif", ""));
				put("[s:110]", new EmotionConf("[s:110]", "emotion/default/603.gif", ""));
				put("[s:98]", new EmotionConf("[s:98]", "emotion/default/505.jpg", ""));
				put("[s:97]", new EmotionConf("[s:97]", "emotion/default/504.gif", ""));
			}
		};
		emotionConfs.put("default", defaultEmotion);

		signs = new String[emotionConfs.get("default").size()];
		int i = 0;
		for (String key : emotionConfs.get("default").keySet()) {
			signs[i++] = key;
		}
	}

	public static HashMap<String, EmotionConf> getDefaultEmotions() {
		return emotionConfs.get("default");
	}

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 */
	public static void parseContent(final Context context, final SpannableString spannableString, final Pattern patten, final int start) {
		Matcher matcher = patten.matcher(spannableString);
		try {
			while (matcher.find()) {
				String key = matcher.group();
				if (matcher.start() < start) {
					continue;
				}
				//Field field = R.drawable.class.getDeclaredField(key);
				//int resId = Integer.parseInt(field.get(null).toString()); //通过上面匹配得到的字符串来生成图片资源id
				Bitmap bitmap = getEmotionBitmap(context, key);
				if (bitmap == null) {
					continue;
				}
				//if (resId != 0) {
				//Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
				ImageSpan imageSpan = new ImageSpan(bitmap); //通过图片资源id来得到bitmap，用一个ImageSpan来包装
				int end = matcher.start() + key.length(); //计算该图片名字的长度，也就是要替换的字符串的长度
				spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE); //将该图片替换字符串中规定的位置中
				if (end < spannableString.length()) { //如果整个字符串还未验证完，则继续。。
					parseContent(context, spannableString, patten, end);
				}
				break;
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * @param context
	 * @param str
	 * @return
	 */
	public static SpannableString getExpressionString(final Context context, final String str, final String zhengze) {
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); //通过传入的正则表达式来生成一个pattern
		try {
			parseContent(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			Log.e("parseContent", e.getMessage());
		}
		return spannableString;
	}

	public static Bitmap getEmotionBitmap(final Context context, final String emotionSign) {
		AssetManager assets = context.getAssets();
		String filename = emotionConfs.get("default").get(emotionSign).filename;
		InputStream emotion = null;
		try {
			emotion = assets.open(filename);
			return BitmapFactory.decodeStream(emotion);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (emotion != null) {
				try {
					emotion.close();
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
}
