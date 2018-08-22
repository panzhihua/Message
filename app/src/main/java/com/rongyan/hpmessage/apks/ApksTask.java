package com.rongyan.hpmessage.apks;

import android.R.integer;
import android.content.Context;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.ShowWindowObserver;
import com.rongyan.hpmessage.item.ApksResponseItem;
import com.rongyan.hpmessage.util.ACache;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.StringUtils;

public class ApksTask extends AbstractTask{

	private final static String TAG="ApksTask";
	
	private static ApksTask mApksTask;
	
	public static boolean isDownSuccess=false;
	
	public static ApksTask getInstance(Context context, String url,
			ShowWindowObserver observer) {
		if (mApksTask == null) {
			mApksTask = new ApksTask(context, url, observer);
		}
		return mApksTask;
	}
	
	public ApksTask(Context context, String url, ShowWindowObserver observer) {
		super(context, url, observer);
	}

	@Override
	public void setResponseData(String value) {
		try{
			LogUtils.w(TAG,value);
			ApksResponseItem item = (ApksResponseItem) JsonUtils
					.jsonToBean(value, ApksResponseItem.class);
			if (item!=null&&item.isSuccess() && item.getData() != null) {
				if(ApplicationUtils.compareVersion(ApplicationUtils.getAppVersion(mContext), item.getData().getApk().getVersion())){
					if(item.getData().getApk().getApk_file_url()!=null&&!item.getData().getApk().getApk_file_url().equals("")){
						startHttpGetConnect(item.getData().getApk().getApk_file_url(),1);
					}
				}else{
					ApksTask.isDownSuccess=true;
				}
			} else {
				//startTimer(60000);请求体为空 说明已经下架 不需要重复请求
				ApksTask.isDownSuccess=true;//设置标志位 不需要重复请求
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
	

	public void setResponseData(ApksResponseItem.Apk item) {
		try{
			if(item != null &&ApplicationUtils.compareVersion(ApplicationUtils.getAppVersion(mContext), item.getVersion())){
				if(item.getApk_file_url()!=null&&!item.getApk_file_url().equals("")){
					startHttpGetConnect(item.getApk_file_url(),1);
//					String numString=mCache.getAsString("download_sum");
//					String timeString=mCache.getAsString("download_time");
//					if(numString==null||numString.equals("")){
//						mCache.put("download_sum","0");
//						startHttpGetConnect(item.getApk_file_url(),1);
//					}else{
//						int num=Integer.parseInt(numString);
//						if(num<2){
//							mCache.put("download_sum", String.valueOf(num++));
//							startHttpGetConnect(item.getApk_file_url(),1);
//						}else{
//							if(timeString==null||timeString.equals("")){
//								mCache.put("download_sum", String.valueOf(num++));
//								startHttpGetConnect(item.getApk_file_url(),1);
//							}else if(StringUtils.getSystemTime()-Integer.parseInt(timeString)>24*60*60){//超过一天
//								mCache.put("download_sum","0");
//								mCache.put("download_time", String.valueOf(StringUtils.getSystemTime()));
//								startHttpGetConnect(item.getApk_file_url(),1);
//							}
//						}
//					}
					
				}
			}else{
				ApksTask.isDownSuccess=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
