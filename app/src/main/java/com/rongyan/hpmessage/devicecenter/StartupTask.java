package com.rongyan.hpmessage.devicecenter;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.util.AliyunSDKUtils;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.NewHttpPostUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

public class StartupTask implements NewHttpPostUtils.CallBack{

	private final static String TAG="StartupTask";
	
	private NewHttpPostUtils mSettingsUtils;

    private Handler mHandler = new Handler();
    
    private Timer mSettingsTimer;
    
    public static boolean isSuccess=false;
    
	private static StartupTask mStartupTask;
	
	private static Context mContext;
    
    public static StartupTask getIntance(Context context) {
		if (mStartupTask == null) {
			mStartupTask = new StartupTask();
			mContext=context;
		}
		return mStartupTask;
	}
    
    public void start(){
    	try{
    		isSuccess=true;
    		HashMap<String, String> map = new HashMap<String,String>();
    		map.put("device_sn", Build.SERIAL);
    		String packagesString = JsonUtils.beanToJson(map);
    		AliyunSDKUtils.getInstance(mContext).putLogTst("[POST_STARTUP]"+packagesString,1);
    		LogUtils.w(TAG, packagesString);
    		startTimer(packagesString);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
	public void startTimer(String data) {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mSettingsTimer != null) {
            	mSettingsTimer.cancel();
            }
            mSettingsTimer = new Timer();
            mSettingsTimer.schedule(new SettingTask(data), 5000);
        }
	}
	
	class SettingTask extends TimerTask {

		private String mData;

		SettingTask(String data){
            if(data!=null) {
            	mData = data;
            }
        }
        @Override
        public void run() {
        	mSettingsUtils = new NewHttpPostUtils(StartupTask.this,MessageApplication.HTTP_STARTUP_STRING,mHandler, mData);
        	ThreadPoolUtils.newFixThreadPool(mSettingsUtils);
        }
    }
	

	@Override
	public void setPostResponseData(String value) {
		AliyunSDKUtils.getInstance(mContext).putLogTst("[RETURN_STARTUP_SUCCESS]"+value,1);
		isSuccess=true;
		
	}

	@Override
	public void setPostFailedResponse(String value) {
		AliyunSDKUtils.getInstance(mContext).putLogTst("[RETURN_STARTUP_FATAL]"+value,2);
		isSuccess=false;
	}

	@Override
	public void setPostTimeoutResponse(String value) {
		AliyunSDKUtils.getInstance(mContext).putLogTst("[RETURN_STARTUP_FATAL]"+value,2);
		isSuccess=false;
	}

}
