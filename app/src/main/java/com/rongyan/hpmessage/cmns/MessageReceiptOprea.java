package com.rongyan.hpmessage.cmns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.MessageService;
import com.rongyan.hpmessage.apks.ApksTask;
import com.rongyan.hpmessage.apks.AppStoreTask;
import com.rongyan.hpmessage.bootads.BootAdsTask;
import com.rongyan.hpmessage.bootconfig.BootConfigTask;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.database.DatabaseColume;
import com.rongyan.hpmessage.desktop.DesktopQSTask;
import com.rongyan.hpmessage.item.AppItem;
import com.rongyan.hpmessage.item.MessagePushItem;
import com.rongyan.hpmessage.item.MessageListResponseItem.Data.Notifications;
import com.rongyan.hpmessage.item.PushClientItem;
import com.rongyan.hpmessage.util.AliyunSDKUtils;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.HttpDownAPKUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.ApksTaskUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;
/**
 * 接收到cmns消息后操作类
 * 
 * @author Administrator
 * 
 */
public class MessageReceiptOprea {
	
	private final static String TAG="MessageReceiptOprea";

	private Context mContext;
	private static List<CallBack> mList = new ArrayList<CallBack>();
	private Random random = new Random();
	private static MessageReceiptOprea mMessageReceiptOprea;
	private DataBaseOpenHelper mDataBaseOpenHelper;
	private BootConfigTask mBootConfigTask;
	private HttpDownAPKUtils mHttpDownAPKUtils;
	public interface CallBack {
		public abstract void onMessageReceice(Notifications item);
	}

	public static MessageReceiptOprea getInstance(Context context) {
		if (mMessageReceiptOprea == null) {
			mMessageReceiptOprea = new MessageReceiptOprea(context);
		}
		return mMessageReceiptOprea;
	}

	public void setDataBaseHelper(DataBaseOpenHelper base) {
		mDataBaseOpenHelper = base;
	}

	public void setmBootConfigTask(BootConfigTask mBootConfigTask) {
		this.mBootConfigTask = mBootConfigTask;
	}

	public MessageReceiptOprea(Context context) {
		mContext = context;
	}

	public void addCallBack(CallBack callBack) {
		mList.add(callBack);
	}

