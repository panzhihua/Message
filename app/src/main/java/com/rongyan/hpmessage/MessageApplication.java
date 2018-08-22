package com.rongyan.hpmessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.alibaba.wireless.security.open.staticdatastore.IStaticDataStoreComponent;
import com.rongyan.hpmessage.cmns.MessageReceiptOprea;
import com.rongyan.hpmessage.item.SystemDataItem;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.yunos.push.PushClient;
import com.yunos.push.api.PushMessage;
import com.yunos.push.api.listener.PushAsyncInitListener;
import com.yunos.push.api.listener.PushConnectionListener;
import com.yunos.push.api.listener.PushMessageListener;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

public class MessageApplication extends Application {
	
	private final static String TAG = "MessageApplication";	

	private final static String HTTP_IP = "http://staging.huo365.cn/api/";
//	private final static String HTTP_IP1 = "http://staging.huo365.cn/api/";
//	private final static String HTTP_IP = "https://huo365.cn/api/";
//	private final static String HTTP_IP_DEVICE = "http://staging-rongyan-device.huo365.cn/api/";//test evverment 
	private final static String HTTP_IP_DEVICE="https://device.huo365.cn/api/";

	public final static String HTTP_BOOTADS_URL = HTTP_IP
			+ "v2/ads/cash_register";// 开机广告
	public final static String HTTP_DESKTOP_URL = HTTP_IP
			+ "v1/qr_codes/cash_register";// 桌面二维码

	public final static String HTTP_NOTICATIONS_URL = HTTP_IP
			+ "v1/notifications/";// 消息列表
	public final static String HTTP_NOTIFICATIONINOF_URL = HTTP_IP
			+ "v1/notifications/";// 消息詳情
	public final static String HTTP_BOOT_CONFIG_URL = HTTP_IP
			+ "v1/cash_register/boot_config";// 新增四合一接口
	
	public final static String HTTP_STS_TOKEN_URL = HTTP_IP
			+ "v1/sts_token?role_type=logstore_rongyan_policy";// 获取临时token接口
	
	public final static String HTTP_NOTIFICATION_PUSHRECEIPT_STRING = HTTP_IP
			+ "v1/notifications/";

	public final static String HTTP_APKS_LATEST_STRING = HTTP_IP
			+ "v1/apks/latest?apk_type=rongyan_notification";// 获取我的消息最新的 Apk
	
	public final static String HTTP_APPSTORE_LATEST_STRING = HTTP_IP
			+ "v1/apks/latest?apk_type=rongyan_appstore";// 获取应用市场最新的 Apk
	
	public final static String HTTP_STARTUP_STRING = HTTP_IP_DEVICE
			+ "v1/devicecenter/reports/startup";// 设备启动上报接口
	
	public final static String HTTP_SETTINGS_STRING = HTTP_IP_DEVICE
			+ "v1/devicecenter/settings";// 数据上报配置接口
	
	public final static String HTTP_STATE_STRING = HTTP_IP_DEVICE
			+ "v1/devicecenter/reports/state";// 数据上报配置接口
	
	public final static String HTTP_HEARTBEAT_STRING = HTTP_IP_DEVICE
			+ "v1/devicecenter/reports/heartbeat";// 设备心跳接口
	
	public final static String HTTP_APP_NO_URL = HTTP_IP_DEVICE + "v1/appstore/apps/";// 返回应用详情
	
	public final static String HTTP_INCALLBACK_URL = HTTP_IP_DEVICE + "v1/notification/app_install/callback";//云箭推送安装回调接口

	public final static String HTTP_UNCALLBACK_URL = HTTP_IP_DEVICE + "v1/notification/app_uninstall/callback";//云箭推送卸载回调接口
	
	public final static String HTTP_DESKTOP_ENTRANCES_STRING = HTTP_IP
			+ "v1/desktop_entrances";// 获取快捷方式接口

	public static Intent mIntent = new Intent("action.receive.newmessage");

	private static String mUUID = null;// 机器唯一编码
	private static String mFireShopId = "0";// 主收银店铺名称
	private static String mRetailFireShopIdString = "0";// 零售店铺名称
	private static String mOrderDeskFireShopIdString = "0";// 副收银店铺名称
	private Handler mHandler = new Handler();
	private static MessageApplication mApplication;
	
