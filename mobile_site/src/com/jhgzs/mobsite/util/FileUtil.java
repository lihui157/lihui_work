package com.jhgzs.mobsite.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";  
//	 /**
//	  * ��ȡͼƬ����
//	  * @param imageUri
//	  * @return
//	  */
//    public static File getCacheFile(String imageUri){  
//        File cacheFile = null;  
//        try {  
//            if (Environment.getExternalStorageState().equals(  
//                    Environment.MEDIA_MOUNTED)) {  
//                File sdCardDir = Environment.getExternalStorageDirectory();  
//                String fileName = getFileName(imageUri);  
//                File dir = new File(sdCardDir.getCanonicalPath()  
//                        + AsynImageLoader.CACHE_DIR);  
//                if (!dir.exists()) {  
//                    dir.mkdirs();  
//                }  
//                cacheFile = new File(dir, fileName);  
////                LogUtil.info(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);  
//            }    
//        } catch (IOException e) {  
//            e.printStackTrace();  
//            LogUtil.debug(TAG, "getCacheFileError:" + e.getMessage());  
//        }  
//          
//        return cacheFile;  
//    }  
      
    public static String getFileName(String path) {  
        int index = path.lastIndexOf("/");  
        return path.substring(index + 1);  
    }  
    
    /** 
     *  ����·��ɾ��ָ����Ŀ¼���ļ������۴������ 
     *@param sPath  Ҫɾ����Ŀ¼���ļ� 
     *@return ɾ���ɹ����� true�����򷵻� false�� 
     */  
    public static boolean DeleteFolder(String sPath) {  
        boolean flag = false;  
        File file = new File(sPath);  
        // �ж�Ŀ¼���ļ��Ƿ����  
        if (!file.exists()) {  // �����ڷ��� false  
            return flag;  
        } else {  
            // �ж��Ƿ�Ϊ�ļ�  
            if (file.isFile()) {  // Ϊ�ļ�ʱ����ɾ���ļ�����  
                return deleteFile(sPath);  
            } else {  // ΪĿ¼ʱ����ɾ��Ŀ¼����  
                return deleteDirectory(sPath);  
            }  
        }  
    } 
    
    /** 
     * ɾ�������ļ� 
     * @param   sPath    ��ɾ���ļ����ļ��� 
     * @return �����ļ�ɾ���ɹ�����true�����򷵻�false 
     */  
    public static boolean deleteFile(String sPath) {  
    	boolean flag = false;  
        File file = new File(sPath);  
        // ·��Ϊ�ļ��Ҳ�Ϊ�������ɾ��  
        if (file.isFile() && file.exists()) {  
            file.delete();  
            flag = true;  
        }  
        return flag;  
    } 
    
    /** 
     * ɾ��Ŀ¼���ļ��У��Լ�Ŀ¼�µ��ļ� 
     * @param   sPath ��ɾ��Ŀ¼���ļ�·�� 
     * @return  Ŀ¼ɾ���ɹ�����true�����򷵻�false 
     */  
    public static boolean deleteDirectory(String sPath) {  
        //���sPath�����ļ��ָ�����β���Զ�����ļ��ָ���  
        if (!sPath.endsWith(File.separator)) {  
            sPath = sPath + File.separator;  
        }  
        File dirFile = new File(sPath);  
        //���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�  
        if (!dirFile.exists() || !dirFile.isDirectory()) {  
            return false;  
        }  
       boolean  flag = true;  
        //ɾ���ļ����µ������ļ�(������Ŀ¼)  
        File[] files = dirFile.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            //ɾ�����ļ�  
            if (files[i].isFile()) {  
                flag = deleteFile(files[i].getAbsolutePath());  
                if (!flag) break;  
            } //ɾ����Ŀ¼  
            else {  
                flag = deleteDirectory(files[i].getAbsolutePath());  
                if (!flag) break;  
            }  
        }  
        if (!flag) return false;  
        //ɾ����ǰĿ¼  
        if (dirFile.delete()) {  
            return true;  
        } else {  
            return false;  
        }  
    } 
    
    /**
     * �ж�Ŀ¼�Ƿ��ظ�
     * @return
     */
    public static boolean isRepeat(String[] pathArr,String path){
    	boolean tag = false;
    	if(pathArr==null||pathArr.length==0) return false;
    	for(int i = 0;i<pathArr.length;i++){
    		if(pathArr[i].equals(path)){
    			tag = true;
    		}
    	}
    	return tag;
    }
    
    /**
     * ��ȡ�ļ���׺��
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return "";   
    }
    
    /**
     * д��txt�ĵ�
     * @param path
     * @param str
     */
    public static void writeText(String path,String str){
    	BufferedWriter bw = null;
		try {
			if(str!=null&&!str.equals("")){
				File file = new File(path);
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				if(!file.exists()){
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file, false);
				bw = new BufferedWriter(fw);
				bw.write(str);
			}else{
				Log.e(TAG,"error : str=" + str);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bw!=null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * �����ļ�����
     * @param path
     * @return
     */
    public static String readText(String path){
    	File file = new File(path);
    	StringBuffer sb = new StringBuffer();
		  try {
		   BufferedReader bw = new BufferedReader(new FileReader(file));
		   String line = null;
		   //��Ϊ��֪���м������ݣ������ȴ���list������
		   while((line = bw.readLine()) != null){
			   sb.append(line);
		   }
		   bw.close();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }catch (Error e) {
			e.printStackTrace();
			System.gc();
		}
		  return sb.toString();
    }
    
    /** 
     * ���Ƶ����ļ� 
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt 
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt 
     * @return boolean 
     */ 
   public static boolean copyFile(String oldPath, String newPath) { 
	   boolean b= false;
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) { //�ļ�����ʱ 
               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ� 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //�ֽ��� �ļ���С 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
               b = true;
           } 
       } 
       catch (Exception e) { 
           System.out.println("���Ƶ����ļ���������"); 
           e.printStackTrace(); 

       } 
       return b;

   } 
   
// �����ļ���
   public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
       // �½�Ŀ��Ŀ¼
       (new File(targetDir)).mkdirs();
       // ��ȡԴ�ļ��е�ǰ�µ��ļ���Ŀ¼
       File[] file = (new File(sourceDir)).listFiles();
       for (int i = 0; i < file.length; i++) {
           if (file[i].isFile()) {
               // Դ�ļ�
               File sourceFile = file[i];
               // Ŀ���ļ�
               File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
               copyFile(sourceFile.getCanonicalPath(), targetFile.getCanonicalPath());
           }
           if (file[i].isDirectory()) {
               // ׼�����Ƶ�Դ�ļ���
               String dir1 = sourceDir + "/" + file[i].getName();
               // ׼�����Ƶ�Ŀ���ļ���
               String dir2 = targetDir + "/" + file[i].getName();
               copyDirectiory(dir1, dir2);
           }
       }
   }
    
}
