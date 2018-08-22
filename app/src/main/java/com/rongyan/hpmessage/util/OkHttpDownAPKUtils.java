package com.rongyan.hpmessage.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.rongyan.hpmessage.item.Apps;
/**
 * OkHttp下载类
 */

public class OkHttpDownAPKUtils extends Thread{
	// 下载文件 存放目的地
    public final static String downloadPath = Environment.getExternalStorageDirectory()
            .getPath() + "/AppStore/download_apk/";

    private final static String TAG="OkHttpDownAPKUtils";

    private long contentLength = 0;//服务器返回的数据长度

    private long readSize = 0L;//已下载的总大小

    private String appname;//下载apk名字

    private int mState;//状态

    private progress mProgress;//定义一个接口变量

    private int num;

    private Apps app;
    
    private Context mContext;

    /**
     * 定义一个接口
     */
    public interface progress{
        void putProgress(int progress,int state,String appNo);//返回下载状态和进度
    }

    public OkHttpDownAPKUtils(Context context,progress mProgress, Apps app, String appname, int state) {
    	this.mContext=context;
        this.mProgress=mProgress;
        this.appname=appname;
        this.mState=state;
        this.app=app;
    }

    @Override
    public void run() {
        File tmpFile = new File(downloadPath);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        final File file = new File(downloadPath + appname);
        try {
            if(file.exists()){//判断apk是否已经存在
                if(file.length()<app.getPackage_size()) {//判断apk是否下载完成
                    file.delete();//下载失败，删除已下载部分重新下载
                }else{
                    mProgress.putProgress(-2, mState, app.getNo());
                    return;
                }
            }
            LogUtils.w(TAG, "start download");
            AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_START_DOWNLOAD]"+appname,2);
            URL url = new URL(app.getPackage_url());
            HttpURLConnection conn=null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                conn = (HttpURLConnection) url
                        .openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                contentLength = conn.getContentLength();
                is = conn.getInputStream();
                fos = new FileOutputStream(file);
                byte[] buf = new byte[8192];
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    num=0;
                    while (true) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                            	LogUtils.w(TAG, "download successful");	
                            	AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_DOWNLOAD_SUCCESS]"+appname,2);
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                                num++;
                                readSize = readSize + numRead;//累加已下载的大小
                                if (num>1599||(int) (readSize * 100 / contentLength)==100){
                                    num=0;
                                    mProgress.putProgress((int) (readSize * 100 / contentLength), mState,app.getNo());
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            } catch (IOException e) {        
            	 AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_DOWNLOAD_FATAL_A]"+appname+":"+e.toString(),3);
                mProgress.putProgress(-1, mState,app.getNo());
                file.delete();//下载失败，删除已下载部分
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
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
        } catch (Exception e) {
        	 AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_DOWNLOAD_FATAL_B]"+appname+":"+e.toString(),3);
            mProgress.putProgress(-1, mState,app.getNo());
            file.delete();//下载失败，删除已下载部分
            e.printStackTrace();
        }
    }
}