	public static MessageApplication getInstance(){
		if(mApplication == null){
			mApplication = new MessageApplication();
		}
		return mApplication;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		// // Step1: 初始化AlibabaSDK。
		// AlibabaSDK.asyncInit(this, new InitResultCallback() {
		// @Override
		// public void onSuccess() {
		// // Toast.makeText(MessageApplication.this, "初始化成功",
		// // Toast.LENGTH_SHORT).show();
		// // UTAnalytics.getInstance().turnOnDebug();
		// }
		//
		// @Override
		// public void onFailure(int code, String message) {
		// Toast.makeText(MessageApplication.this, "初始化异常",
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// // Step2: 需要单独初始化埋点抓取模块:
		// UTAnalytics.getInstance().setContext(this.getApplicationContext());
		// // 1.
		// // [必选]
		// // 设置context.
		// UTAnalytics.getInstance().setAppApplicationInstance(this); // 2. [必选]
		// // set
		// // Application
		// UTAnalytics.getInstance().setAppVersion(
		// ApplicationUtils.getAppVersion(getApplicationContext())); // 3.[必选]
		// 设置本应用的版本号
		// // UTAnalytics.getInstance().setChannel("你申请的发布渠道号,没有可以不写"); //4.[可选]
		// // 设置本发布渠道号，没有可以不写。
		// UTAnalytics.getInstance().setRequestAuthentication(
		// new UTSecuritySDKRequestAuthentication(getAppKeyByIndex(this),
		// "")); // 5.[必选]设置请求验证方式，利用阿里的安全保镖(SecurityGuard)来从安全图片获取Appkey

		// Push SDK initialize begin

		// TCAgent.LOG_ON=true;
		// // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
		// // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
		// TCAgent.init(this);
		// // 如果已经在AndroidManifest.xml配置了App
		// ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
		// TCAgent.setReportUncaughtExceptions(true);

		String processName = getProcessName(this);
		if (processName != null) {
			if (processName.equals("com.rongyan.hpmessage")) {
				PushClient.getInstance().init(this.getApplicationContext(),
						new PushAsyncInitListener() {
							@Override
							public void onInit(int errorCode) {
								LogUtils.w(TAG, "errorCode:" + errorCode);
								if (errorCode == 0) {
									PushClient.getInstance().connect(
											new PushConnectionListener() {
												@Override
												public void onConnect(
														final int errorCode) {
													LogUtils.d(TAG,
															"onConnect: "
																	+ errorCode);
												}
											});
								} else {
									LogUtils.e(TAG, "init failed for reason: "
											+ errorCode);
								}
							}

						});
				PushClient.getInstance().setMessageListener(
						new PushMessageListener() {
							@Override
							public void onNotificationClicked(Context context,
									PushMessage pushMessage) {
								// TODO Auto-generated method stub
								LogUtils.d(TAG, "消息点击事件");
								// Toast.makeText(MessageApplication.this,
								// "消息点击事件：" + pushMessage.getPayload(),
								// Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onPassThroughMessage(Context context,
									PushMessage pushMessage) {
								// Log.d("liumeng0112", "消息透传事件");
								// Toast.makeText(MessageApplication.this,
								// "透传事件：" + pushMessage.getPayload(),
								// Toast.LENGTH_SHORT)
								// .show();
								final String data = pushMessage.getPayload();
								LogUtils.w(TAG, "data:" + data);
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										LogUtils.w(TAG, "cmns data: run ");
										MessageReceiptOprea oprea = MessageReceiptOprea
												.getInstance(getApplicationContext());
										oprea.setData(data);
									}
								});
							}
						});
				// 获取MAN服务
				MANService manService = MANServiceProvider.getService();

