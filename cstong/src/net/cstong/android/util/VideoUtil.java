package net.cstong.android.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.util.Base64;
import android.util.Log;

public class VideoUtil {
	private static final String TAG = "VideoUtil";

	public static String videoToString(final String filePath) {
		try {
			File tmpFile = new File(filePath);
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(tmpFile));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[in.available()];
			int length;
			while ((length = in.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			byte[] b = baos.toByteArray();
			return Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			Log.e(TAG, "读取视频出现异常", e);
		}
		return null;
	}
}
