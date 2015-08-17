package com.jun.modif;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;



public class FileUtil {
	private static int FILE_SIZE = 4 * 1024;
	private static String TAG = "FileUtil";
    
    /**  
     * 根据URL得到输入�?  
     * @param urlStr  
     * @return  
     */  
    public static InputStream getInputStreamFromURL(String urlStr) {  
        HttpURLConnection urlConn = null;  
        InputStream inputStream = null;  
        try {  
        	URL  url = new URL(urlStr);  
            urlConn = (HttpURLConnection)url.openConnection();  
            inputStream = urlConn.getInputStream();  
              
        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
        return inputStream;  
    } 
	
	/**  
     *   
     * @param urlStr  
     * @param folder  
     * @param fileName  
     * @return   
     *      -1:文件下载出错  
     *       0:文件下载成功  
     *       1:文件已经存在  
     */  
    public static int downFile(String urlStr, String folder, String fileName){  
        InputStream inputStream = null;  
        try {   
            if(FileUtil.exists(folder + fileName)){  
                return 1;  
            } else {  
                inputStream = getInputStreamFromURL(urlStr);  
                File resultFile = FileUtil.write2SDFromInput(folder, fileName, inputStream);  
                if(resultFile == null){  
                    return -1;  
                }  
            }  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
            return -1;  
        }  
        finally{  
            try {
            	if(inputStream!=null)
                inputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return 0;  
    }
	
	/**  
     * 将一个InputStream里面的数据写入到SD卡中  
     * @param path  
     * @param fileName  
     * @param input  
     * @return  
     */  
    public static File write2SDFromInput(String path,String fileName,InputStream input){  
        File file = null;  
        OutputStream output = null;  
        try {  
        	createPath(path);  
            file = new File(path + fileName);  
            if(!file.exists()){
            	file.createNewFile();
            }
            output = new FileOutputStream(file);  
                            byte[] buffer = new byte[FILE_SIZE];  
  
            /*真机测试，这段可能有问题，请采用下面网友提供�?  
                            while((input.read(buffer)) != -1){  
                output.write(buffer);  
            }  
                            */  
  
                           /* 网友提供 begin */  
                           int length;  
                           while((length=(input.read(buffer))) >0){  
                                 output.write(buffer,0,length);  
                           }  
                           /* 网友提供 end */  
  
            output.flush();  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        finally{  
            try {  
                output.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return file;  
    }  

	public static boolean createPath(final String path) {
		File f = new File(path);
		if (!f.exists()) {
			Boolean o = f.mkdirs();
			return o;
		}
		return true;
	}

	public static boolean exists(final String file) {
		return new File(file).exists();
	}

	public static File saveFile(final String file, final InputStream inputStream) {
		File f = null;
		OutputStream outSm = null;

		try {
			f = new File(file);
			String path = f.getParent();
			if (!createPath(path)) {
				return null;
			}

			if (!f.exists()) {
				f.createNewFile();
			}

			outSm = new FileOutputStream(f);
			byte[] buffer = new byte[FILE_SIZE];
			while ((inputStream.read(buffer)) != -1) {
				outSm.write(buffer);
			}
			outSm.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;

		} finally {
			try {
				if (outSm != null) {
					outSm.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return f;
	}
	
	
	
	private static byte[] getBytes(InputStream is) throws IOException {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[512];
			int len = 0;

			while ((len = is.read(b, 0, 512)) != -1) {
				// LogUtil.debug(TAG, "getBytes",
				// ""+len+"  baos lenth:"+baos.size());
				baos.write(b, 0, len);
				baos.flush();
			}
			byte[] bytes = baos.toByteArray();

			baos.reset();

			baos.close();

			baos = null;
			b = null;

			return bytes;
		} catch(OutOfMemoryError error){
			error.printStackTrace();
			System.gc();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	 
}