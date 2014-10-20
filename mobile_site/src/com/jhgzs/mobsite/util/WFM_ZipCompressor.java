package com.jhgzs.mobsite.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class WFM_ZipCompressor {
	static final int BUFFER = 8192;

	private File zipFile;

	public WFM_ZipCompressor(String pathName) {
		zipFile = new File(pathName);
	}

	/**
	 * ѹ���ļ�
	 * 
	 * @param srcPathName
	 */
	public boolean compress(String srcPathName) {
		boolean b = false;
		File file = new File(srcPathName);
		if (!file.exists()){
			throw new RuntimeException(srcPathName + "�����ڣ�");
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32()); // ����CRC32��һ�����������ļ��������������У�飬�����ָ��
			ZipOutputStream out = new ZipOutputStream(cos);
			out.setEncoding("utf-8"); // ������Ӵ˾䣬ѹ���ļ���Ȼ�������ɣ�ֻ���ڴ򿪺ͽ�ѹ��ʱ�򣬻���ʾ���룬���ǻ��ǻ��ѹ����
			String basedir = "";
			compress(file, out, basedir);
			out.close();
			b = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return b;
	}

	private void compress(File file, ZipOutputStream out, String basedir) {
		/* �ж���Ŀ¼�����ļ� */
		if (file.isDirectory()) {
			this.compressDirectory(file, out, basedir);
		} else {
			this.compressFile(file, out, basedir);
		}
	}

	/** ѹ��һ��Ŀ¼ */
	private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists())
			return;

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* �ݹ� */
			compress(files[i], out, basedir + dir.getName() + "/");
		}
	}

	/** ѹ��һ���ļ� */
	private void compressFile(File file, ZipOutputStream out, String basedir) {
		if (!file.exists()) {
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			ZipEntry entry = new ZipEntry(basedir + file.getName());
			out.putNextEntry(entry);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		WFM_ZipCompressor zc = new WFM_ZipCompressor("c:/telnetenable_8039.zip");
		zc.compress("c:/telnetenable_8039"); // ѹ��һ���ļ���

	}
}