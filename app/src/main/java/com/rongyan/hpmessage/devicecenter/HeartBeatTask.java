package com.rongyan.hpmessage.devicecenter;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.item.Devices;
import com.rongyan.hpmessage.util.AliyunSDKUtils;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.NewHttpPostUtils;
import com.rongyan.hpmessage.util.StringUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

public class HeartBeatTask implements NewHttpPostUtils.CallBack{

	private final static String TAG="HeartBeatTask";
	
	private NewHttpPostUtils mHeartBeatUtils;

    private Handler mHandler = new Handler();
    
    private Timer mHeartBeatTimer;
    
    private Devices device=new Devices();
    
    public static boolean isSuccess=false;
    
	private static HeartBeatTask mHeartBeatTask;
	
	private static Context mContext;
    
    public static HeartBeatTask getIntance(Context context) {
		if (mHeartBeatTask == null) {
			mHeartBeatTask = new HeartBeatTask();
			mContext=context;
		}
		return mHeartBeatTask;
	}
    
    public void startTimer() {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mHeartBeatTimer != null) {
            	mHeartBeatTimer.cancel();
            }
            mHeartBeatTimer = new Timer();
            mHeartBeatTimer.schedule(new StateTask(),5000, ApplicationUtils.heartbeat_duration*60*1000);
        }
	}
    
    class StateTask extends TimerTask {

        @Override
        public void run() {
        	try{
        		device.getDevice().setNet_flow(StringUtils.getDev()+"");
        		device.getDevice().setFire_entity_id(ApplicationUtils.getentityId());
        		device.getDevice().setMac(StringUtils.getLocalMacAddress());
        		device.getDevice().setSn(Build.SERIAL);
	        	String packagesString = JsonUtils.beanToJson(device);
	        	AliyunSDKUtils.getInstance(mContext).putLogTst("[HeartBeat]"+packagesString,1);
	 	    	LogUtils.w(TAG, packagesString);
	 	    	startTimer(packagesString);
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
        }
    }
    
	public void startTimer(String data) {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mHeartBeatTimer != null) {
            	mHeartBeatTimer.cancel();
            }
            mHeartBeatTimer = new Timer();
            mHeartBeatTimer.schedule(new StatesTask(data), 0);
        }
	}
	
	class StatesTask extends TimerTask {

		private String mData;

		StatesTask(String data){
            if(data!=null) {
            	mData = data;
            }
        }
        @Override
        public void run() {
        	mHeartBeatUtils = new NewHttpPostUtils(HeartBeatTask.this,MessageApplication.HTTP_HEARTBEAT_STRING,mHandler,mData);
        	ThreadPoolUtils.newFixThreadPool(mHeartBeatUtils);
        }
    }

	@Override
	public void setPostResponseData(String value) {
		// TODO Auto-generated method stub
		LogUtils.w(TAG, value);
	}

	@Override
	public void setPostFailedResponse(String value) {
		// TODO Auto-generated method stub
		LogUtils.w(TAG, value);
	}

	@Override
	public void setPostTimeoutResponse(String value) {
		// TODO Auto-generated method stub
		LogUtils.w(TAG, value);
	}
}
