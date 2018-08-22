package com.rongyan.hpmessage.devicecenter;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.item.SettingsItem;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.NewHttpGetUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

public class SettingsTask implements NewHttpGetUtils.CallBack{

	private final static String TAG="SettingsTask";
	
    private NewHttpGetUtils mSettingsUtils;

    private Handler mHandler = new Handler();
    
    private Timer mSettingsTimer;
    
    private static OnlineOpera mOnlineOpera;
    
    public static boolean isSuccess=false;
    
	private static SettingsTask mSettingsTask;
	
	private static Context mContext;
    
    public static SettingsTask getIntance(OnlineOpera onlineOpera,Context context) {
		if (mSettingsTask == null) {
			mSettingsTask = new SettingsTask();
			mOnlineOpera=onlineOpera;
			mContext=context;
		}
		return mSettingsTask;
	}
    
	public void startTimer() {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mSettingsTimer != null) {
            	mSettingsTimer.cancel();
            }
            mSettingsTimer = new Timer();
            mSettingsTimer.schedule(new SettingTask(), 5000);
        }
	}
	
	class SettingTask extends TimerTask {

        @Override
        public void run() {
        	mSettingsUtils = new NewHttpGetUtils(SettingsTask.this,MessageApplication.HTTP_SETTINGS_STRING, mHandler);
        	ThreadPoolUtils.newFixThreadPool(mSettingsUtils);
        }
    }
	
	@Override
	public void setResponseData(String value) {
		LogUtils.w(TAG, value);
		try {
        	SettingsItem item = (SettingsItem) JsonUtils
                    .jsonToBean(value, SettingsItem.class);
            if (item != null && item.isSuccess()) {
            	isSuccess=true;
            	ApplicationUtils.collection_duration=item.getData().getSettings().getCollection_duration();
            	ApplicationUtils.report_duration=item.getData().getSettings().getReport_duration();
            	ApplicationUtils.heartbeat_duration=item.getData().getSettings().getHeartbeat_duration();
            	OnlineTask.getIntance(mOnlineOpera).startTimer();
            	HeartBeatTask.getIntance(mContext).startTimer();
            }
		}catch(Exception e){
			LogUtils.w(TAG, e.toString());
			e.printStackTrace();
		}
		
	}

	@Override
	public void setFailedResponse(String value) {
		isSuccess=false;
	}

	@Override
	public void setTimeoutResponse(String value) {
		isSuccess=false;
	}


}