	public void setData(String data) {
		try {
			LogUtils.w(TAG, "data:" + data);
			AliyunSDKUtils.getInstance(mContext).putLogTst("[RECEIVE_CMNS_MSG]"+data,1);
			JSONObject jsonObject = new JSONObject(data);
			String jsonstrtemp;
			try {
				jsonstrtemp = jsonObject.getString("notification");
				LogUtils.w(TAG, "jsonstrtemp temp:" + jsonstrtemp);
				if (jsonstrtemp != null && !jsonstrtemp.isEmpty()) {
					AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_UPDATE_NOTIFICATION]",1);
					MessagePushItem item = (MessagePushItem) JsonUtils
							.jsonToBean(jsonstrtemp, MessagePushItem.class);
					// MessagePushItem item = (MessagePushItem)
					// JsonUtils.jsonToBean(data,
					// MessagePushItem.class);
					String id = String.valueOf(item.getId());
					// 如果缓存里面已经有这条消息 将不做处理
					// if (Cache.get(id) == null) {
					// Cache.add(String.valueOf(item.getId()), item);
					// }
					if (mDataBaseOpenHelper == null) {
						mDataBaseOpenHelper = DataBaseOpenHelper
								.getInstance(mContext);
					}
					if (!mDataBaseOpenHelper
							.isExist(id, DatabaseColume.MESSAGE)) {
						LogUtils.w(TAG, "id database null so add");
						Notifications notification = new Notifications();
						notification.setId(item.getId());
						notification.setPreview_icon(item.getPreview_icon());
						notification.setTitle(item.getTitle());
						notification.setSummary(item.getSummary());
						notification.setRead(false);
						notification.setPushed_at(item.getPushed_at());
						notification.setType(Integer
								.valueOf(DatabaseColume.MESSAGE));
						mDataBaseOpenHelper.Add(notification);
						mContext.sendBroadcast(MessageApplication.mIntent);// 告知系统有新的信息
						if (mList.size() > 0) {
							for (CallBack callBack : mList) {
								callBack.onMessageReceice(notification);
							}
						}
					}
					LogUtils.w(TAG, "item.isReturn_receipt()=" + item.isReturn_receipt());
					if (item.isReturn_receipt()) {						
						String url = MessageApplication.HTTP_NOTIFICATION_PUSHRECEIPT_STRING
								+ id + "/receipt";
						MessageReceiptTask task = new MessageReceiptTask(
								mContext, url, null);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("sn_no", Build.SERIAL);
						map.put("uuid", ApplicationUtils.getUUID());
						map.put("device_token", ApplicationUtils.mDeviceToken);
						map.put("fire_shop_no",
								ApplicationUtils.getentityId());
						map.put("actived_at",
								ApplicationUtils
										.getIntance(mContext)
										.getPerferencesStringValue(
												ApplicationUtils.ACTIVITIES_TIME_STRING));
						String receiptString = JsonUtils.beanToJson(map);
						LogUtils.w(TAG, "receiptString:" + receiptString);
						task.setData(receiptString, true);
						int timer = random.nextInt(10)*1000;
						task.startTimer(timer);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				jsonstrtemp = jsonObject.getString("system_command");
				LogUtils.w(TAG, "jsonstrtemp secend:" + jsonstrtemp);
				if (jsonstrtemp != null && !jsonstrtemp.isEmpty()) {
					JSONObject commondobject = new JSONObject(jsonstrtemp);
					if (commondobject != null) {
						String commdtype = commondobject.getString("type");
						if (commdtype != null && !commdtype.isEmpty()) {
							int timer = (random.nextInt(3)+1)*1000;
							if (commdtype.equals("update_app")) {
								AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_UPDATE_MESSAGE]",1);
								JSONObject apkobject = new JSONObject(commondobject.getString("apk"));
								String urlString = apkobject.getString("url");
								String versionString = apkobject.getString("version");
								if(ApplicationUtils.compareVersion(ApplicationUtils.getAppVersion(mContext), versionString)){
									if(urlString!=null&&!urlString.equals("")){
										startHttpGetConnect(urlString,1);
									}
								}
								
							} else if (commdtype.equals("update_ad")) {
								AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_UPDATE_ADS]",1);
								if (mBootConfigTask != null) {
									mBootConfigTask.startTimer(timer);
								}
							} else if (commdtype.equals("update_qr_code")) {
								AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_UPDATE_QR_CODE]",1);
								if (mBootConfigTask != null) {
									mBootConfigTask.startTimer(timer);
								}
							} else if(commdtype.equals("update_appstore")){
								AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_UPDATE_APPSTORE]",1);
								List<AppItem> appItemList=mDataBaseOpenHelper.getAppInfoList();
								if(appItemList!=null&&!appItemList.isEmpty()){
									for(AppItem appItem:appItemList){
										if(appItem.getPackage_().equals("com.rongyan.appstore")){
											JSONObject apkobject = new JSONObject(commondobject.getString("apk"));
											String urlString = apkobject.getString("url");
											String versionString = apkobject.getString("version");										
											if(ApplicationUtils.compareVersion(appItem.getVersion(), versionString)){
												if(urlString!=null&&!urlString.equals("")){
													startHttpGetConnect(urlString,2);
												}
											}
											break;
										}
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//静默安装卸载
			try {		
                PushClientItem item = (PushClientItem) JsonUtils
                        .jsonToBean(data, PushClientItem.class);
                if (item != null) {
                	AliyunSDKUtils.getInstance(mContext).putLogTst("[CMNS_UPDATE_PUSH_CLIENT]",1);
                    new ApksTaskUtils(mContext,item);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void startHttpGetConnect(String address,int type) {
		if (ApplicationUtils.ismNetWorkEnable()&&!HttpDownAPKUtils.isDown) {
			if(mHttpDownAPKUtils==null){
				mHttpDownAPKUtils=HttpDownAPKUtils.getInstance(mContext,address,type);
			}else{
				mHttpDownAPKUtils.setUrl(address, type);
			}
			ThreadPoolUtils.newFixThreadPool(mHttpDownAPKUtils);
		}
	}
}
