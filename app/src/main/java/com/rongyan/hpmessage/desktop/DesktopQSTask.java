package com.rongyan.hpmessage.desktop;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.rongyan.hpmessage.*;
import com.rongyan.hpmessage.item.BootConfigItem;
import com.rongyan.hpmessage.item.DesktopEntrancesItem;
import com.rongyan.hpmessage.item.DesktopQRcodeResponseItem;
import com.rongyan.hpmessage.item.DesktopEntrancesItem.Data.Desktop_Entries;
import com.rongyan.hpmessage.util.*;

public class DesktopQSTask extends AbstractTask implements
		ShowWindowObserver.CallBack {

	private static DesktopQSTask mDesktopQSTask;
	public DesktopQRcodeView mDesktopQRcodeView;
	private MyBitmapUtils myBitmapUtils;
	private String localimageurlString=null;
	private String localimagePointString=null;
	public static final String MPICTUREPATH_STRING = Environment
			.getExternalStorageDirectory().getPath()
			+ "/messageservice/desktop/";
	private final String TAG = "DesktopQSTask";

	public static DesktopQSTask getIntance(Context context, String url,
			ShowWindowObserver observer) {
		if (mDesktopQSTask == null) {
			mDesktopQSTask = new DesktopQSTask(context, url, observer);
		}
		return mDesktopQSTask;
	}

	public DesktopQSTask(Context context, String url,
			ShowWindowObserver observer) {
		super(context, url, observer);
		mDesktopQRcodeView = new DesktopQRcodeView(context);
		myBitmapUtils= new MyBitmapUtils();
	}

	@Override
	public void startShowView() {
		if (issuccess && showWindow && mShowWindowObserver.isShowWindow()) {
			mDesktopQRcodeView.startView();
		}
	}

	@Override
	public void hideView() {
		mDesktopQRcodeView.removeView();
	}

	/**
	 * 返回接口成功 返回json数据
	 */
	@Override
	public void setResponseData(String value) {
		try {
			LogUtils.w(TAG, "setResponseData data:" + value);
			DesktopEntrancesItem item = (DesktopEntrancesItem) JsonUtils
					.jsonToBean(value, DesktopEntrancesItem.class);
			if (item.getSuccess() && item.getData() != null&&item.getData().getDesktop_entries()!=null&&!item.getData().getDesktop_entries().isEmpty()) {
				for(final Desktop_Entries desktop_entries:item.getData().getDesktop_entries()){
					final String imageurl = desktop_entries.getIcon_url();
					LogUtils.w(TAG, "desktop imageurl :" + imageurl);
					if (imageurl != null && !imageurl.isEmpty()) {
						if (localimageurlString == null|| mBitmap == null) {
							if(localimageurlString!=null&&!localimageurlString.equals(imageurl.substring(imageurl.length()-14))){//如果本地二维码和网络二维码不一致就删除本地二维码
								File desktopfile = new File(Environment.getExternalStorageDirectory().getPath()+ "/messageservice/desktop/"+localimageurlString);
								try {
							        if (desktopfile!=null&&desktopfile.isFile() && desktopfile.exists()) {
							        	desktopfile.delete();
							        }
						    	}catch (Exception e1) {    
						         	e1.printStackTrace();   
						        }
							}
							new Thread(new Runnable() {
						           @Override
						           public void run() {
										try {
											issuccess = true;
											final Bitmap bitmap=myBitmapUtils.disPlay(imageurl,MyBitmapUtils.TYPE_DESKTOP);
											if(bitmap!=null){
												CacheUtils.putString(mContext, "Desktop",JsonUtils.beanToJson(desktop_entries));
												mHandler.post(new Runnable() {
													@Override
													public void run() {												
														mDesktopQRcodeView.setImageView(bitmap,desktop_entries);
														if (mShowWindowObserver.isShowWindow()) {
															mDesktopQRcodeView.startView();
														}
														showWindow = true;
													}
												});
											}else{
												hideView();
												showWindow = false;
											}
										}catch(Exception e){
											e.printStackTrace();
											issuccess = true;
											hideView();
											showWindow = false;
										}
						           }
					         }).start();
						}else if(!localimageurlString.equals(imageurl.substring(imageurl.length()-14))){
							File desktopfile = new File(Environment.getExternalStorageDirectory().getPath()+ "/messageservice/desktop/"+localimageurlString);
							try {
						        if (desktopfile!=null&&desktopfile.isFile() && desktopfile.exists()) {
						        	desktopfile.delete();
						        }
					    	}catch (Exception e1) {    
					         	e1.printStackTrace();   
					        }
							new Thread(new Runnable() {
						           @Override
						           public void run() {
										try {
											issuccess = true;
											final Bitmap bitmap=myBitmapUtils.disPlay(imageurl,MyBitmapUtils.TYPE_DESKTOP);
											if(bitmap!=null){
												CacheUtils.putString(mContext, "Desktop",JsonUtils.beanToJson(desktop_entries));
												mHandler.post(new Runnable() {
													@Override
													public void run() {
														mDesktopQRcodeView.setImageView(bitmap,desktop_entries);
														if (mShowWindowObserver.isShowWindow()) {
															mDesktopQRcodeView.startView();
														}
														showWindow = true;
													}
												});
											}else{
												hideView();
												showWindow = false;
											}
										}catch(Exception e){
											e.printStackTrace();
											issuccess = true;
											hideView();
											showWindow = false;
										}
						           }
					         }).start();					
						}else{
							CacheUtils.putString(mContext, "Desktop",JsonUtils.beanToJson(desktop_entries));
							mDesktopQRcodeView.setImageDesktopEntries(desktop_entries);
							issuccess = true;
							showWindow = true;
							startShowView();
						}
					}else{
						hideView();
						issuccess = true;
						showWindow = false;
					}
					break;
				}
			} else {
				// startTimer(60000);等于空说明下架了不去请求了
				hideView();
				issuccess = true;
				showWindow = false;
			}
		} catch (Exception e) {
			// startTimer(60000);
			hideView();
			issuccess = true;
			showWindow = false;
		}
	}

	/**
	 * 访问接口失败回调
	 */
	@Override
	public void setFailedMessage() {
		LogUtils.w(TAG, "setFailedMessage");
		if (ApplicationUtils.ismNetWorkEnable()) {
			startTimer(60000);
		}
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
    					localimageurlString=file.getName();
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

	public void startShowViewWithBitMap() {
		try {
			if (mBitmap != null) {
				String value=CacheUtils.getString(mContext, "Desktop");
				if(value!=null&&!value.equals("")){
					Desktop_Entries item = (Desktop_Entries) JsonUtils
							.jsonToBean(value, Desktop_Entries.class);
					mDesktopQRcodeView.setImageView(mBitmap,item);
					localimagePointString=value;
				}else{
					mDesktopQRcodeView.setImageView(mBitmap,null);
				}			
				LogUtils.w(TAG, "desktop mShowWindowObserver.isShowWindow():"+mShowWindowObserver.isShowWindow());
				if (mShowWindowObserver.isShowWindow()) {
					mDesktopQRcodeView.startView();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateWindow(boolean show) {
		if (show) {
			startShowView();
		} else {
			hideView();
		}

	}

	@Override
	public void setMessageListResponseData(String value) {
		// TODO Auto-generated method stub

	}
	
	public void setResponseData(List<DesktopEntrancesItem.Data.Desktop_Entries> mDesktopEntriesList) {
		try {
			if (mDesktopEntriesList!=null&&!mDesktopEntriesList.isEmpty()) {
				for(final DesktopEntrancesItem.Data.Desktop_Entries desktop_entries:mDesktopEntriesList){
					final String imageurl = desktop_entries.getIcon_url();
					LogUtils.w(TAG, "desktop imageurl :" + imageurl);
					if (imageurl != null && !imageurl.isEmpty()) {
						if (localimageurlString == null|| mBitmap == null) {
							if(localimageurlString!=null&&!localimageurlString.equals(imageurl.substring(imageurl.length()-14))){//如果本地二维码和网络二维码不一致就删除本地二维码
								File desktopfile = new File(Environment.getExternalStorageDirectory().getPath()+ "/messageservice/desktop/"+localimageurlString);
								try {
							        if (desktopfile!=null&&desktopfile.isFile() && desktopfile.exists()) {
							        	desktopfile.delete();
							        }
						    	}catch (Exception e1) {    
						         	e1.printStackTrace();   
						        }
							}
							new Thread(new Runnable() {
						           @Override
						           public void run() {
										try {
											issuccess = true;
											final Bitmap bitmap=myBitmapUtils.disPlay(imageurl,MyBitmapUtils.TYPE_DESKTOP);
											if(bitmap!=null){
												CacheUtils.putString(mContext, "Desktop",JsonUtils.beanToJson(desktop_entries));
												mHandler.post(new Runnable() {
													@Override
													public void run() {												
														mDesktopQRcodeView.setImageView(bitmap,desktop_entries);
														if (mShowWindowObserver.isShowWindow()) {
															mDesktopQRcodeView.startView();
														}
														showWindow = true;
													}
												});
											}else{
												hideView();
												showWindow = false;
											}
										}catch(Exception e){
											e.printStackTrace();
											issuccess = true;
											hideView();
											showWindow = false;
										}
						           }
					         }).start();
						}else if(!localimageurlString.equals(imageurl.substring(imageurl.length()-14))){
							File desktopfile = new File(Environment.getExternalStorageDirectory().getPath()+ "/messageservice/desktop/"+localimageurlString);
							try {
						        if (desktopfile!=null&&desktopfile.isFile() && desktopfile.exists()) {
						        	desktopfile.delete();
						        }
					    	}catch (Exception e1) {    
					         	e1.printStackTrace();   
					        }
							new Thread(new Runnable() {
						           @Override
						           public void run() {
										try {
											issuccess = true;
											final Bitmap bitmap=myBitmapUtils.disPlay(imageurl,MyBitmapUtils.TYPE_DESKTOP);
											if(bitmap!=null){
												CacheUtils.putString(mContext, "Desktop",JsonUtils.beanToJson(desktop_entries));
												mHandler.post(new Runnable() {
													@Override
													public void run() {
														mDesktopQRcodeView.setImageView(bitmap,desktop_entries);
														if (mShowWindowObserver.isShowWindow()) {
															mDesktopQRcodeView.startView();
														}
														showWindow = true;
													}
												});
											}else{
												hideView();
												showWindow = false;
											}
										}catch(Exception e){
											e.printStackTrace();
											issuccess = true;
											hideView();
											showWindow = false;
										}
						           }
					         }).start();					
						}else{
							CacheUtils.putString(mContext, "Desktop",JsonUtils.beanToJson(desktop_entries));
							mDesktopQRcodeView.setImageDesktopEntries(desktop_entries);
							issuccess = true;
							showWindow = true;
							startShowView();
						}
					}else{
						hideView();
						issuccess = true;
						showWindow = false;
					}
					break;
				}
			} else {
				// startTimer(60000);等于空说明下架了不去请求了
				hideView();
				issuccess = true;
				showWindow = false;
			}
		} catch (Exception e) {
			// startTimer(60000);
			hideView();
			issuccess = true;
			showWindow = false;
		}
	}
}