				manService.getMANAnalytics().setChannel("RongYan");
				// 打开调试日志，线上版本建议关闭
				// manService.getMANAnalytics().turnOnDebug();
				// MAN初始化方法之一，从AndroidManifest.xml中获取appKey和appSecret初始化
				manService.getMANAnalytics()
						.init(this, getApplicationContext());
				// bugly
				CrashReport.initCrashReport(getApplicationContext());
				CrashReport.setUserId(Build.SERIAL);
			}
		}

		init();
	}
	
	public void init(){
        try {
            String[] strArray = Build.DISPLAY.split("_");
            LogUtils.w(TAG, Build.DISPLAY);
            ApplicationUtils.setmBROKER(strArray[1]);
            ApplicationUtils.setmMODEL(strArray[2]);
            ApplicationUtils.setmVERSION(strArray[3]);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

	private String getProcessName(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am
				.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
			if (proInfo.pid == android.os.Process.myPid()) {
				if (proInfo.processName != null) {
					return proInfo.processName;
				}
			}
		}
		return null;
	}

	/**
	 * 获取UUID
	 * 
	 * @return UUID 值
	 */

	public static String getUUID() {
		if (mUUID == null || mUUID.equals("0")) {
			mUUID = getProperty("ro.aliyun.clouduuid", "0");
		}
		return mUUID;
	}

	/**
	 * 获取 属性值
	 * 
	 * @param key
	 *            属性key
	 * @param defaultValue
	 *            默认值
	 * @return 如果获取不到 就返回默认值
	 */

	@SuppressWarnings({ "finally", "unused" })
	private static String getProperty(final String key,
			final String defaultValue) {
		String value = defaultValue;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			value = (String) (get.invoke(c, key, defaultValue));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return value;
		}
	}

	/**
	 * 获取app key
	 * 
	 * @param cxt
	 * @return
	 */

	public static String getAppKeyByIndex(Context cxt) {
		try {
			SecurityGuardManager sgMgr = SecurityGuardManager.getInstance(cxt);
			if (sgMgr != null) {
				IStaticDataStoreComponent sdsComp = sgMgr
						.getStaticDataStoreComp();
				if (sdsComp != null) {
					String appKey = sdsComp.getAppKeyByIndex(0, null);
					LogUtils.d("TAG", "getAppKeyByIndex:" + appKey);
					return appKey;
				}
			}
		} catch (SecException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取收银版本二维火店铺ID
	 * 
	 * @return
	 */
	public static String getentityId() {
		try {
			if (mFireShopId == null || mFireShopId.equals("0")) {
				String content = ""; // 文件内容字符串
				File file = new File("/mnt/sdcard/zmcash/system_data.txt");
				if (file.exists()) {
					try {
						InputStream instream = new FileInputStream(file);
						if (instream != null) {
							InputStreamReader inputreader = new InputStreamReader(
									instream);
							BufferedReader buffreader = new BufferedReader(
									inputreader);
							String line;
							// 分行读取
							while ((line = buffreader.readLine()) != null) {
								content += line;
							}
							instream.close();
							if (!content.equals("")) {
								SystemDataItem item = (SystemDataItem) JsonUtils
										.jsonToBean(content, SystemDataItem.class);
								mFireShopId = item.getEntity_id();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// 如果取不到二维火店铺ENTRYID 则需要取副收银端的ENTRYID
			if (mFireShopId == null || mFireShopId.equals("0")) {
				mFireShopId = getOrderDeskHEntityId();
			}
			// 如果取不到副收银店铺ENTRYID 则需要取零售端的ENTRYID
			if (mFireShopId == null || mFireShopId.equals("0")) {
				mFireShopId = getRetailEntityId();
			}
			if (mFireShopId == null) {
				mFireShopId = "0";
			}
			LogUtils.w(TAG, "mFireShopId=" + mFireShopId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mFireShopId;
	}

	/**
	 * 获取零售版本二维火店铺ID
	 * 
	 * @return
	 */

	public static String getRetailEntityId() {
		if (mRetailFireShopIdString == null
				|| mRetailFireShopIdString.equals("0")) {
			String content = ""; // 文件内容字符串
			File file = new File(
					"/mnt/sdcard/2dfire_retail_cashdesk/system_data.txt");
			if (file.exists()) {
				try {
					InputStream instream = new FileInputStream(file);
					if (instream != null) {
						InputStreamReader inputreader = new InputStreamReader(
								instream);
						BufferedReader buffreader = new BufferedReader(
								inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							content += line;
						}
						instream.close();
						if (!content.equals("")) {
							SystemDataItem item = (SystemDataItem) JsonUtils
									.jsonToBean(content, SystemDataItem.class);
							mRetailFireShopIdString = item.getEntity_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LogUtils.w(TAG, "mRetailFireShopIdString=" + mRetailFireShopIdString);
		return mRetailFireShopIdString;
	}
	
	/**
	 * 获取二维火副收银店铺ID
	 */
	public static String getOrderDeskHEntityId() {
		if (mOrderDeskFireShopIdString == null
				|| mOrderDeskFireShopIdString.equals("0")) {
			String content = ""; // 文件内容字符串
			File file = new File(
					"/mnt/sdcard/zm_orderDeskH/system_data.txt");
			if (file.exists()) {
				try {
					InputStream instream = new FileInputStream(file);
					if (instream != null) {
						InputStreamReader inputreader = new InputStreamReader(
								instream);
						BufferedReader buffreader = new BufferedReader(
								inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							content += line;
						}
						instream.close();
						if (!content.equals("")) {
							SystemDataItem item = (SystemDataItem) JsonUtils
									.jsonToBean(content, SystemDataItem.class);
							mOrderDeskFireShopIdString = item.getEntity_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LogUtils.w(TAG, "mOrderDeskFireShopIdString=" + mOrderDeskFireShopIdString);
		return mOrderDeskFireShopIdString;
	}
	
	 /** 获取App版本号 **/
    public static String getAppVersion(Context context){
        String localVersion = "";
        try {
            localVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
