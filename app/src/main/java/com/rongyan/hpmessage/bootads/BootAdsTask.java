package com.rongyan.hpmessage.bootads;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.ShowWindowObserver;
import com.rongyan.hpmessage.cache.Cache;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.database.DatabaseColume;
import com.rongyan.hpmessage.item.AdsItem;
import com.rongyan.hpmessage.item.BootAdsResponseItem;
import com.rongyan.hpmessage.item.MessageinfoResponseItem;
import com.rongyan.hpmessage.item.BootAdsResponseItem.Data.Ads;
import com.rongyan.hpmessage.item.MessageListResponseItem.Data.Notifications;
import com.rongyan.hpmessage.item.MessageinfoResponseItem.Notificationinfo;
import com.rongyan.hpmessage.messagelist.MessageListTask;
import com.rongyan.hpmessage.util.HttpGetUtils;
import com.rongyan.hpmessage.util.ImageUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.MyBitmapUtils;
import com.rongyan.hpmessage.util.StringUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

public class BootAdsTask extends AbstractTask implements ShowWindowObserver.CallBack {

	static BootAdsTask mBootAdsTask;
	public BootAdsView nBootAdsView;
	private int notificationId = -1;
	private MyBitmapUtils myBitmapUtils;
	private DataBaseOpenHelper mDataBaseOpenHelper;
	private MessageListTask mMessageListTask = null;
	public static final String MPICTUREPATH_STRING = Environment
			.getExternalStorageDirectory().getPath()
			+ "/messageservice/bootads/";
	private final String TAG = "BootAdsTask";
	private int sum=0;//总数量
	private int succeedSum=0;//成功数量
	private int failSum=0;//失败数量
	private List<String> paths=new ArrayList<>();

	public static BootAdsTask getInstance(Context context, String url,
			ShowWindowObserver observer) {
		if (mBootAdsTask == null) {
			mBootAdsTask = new BootAdsTask(context, url, observer);
		}
		return mBootAdsTask;
	}

	public BootAdsTask(Context context, String url, ShowWindowObserver observer) {
		super(context, url, observer);
		mDataBaseOpenHelper = DataBaseOpenHelper.getInstance(context);
		nBootAdsView = new BootAdsView(this,context,mDataBaseOpenHelper);		
		myBitmapUtils= new MyBitmapUtils();
	}

	public void setMessageListTask(MessageListTask task) {
		mMessageListTask = task;
	}

