package com.rongyan.hpmessage.apks;

import android.content.Context;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.ShowWindowObserver;
import com.rongyan.hpmessage.item.ApksResponseItem;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;

public class AppStoreTask extends AbstractTask{
	private final static String TAG="AppStoreTask";
	
	private static AppStoreTask mAppStoreTask;
	
	private String version;
	
	public static boolean isDownSuccess=true;
	
	public static AppStoreTask getInstance(Context context, String url,
			ShowWindowObserver observer) {
		if (mAppStoreTask == null) {
			mAppStoreTask = new AppStoreTask(context, url, observer);
		}
		return mAppStoreTask;
	}
	
	public AppStoreTask(Context context, String url, ShowWindowObserver observer) {
		super(context, url, observer);
	}

	@Override
	public void setResponseData(String value) {
		try{
			LogUtils.w(TAG,value);
			ApksResponseItem item = (ApksResponseItem) JsonUtils
					.jsonToBean(value, ApksResponseItem.class);
			if (item.isSuccess() && item.getData() != null) {
				if(ApplicationUtils.compareVersion(version, item.getData().getApk().getVersion())){
					if(item.getData().getApk().getApk_file_url()!=null&&!item.getData().getApk().getApk_file_url().equals("")){
						startHttpGetConnect(item.getData().getApk().getApk_file_url(),2);
					}
				}else{
					AppStoreTask.isDownSuccess=true;
				}
			} else {
				//startTimer(60000);请求体为空 说明已经下架 不需要重复请求
				AppStoreTask.isDownSuccess=true;//设置标志位 不需要重复请求
			}
		}catch(Exception e){
			e.printStackTrace();
//			startTimer(60000);
		}
		
	}

	@Override
	public void startShowView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFailedMessage() {
		if (ApplicationUtils.ismNetWorkEnable()) {
			startTimer(60000);
		}
	}

	@Override
	public void setMessageListResponseData(String value) {
		// TODO Auto-generated method stub
		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setResponseData(ApksResponseItem.Apk item) {
		try{
			if(item != null &&ApplicationUtils.compareVersion(version, item.getVersion())){
				if(item.getApk_file_url()!=null&&!item.getApk_file_url().equals("")){
					startHttpGetConnect(item.getApk_file_url(),2);
				}
			}else{
				AppStoreTask.isDownSuccess=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
