package com.rongyan.hpmessage.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.rongyan.hpmessage.bootconfig.BootConfigTask;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class HttpDownAPKUtils implements Runnable{
	
	private final static String TAG="HttpDownAPKUtils";
    // 下载文件 存放目的地
    private String downloadPath = Environment.getExternalStorageDirectory()
            .getPath() + "/messageservice/download_apk/";

    private String address;

    private Context context;

    private long readSize = 0L;//已下载的总大小
    
    private long contentLength = 0;//服务器返回的数据长度
	
	private int type;
	
	private String apkNameString="hpmessage.apk";
	
	private boolean isFirst=true;
	
	public static boolean isDown=false;
	
    private static volatile HttpDownAPKUtils mHttpDownAPKUtils;
    
    private String md5;
    
	public static HttpDownAPKUtils getInstance(Context context,String address,int type) {
        if (mHttpDownAPKUtils == null) {
            synchronized (AliyunSDKUtils.class) {
                if (mHttpDownAPKUtils == null) {
                	mHttpDownAPKUtils = new HttpDownAPKUtils(context, address,type);
                }
            }
        }
        return mHttpDownAPKUtils;
    }
	
	public HttpDownAPKUtils(Context context,String address,int type) {
		this.context=context;
		this.address = address;
		this.type=type;
		this.isFirst=true;
	}
	
	public void setUrl(String address,int type) {
		this.address = address;
		this.type=type;
		this.isFirst=true;
	}
	
	@Override
	public void run() {
        try {
        	isDown=true;
        	String[] strArray =address.split("/");
    		apkNameString=strArray[strArray.length-1];
    		File tmpFile = new File(downloadPath);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            File file=new File(downloadPath + apkNameString);
            if(file.exists()){//判断apk是否已经存在
                readSize=file.length();
            }
            URL url = new URL(address);
            HttpURLConnection connTwo=null;
            try {
            	connTwo = (HttpURLConnection) url
                        .openConnection();
                connTwo.setReadTimeout(5000);
                connTwo.setConnectTimeout(5000);
                contentLength=connTwo.getContentLength();
                md5=connTwo.getHeaderFields().get("ETag").get(0).toLowerCase();
                if(FileUtils.isDownLoad(contentLength)){//判断剩余空间
	                if(readSize==contentLength){//已下载apk等于目标apk
	                	AliyunSDKUtils.getInstance(context).putLogTst("[APK_EXIST]"+apkNameString,2);
	                	cheakApk(downloadPath+ apkNameString,type,file);//检查apk合法性
	                }else if(readSize>contentLength){//已下载apk比目标apk要大，直接删除
	                	file.delete();
	                	downApk(type, file,0);
	                }else{//已下载apk比目标apk要小，断点下载
	                	downApk(type, file,readSize);
	                }
                }else{
                	BootConfigTask.isSuccess=false;
                	AliyunSDKUtils.getInstance(context).putLogTst("[SPACE_INSUFFICIENT]"+apkNameString+":"+FileUtils.getAvailableSize(),2);
                }
            } catch (IOException e) {
            	isDown=false;
            	BootConfigTask.isSuccess=false;
                AliyunSDKUtils.getInstance(context).putLogTst("[DOWNLOAD_FATAL_A]"+apkNameString+":"+e.toString(),3);
                e.printStackTrace();
            } finally {           
                if (connTwo != null) {
                    try {
                    	connTwo.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
        	isDown=false;
        	BootConfigTask.isSuccess=false;
            AliyunSDKUtils.getInstance(context).putLogTst("[DOWNLOAD_FATAL_B]"+apkNameString+":"+e.toString(),3);
            e.printStackTrace();
        }
	}
	
	private void cheakApk(String path,int type,File file){
	      if(md5.contains(ApkUtils.getFileMD5(file))){//MD5校验合法通知系统安装
	    	 AliyunSDKUtils.getInstance(context).putLogTst("[APK_INSTALL]"+apkNameString,2);
	    	 if(type==1){	    
                Intent intent = new Intent("action.install.apk");
                intent.putExtra("path", downloadPath+ apkNameString);
                intent.putExtra("package_name", "com.rongyan.hpmessage");
                context.sendBroadcast(intent);
        	}else if(type==2){       		
        		Intent intent = new Intent("action.install.apk");
                intent.putExtra("path", downloadPath+ apkNameString);
                intent.putExtra("package_name", "com.rongyan.appstore");
                context.sendBroadcast(intent);
        	}  
	    	isDown=false;
	      }else{//校验非法删除已下载apk
	    	  LogUtils.w(TAG, "apk illegal");	
	          AliyunSDKUtils.getInstance(context).putLogTst("[APK_ILLEGAL]"+apkNameString,2);  
	          file.delete();
	          if(isFirst){
	        	  isFirst=false;
	        	  downApk(type, file,0);
	          }else{
	        	  isDown=false;
	          }
	      }
	}
	
	private void downApk(int type,File file,long length){	
        HttpURLConnection conn=null;
        InputStream is = null;
        RandomAccessFile raf=null;
        try {
        	URL url = new URL(address);
        	conn = (HttpURLConnection) url
                    .openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            LogUtils.w(TAG, "bytes=" + length + "-"+(contentLength - 1));
            AliyunSDKUtils.getInstance(context).putLogTst("[START_DOWNLOAD]"+apkNameString+":"+"bytes=" + length + "-"+(contentLength - 1),2);		                   
            conn.setRequestProperty("Range", "bytes=" + length + "-"+(contentLength - 1));
            is = conn.getInputStream();
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(length);
            byte[] buf = new byte[8192];
            conn.connect();
            LogUtils.w(TAG, "conn.getResponseCode()="+conn.getResponseCode());
            if (conn.getResponseCode() == 206) {
                while (true) {
                    if (is != null) {
                        int numRead = is.read(buf);
                        if (numRead <= 0) {
                            break;
                        } else {
                            raf.write(buf, 0, numRead);
                        }
                    } else {
                        break;
                    }
                }
                LogUtils.w(TAG, "download successful");	
                cheakApk(downloadPath+ apkNameString,type,file);//下载完毕校验apk合法性                   
            } else{
            	BootConfigTask.isSuccess=false;
            }
        } catch (IOException e) {
        	isDown=false;
        	BootConfigTask.isSuccess=false;
            AliyunSDKUtils.getInstance(context).putLogTst("[DOWNLOAD_FATAL_C]"+apkNameString+":"+e.toString(),3);
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }         
        }
	}
}