	/**
	 * HttpGetUtils 返回成功回调
	 */
	@Override
	public void setResponseData(final String value) {
		// 增加一个线程访问网络
		new Thread(new Runnable() {
           @Override
           public void run() {
				try {
					LogUtils.w(TAG, value);
					BootAdsResponseItem item = (BootAdsResponseItem) JsonUtils
							.jsonToBean(value, BootAdsResponseItem.class);
					if (item.getSuccess() && item.getData() != null) {
						paths.clear();
						nBootAdsView.setInitList();
						final List<Ads> list = item.getData().getAds();
						sum=list.size();
						succeedSum=0;
						failSum=0;
						LogUtils.w(TAG, "sum="+sum);
						if (sum > 0) {
							if(sum>5){
								sum=5;
							}
							for(int i=sum-1;i>-1;i--){
								final Ads ads=list.get(i);
								if(ads!=null){
									String imageurl = ads.getImage_path();
									paths.add(imageurl.substring(imageurl.length()-14));
									if(imageurl!=null){
										notificationId = ads.getNotification_id();
										final Bitmap bitmap=myBitmapUtils.disPlay(imageurl,MyBitmapUtils.TYPE_AD);
										if(bitmap!=null){
											LogUtils.w(TAG, "Notification_id="+notificationId);
											if (!ads.isAd_only()&&!mDataBaseOpenHelper.isExist(String.valueOf(ads.getNotification_id()),DatabaseColume.MESSAGE)) {
												adNotification(MessageApplication.HTTP_NOTICATIONS_URL
														+ ads.getNotification_id());
											}
											if(!mDataBaseOpenHelper.isExistAds(ads.getId())){
												// 把广告放入数据库											
												AdsItem mAdsItem = new AdsItem();
												mAdsItem.setId(ads.getId());													
												mAdsItem.setTime(0);
												mAdsItem.setNumber(0);
												mDataBaseOpenHelper.AddAds(mAdsItem);
											}
											recycle(bitmap);
											nBootAdsView.setImageViewList(imageurl,  ads);//设置图片
											setNum(true,true);
										}else{
											setNum(false,true);
										}
									}
								}
							}
						}else{
							issuccess = true;
							hideView();// 获取不到就不显示view
							showWindow = false;
						}
					} else {
						issuccess = true;
						hideView();// 获取不到就不显示view
						showWindow = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					startTimer(60000);
				}
             }
         }).start();
	}

	public boolean isBitmapExitsAndUse() {
		File fileExit = new File(MPICTUREPATH_STRING);
		File[] files = fileExit.listFiles();// 列出所有文件 
		 // 将所有文件存入list中  
        if(files != null&&files.length>0){ 
        	try {
	        	File file = files[0]; 
				if (file.exists()) {					
					if (mBitmap == null) {
						mBitmap = BitmapFactory.decodeFile(MPICTUREPATH_STRING+file.getName());
					}
					return true;					
				}
        	} catch (Exception e) {
				e.printStackTrace();
			}
        }
		return false;
	}
	
	public void deleteFile() {
		File fileExit = new File(MPICTUREPATH_STRING);
		File[] files = fileExit.listFiles();// 列出所有文件 
		 // 将所有文件存入list中  
        if(files != null){
        	for(int i=0;i<files.length;i++){
	        	File file = files[i]; 
				if (file.exists()) {					
					boolean isLike=false;
					for(String filename:paths){
						if(filename!=null&&filename.equals(file.getName())){
							isLike=true;
							break;
						}
					}
					if(!isLike){
						file.delete();
					}
				}
        	}
        }
	}
	
	public void adNotification(String url) {
		ThreadPoolUtils.newFixThreadPool(new HttpGetUtils(this, url, mHandler));
	}

	/**
	 * HttpGetImageUtils 返回失败回调
	 */
	@Override
	public void setFailedMessage() {
		setNum(false,true);		
	}

	/**
	 * 显示view
	 */
	@Override
	public void startShowView() {
		if (issuccess && showWindow && mShowWindowObserver.isShowWindow()) {
			nBootAdsView.startView();
		}
	}

	/**
	 * 隐藏view
	 */
	@Override
	public void hideView() {
		nBootAdsView.removeView();
	}

	/**
	 * ShowWindowObserver.CallBack 接口回调
	 */
	@Override
	public void updateWindow(boolean show) {
		if (show) {
			startShowView();
		} else {
			hideView();
		}
	}

	public void startShowViewWithBitMap() {
		try {
			if (mBitmap != null) {
				nBootAdsView.setImageView(mBitmap, notificationId);
				LogUtils.w(TAG, "startShowViewWithBitMap()");
				if (mShowWindowObserver.isShowWindow()) {
					nBootAdsView.startView();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 消息详情服务器返回数据
	 */
	@Override
	public void setMessageListResponseData(final String value) {
		new Thread(new Runnable() {
	           @Override
	           public void run() {
	        	   LogUtils.w(TAG, "setMessageListResponseData()");
					MessageinfoResponseItem item;
					try {
						item = (MessageinfoResponseItem) JsonUtils.jsonToBean(value,
								MessageinfoResponseItem.class);
			
						if (item != null && item.isSuccess() && item.getData() != null) {
							Notificationinfo mNotificationinfo = item.getData()
									.getNotification();
							if (mNotificationinfo != null) {
								// 把消息内容放入缓存
								if (Cache.get(String.valueOf(mNotificationinfo.getId())
										+ "+" + DatabaseColume.MESSAGE) == null) {
									Cache.add(
											String.valueOf(mNotificationinfo.getId() + "+"
													+ DatabaseColume.MESSAGE),
											mNotificationinfo.getContent());
								}
								// 把消息内容放入数据库
								if (!mDataBaseOpenHelper
										.isExist(String.valueOf(mNotificationinfo.getId()),
												DatabaseColume.MESSAGE)) {
									Notifications notification = new Notifications();
									notification.setId(mNotificationinfo.getId());
									notification.setPreview_icon(mNotificationinfo
											.getPreview_icon());
									notification.setTitle(mNotificationinfo.getTitle());
									notification.setSummary(mNotificationinfo.getSummary());
									notification.setRead(false);
									notification.setPushed_at(StringUtils.getSystemTime());
									notification.setType(Integer.valueOf(DatabaseColume.MESSAGE));
									mDataBaseOpenHelper.Add(notification);
									mContext.sendBroadcast(MessageApplication.mIntent);// 告知系统有新的信息
									if (mMessageListTask != null) {
										mMessageListTask.updateList(notification);// 更新桌面弹出框
									}
								}
							}
						}  
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	       
		}).start();
	}

	
	private void showAD(int failSum, final int succeedSum,final boolean isOpen){
		if((failSum+succeedSum)==sum){
			deleteFile();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					issuccess = true;
					if(succeedSum==0){
						hideView();// 一个成功的都没有就不显示view
						showWindow = false;
					}else{
						showWindow = true;
						nBootAdsView.setInitI();
						if(isOpen){
							if(nBootAdsView.setImageView(true)){//更换图片
								LogUtils.w(TAG, "A");
								nBootAdsView.setView(true,true);
								if (mShowWindowObserver.isShowWindow()) {//在首页直接弹出来
									if(!nBootAdsView.isShow()){										
										nBootAdsView.startView();
									}
								}else{//不在首页等首次回首页再弹出来
									nBootAdsView.setOpen(true);
								}
							}else{
								
								if (mShowWindowObserver.isShowWindow()) {
									LogUtils.w(TAG, "B");
									nBootAdsView.startView();
									nBootAdsView.setView(false,false);									
								}else{
									LogUtils.w(TAG, "C");
									nBootAdsView.setView(false,false);
									hideView();
								}
							}
						}else{
							nBootAdsView.setImageView(true);
						}
					}
				}
			});
		}		
	}
	
	private void setNum(boolean results, boolean open){
		LogUtils.w(TAG, "results="+results+",open="+open);
		synchronized(this) {
			if(results){
				succeedSum=succeedSum+1;
			}else{
				failSum=failSum+1;
			}
			showAD(failSum,succeedSum,open);
		}
	}
	
	public void setResponseData(final List<BootAdsResponseItem.Data.Ads> adsList) {
		// 增加一个线程访问网络
		new Thread(new Runnable() {
           @Override
           public void run() {
				try {
					if (adsList!= null) {
						paths.clear();
						nBootAdsView.setInitList();
						final List<Ads> list = adsList;
						sum=list.size();
						succeedSum=0;
						failSum=0;
						LogUtils.w(TAG, "sum="+sum);
						if (sum > 0) {
							if(sum>5){
								sum=5;
							}
							for(int i=sum-1;i>-1;i--){
								final Ads ads=list.get(i);
								if(ads!=null){
									String imageurl = ads.getImage_path();
									paths.add(imageurl.substring(imageurl.length()-14));
									if(imageurl!=null){
										notificationId = ads.getNotification_id();
										final Bitmap bitmap=myBitmapUtils.disPlay(imageurl,MyBitmapUtils.TYPE_AD);
										if(bitmap!=null){
											LogUtils.w(TAG, "Notification_id="+notificationId);
											if (!ads.isAd_only()&&!mDataBaseOpenHelper.isExist(String.valueOf(ads.getNotification_id()),DatabaseColume.MESSAGE)) {
												adNotification(MessageApplication.HTTP_NOTICATIONS_URL
														+ ads.getNotification_id());
											}
											if(!mDataBaseOpenHelper.isExistAds(ads.getId())){
												// 把广告放入数据库											
												AdsItem mAdsItem = new AdsItem();
												mAdsItem.setId(ads.getId());													
												mAdsItem.setTime(0);
												mAdsItem.setNumber(0);
												mDataBaseOpenHelper.AddAds(mAdsItem);
											}
											recycle(bitmap);
											nBootAdsView.setImageViewList(imageurl,  ads);//设置图片
											setNum(true,true);
										}else{
											setNum(false,true);
										}
									}
								}
							}
						}else{
							issuccess = true;
							hideView();// 获取不到就不显示view
							showWindow = false;
						}
					} else {
						issuccess = true;
						hideView();// 获取不到就不显示view
						showWindow = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					startTimer(60000);
				}
             }
         }).start();
	}
}
