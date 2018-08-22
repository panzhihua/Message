package com.rongyan.hpmessage.bootconfig;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.apks.ApksTask;
import com.rongyan.hpmessage.apks.AppStoreTask;
import com.rongyan.hpmessage.bootads.BootAdsTask;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.desktop.DesktopQSTask;
import com.rongyan.hpmessage.item.AppItem;
import com.rongyan.hpmessage.item.BootConfigItem;
import com.rongyan.hpmessage.util.AliyunSDKUtils;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.HttpGetUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

public class BootConfigTask implements HttpGetUtils.CallBack{

	private final static String TAG="BootConfigTask";
	
	private HttpGetUtils mBootConfigUtils;

    private Handler mHandler = new Handler();
    
    private Timer mBootConfigTimer;
    
    public static boolean isSuccess=false;
    
	private static BootConfigTask mBootConfigTask;
	
	private BootAdsTask mBootAdsTask;
	
	private DesktopQSTask mDesktopQSTask;
	
	private ApksTask mApksTask;
	
	private AppStoreTask mAppStoreTask;
	
	private DataBaseOpenHelper mDataBaseOpenHelper;
	
	private static Context mContext;
	
	public static BootConfigTask getIntance(BootAdsTask bootAdsTask,DesktopQSTask desktopQSTask,ApksTask apksTask,AppStoreTask appStoreTask,DataBaseOpenHelper dataBaseOpenHelper,Context context) {
		if (mBootConfigTask == null) {
			mBootConfigTask = new BootConfigTask(bootAdsTask,desktopQSTask,apksTask,appStoreTask,dataBaseOpenHelper);
			mContext=context;
		}
		return mBootConfigTask;
	}
	
	public BootConfigTask(BootAdsTask bootAdsTask,DesktopQSTask desktopQSTask,ApksTask apksTask,AppStoreTask appStoreTask,DataBaseOpenHelper dataBaseOpenHelper) {
		mBootAdsTask=bootAdsTask;
		mDesktopQSTask=desktopQSTask;
		mApksTask=apksTask;
		mAppStoreTask=appStoreTask;
		mDataBaseOpenHelper=dataBaseOpenHelper;
	}
    
    public void startTimer(int time) {
        if (ApplicationUtils.ismNetWorkEnable()){
        	AliyunSDKUtils.getInstance(mContext).putLogTst("[POST_BOOTCONFIG]",1);
            if (mBootConfigTimer != null) {
            	mBootConfigTimer.cancel();
            }
            mBootConfigTimer = new Timer();
            mBootConfigTimer.schedule(new BootConfig(),time);
        }
	}
    
    class BootConfig extends TimerTask {

        @Override
        public void run() {
        	mBootConfigUtils = new HttpGetUtils(BootConfigTask.this,MessageApplication.HTTP_BOOT_CONFIG_URL, mHandler);
        	ThreadPoolUtils.newFixThreadPool(mBootConfigUtils);
        }
    }

	@Override
	public void setResponseData(String value,String type,String time) {
		try {
			LogUtils.w(TAG, value);
			AliyunSDKUtils.getInstance(mContext).putLogTst("[RETURN_BOOTCONFIG_SUCCESS]"+value,1);
			BootConfigItem item = (BootConfigItem) JsonUtils
                    .jsonToBean(value, BootConfigItem.class);
            if (item != null && item.isSuccess()) {
            	isSuccess=true;        
            	mDesktopQSTask.setResponseData(item.getData().getBoot_config().getDesktop_entries());
            	mBootAdsTask.setResponseData(item.getData().getBoot_config().getAds());//侧边栏广告
            	mApksTask.setResponseData(item.getData().getBoot_config().getLatest_rongyan_notification_apk());
            	List<AppItem> appItemList=mDataBaseOpenHelper.getAppInfoList();
				if(appItemList!=null&&!appItemList.isEmpty()){
					for(AppItem appItem:appItemList){
						if(appItem.getPackage_().equals("com.rongyan.appstore")){
							mAppStoreTask.setVersion(appItem.getVersion());
							mAppStoreTask.setResponseData(item.getData().getBoot_config().getLatest_rongyan_appstore_apk());
							break;
						}
					}
				}         
            }
		}catch(Exception e){
			AliyunSDKUtils.getInstance(mContext).putLogTst("[RETURN_BOOTCONFIG_FATAL]"+e.toString(),2);
			LogUtils.w(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void setFailedResponse() {
    	isSuccess=false;
		AliyunSDKUtils.getInstance(mContext).putLogTst("[RETURN_BOOTCONFIG_FATAL]Failed",2);
		LogUtils.w(TAG, "Failed");
	}

}
