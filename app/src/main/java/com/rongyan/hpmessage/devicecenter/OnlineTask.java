package com.rongyan.hpmessage.devicecenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Handler;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.item.StateItem;
import com.rongyan.hpmessage.item.StateItem.Package_States;
import com.rongyan.hpmessage.item.States;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.NewHttpPostUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

public class OnlineTask implements NewHttpPostUtils.CallBack{
	
	private final static String TAG="OnlineTask";
	
	private NewHttpPostUtils mOnlineUtils;

    private Handler mHandler = new Handler();
    
    private Timer mOnlineTimer,mStateTimer;
    
    private String packagesString;
    
    public static boolean isAgain=true;
    
    private static int num=2;
    
    private static int interval;
    
	private static OnlineTask mOnlineTask;
	
	private static OnlineOpera mOnlineOpera;
	
	private List<StateItem> stateItemList=new ArrayList<>();
	
	private States state=new States();
	
    public static OnlineTask getIntance(OnlineOpera onlineOpera) {
		if (mOnlineTask == null) {
			mOnlineTask = new OnlineTask();
			mOnlineOpera=onlineOpera;
			interval=ApplicationUtils.report_duration/ApplicationUtils.collection_duration;
			num=interval-1;
		}
		return mOnlineTask;
	}
  
    public void startTimer() {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mStateTimer != null) {
            	mStateTimer.cancel();
            }
            mStateTimer = new Timer();
            mStateTimer.schedule(new StateTask(),5000, ApplicationUtils.collection_duration*60*1000);
        }
	}
    
    class StateTask extends TimerTask {

        @Override
        public void run() {
        	try{
	        	num++;
	        	stateItemList.add(mOnlineOpera.getOnlineItem());
	        	if(stateItemList.size()>100){
	        		List<StateItem> subList = stateItemList.subList(3, stateItemList.size()-1);
	        		stateItemList=subList;
	        	}
	        	LogUtils.w(TAG, num+"==="+interval);
	        	if(num>=interval){
	        		state.setStates(stateItemList);	 
	        		state.setDevice_sn(Build.SERIAL);
	        		packagesString = JsonUtils.beanToJson(state);
	 	    		LogUtils.w(TAG, packagesString);
	 	    		startTimer(packagesString);
	 	    		num=0;
	        	}
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
        }
    }
    
	public void startTimer(String data) {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mOnlineTimer != null) {
            	mOnlineTimer.cancel();
            }
            mOnlineTimer = new Timer();
            mOnlineTimer.schedule(new StatesTask(data), 0);
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
        	mOnlineUtils = new NewHttpPostUtils(OnlineTask.this,MessageApplication.HTTP_STATE_STRING,mHandler,mData);
        	ThreadPoolUtils.newFixThreadPool(mOnlineUtils);
        }
    }

	@Override
	public void setPostResponseData(String value) {
		LogUtils.w(TAG, value);
		stateItemList.clear();
		isAgain=true;	
	}

	@Override
	public void setPostFailedResponse(String value) {
		if(isAgain){
			startTimer(packagesString);
			isAgain=false;
		}else{
			isAgain=true;
		}
		
	}

	@Override
	public void setPostTimeoutResponse(String value) {
		if(isAgain){
			startTimer(packagesString);
			isAgain=false;
		}else{
			isAgain=true;
		}
		
	}

}
