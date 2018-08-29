package com.rongyan.hpmessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.rongyan.hpmessage.apks.ApksTask;
import com.rongyan.hpmessage.apks.AppStoreTask;
import com.rongyan.hpmessage.bootads.BootAdsTask;
import com.rongyan.hpmessage.bootconfig.BootConfigTask;
import com.rongyan.hpmessage.cmns.MessageReceiptOprea;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.database.DatabaseColume;
import com.rongyan.hpmessage.desktop.DesktopQSTask;
import com.rongyan.hpmessage.devicecenter.AppInfoOprea;
import com.rongyan.hpmessage.devicecenter.AppSysncThread;
import com.rongyan.hpmessage.devicecenter.ApplicationState;
import com.rongyan.hpmessage.devicecenter.DeviceCfg;
import com.rongyan.hpmessage.devicecenter.OnlineOpera;
import com.rongyan.hpmessage.devicecenter.SettingsTask;
import com.rongyan.hpmessage.devicecenter.StartupTask;
import com.rongyan.hpmessage.devicecenter.UsbOpera;
import com.rongyan.hpmessage.messagelist.MessageListTask;
import com.rongyan.hpmessage.util.AliyunSDKUtils;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.yunos.push.PushClient;
import com.yunos.push.api.listener.PushGetDeviceTokenListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

public class MessageService extends Service {

	private final static String TAG="MessageService";
	private final String KEY = "pkgname";
	private static final String ACTION_NEW_APPLICATION = "action.start.new.application";
	private static final String ACTION_INSTALL_RETURNCODE ="package.install.returncode";
	private DesktopQSTask mDesktopQSTask;
	private BootAdsTask mBootAdsTask;
	private MessageListTask mMessageListTask;
	private ApksTask mApksTask;
	private AppStoreTask mAppStoreTask;
	private ConnectivityManager mConManager;
	private Handler mHandler = new Handler();
	private ShowWindowObserver mShowWindowObserver;
	private DataBaseObserver mDataBaseObserver;
	private MessageReceiptOprea mMessageReceiptOprea;
	private DeviceCfg mDeviceCfg;
	private UsbOpera mUsbOpera;
	private SettingsTask mSettingsTask=null;
	private StartupTask mStartupTask=null;
	private BootConfigTask mBootConfigTask=null;
	private ApplicationState mApplicationState;
	private ApplicationUtils mApplicationUtils;
	private DataBaseOpenHelper mDataBaseOpenHelper;
	static boolean doUpdateDatabase = false;
	private final boolean isUseDesktopApp = true;// 控制是否显示二维码图片
	
	private OnlineOpera mOnlineOpera;

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			try{
				if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {				
					if (mConManager == null) {
						mConManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					}
					NetworkInfo wifiInfo = mConManager
							.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					NetworkInfo ethInfo = mConManager
							.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
					if(wifiInfo!=null&&ethInfo!=null){
						if (wifiInfo.isConnected() || ethInfo.isConnected()) {
							ApplicationUtils.setmNetWorkEnable(true);
							if (wifiInfo.isConnected()) {
								mDeviceCfg.setEthernetEable(0);
							} else {
								mDeviceCfg.setEthernetEable(1);
							}
							updateDoEverything();
						} else {
							ApplicationUtils.setmNetWorkEnable(false);
							mDeviceCfg.setEthernetEable(0);
						}
					}
				}else if (action.equals(ACTION_NEW_APPLICATION)) {
					new AppSysncThread(context, intent.getStringExtra(KEY)).start();
				}else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
					mUsbOpera.getUsbInfo(0);  
	            }else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
	            	mUsbOpera.getUsbInfo(0);  
	            }else if(ACTION_INSTALL_RETURNCODE.equals(action)) {
	            	LogUtils.w(TAG,intent.getExtras().getString("name")+":"+intent.getExtras().getInt("code"));
	            	if("com.rongyan.hpmessage".equals(intent.getExtras().getString("name"))) {
	            		AliyunSDKUtils.getInstance(context).putLogTst("[INSTALL_RESULT]message:"+intent.getExtras().getInt("code"),2);
	            		if(intent.getExtras().getInt("code")!= 1){
	            			deletefile(Environment.getExternalStorageDirectory().getPath() + "/messageservice/download_apk");
	            		}
	            	}else if("com.rongyan.appstore".equals(intent.getExtras().getString("name"))){
	            		AliyunSDKUtils.getInstance(context).putLogTst("[INSTALL_RESULT]appstore:"+intent.getExtras().getInt("code"),2);
	            		if(intent.getExtras().getInt("code")!= 1){
	            			deletefile(Environment.getExternalStorageDirectory().getPath() + "/messageservice/download_apk");
	            		}
	            	}
	            }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	private void getDeviceToken() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				PushClient.getInstance().getDeviceToken(
						new PushGetDeviceTokenListener() {
							@Override
							public void onGetDeviceToken(final int errorCode,
									final String token) {
								LogUtils.w(TAG, "errorCode:"
										+ errorCode);
								if (errorCode == 0) {
									ApplicationUtils.mDeviceToken = token;
									LogUtils.w(TAG, "mDeviceToken:"
											+ ApplicationUtils.mDeviceToken);
								}
							}
						});
			}
		}, 100);
	}

	public void updateDoEverything() {
		{
			if (ApplicationUtils.mDeviceToken == null) {
				getDeviceToken();// 获取devicetoken
			}
		}
		{
			// 获取激活时间 第一次开机才会写入
			if (mApplicationUtils == null) {
				mApplicationUtils = ApplicationUtils.getIntance(this);
			}
			if (mApplicationUtils.getPerferencesStringValue(
					ApplicationUtils.ACTIVITIES_TIME_STRING).equals("")) {
				long time = getSystemTime();
				mApplicationUtils.writePerferencesValue(
						ApplicationUtils.ACTIVITIES_TIME_STRING,
						String.valueOf(time));
			}
		}
		{
			if (mBootAdsTask == null) {
				mBootAdsTask = BootAdsTask.getInstance(this, getBootAdURL(),
						mShowWindowObserver);
				if (mShowWindowObserver != null) {
					mShowWindowObserver.addCallBack(mBootAdsTask);
				}
			}
		}
		if (isUseDesktopApp) {
			// 更新桌面二维码
			if (mDesktopQSTask == null) {
				mDesktopQSTask = DesktopQSTask.getIntance(this,
						getDesktopQSURL(), mShowWindowObserver);
				if (mShowWindowObserver != null) {
					mShowWindowObserver.addCallBack(mDesktopQSTask);
				}
			}
		}
		{
			// 更新桌面消息列表
			if (mMessageListTask == null) {
				mMessageListTask = MessageListTask.getInstance(this,
						getMessageListURL(), mShowWindowObserver);
				if (mShowWindowObserver != null) {
					mShowWindowObserver.addCallBack(mMessageListTask);
				}				
			}
			mBootAdsTask.setMessageListTask(mMessageListTask);
			if (!mMessageListTask.isSysncDatabase) {
				mMessageListTask.getMessgeListFromDatabase();
			}
		}
		if(mSettingsTask==null){
			mSettingsTask=SettingsTask.getIntance(mOnlineOpera,getApplicationContext());
			if(!mSettingsTask.isSuccess){
				mSettingsTask.startTimer();
			}
		}
		
		if(mStartupTask==null){
			mStartupTask=StartupTask.getIntance(getApplicationContext());
			if(!mStartupTask.isSuccess){
				mStartupTask.start();
			}
		}
		if(mBootConfigTask==null){
			if (mApksTask == null) {
				mApksTask = ApksTask.getInstance(this,
						MessageApplication.HTTP_APKS_LATEST_STRING, null);
			}
			if (mAppStoreTask == null) {
				mAppStoreTask = AppStoreTask.getInstance(this,
						MessageApplication.HTTP_APPSTORE_LATEST_STRING, null);
			}
			mBootConfigTask=BootConfigTask.getIntance(mBootAdsTask,mDesktopQSTask,mApksTask,mAppStoreTask,mDataBaseOpenHelper,getApplicationContext());
			if (mMessageReceiptOprea != null) {
				mMessageReceiptOprea.setmBootConfigTask(mBootConfigTask);
			}
		}
		if(!mBootConfigTask.isSuccess){
			mBootConfigTask.startTimer(5000);
		}
	}

	private long getSystemTime() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		long unixTimestamp = curDate.getTime() / 1000;
		return unixTimestamp;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDesktopQSURL() {
		return MessageApplication.HTTP_DESKTOP_ENTRANCES_STRING;
	}

	private String getBootAdURL() {
		return MessageApplication.HTTP_BOOTADS_URL;
	}

	private String getMessageListURL() {
		return MessageApplication.HTTP_NOTICATIONS_URL;
	}

	@SuppressWarnings("unused")
	public void intAll() {
		try{
			if (!doUpdateDatabase) {
				deleteDataBase();// 清空90天之前的消息
			}
			mDataBaseOpenHelper.DeleteAD(DatabaseColume.MESSAGE_AD);
			deleteFile();
		}catch(Exception e){
			e.printStackTrace();
		}
		mApplicationUtils = ApplicationUtils.getIntance(this);
		if (isUseDesktopApp) {
			mDesktopQSTask = DesktopQSTask.getIntance(this, getDesktopQSURL(),
					mShowWindowObserver);
			if (mShowWindowObserver != null) {
				mShowWindowObserver.addCallBack(mDesktopQSTask);
			}
		}

		mBootAdsTask = BootAdsTask.getInstance(this, getBootAdURL(),
				mShowWindowObserver);
		if (mShowWindowObserver != null) {
			mShowWindowObserver.addCallBack(mBootAdsTask);
		}

		mMessageListTask = MessageListTask.getInstance(this,
				getMessageListURL(), mShowWindowObserver);
		mDataBaseObserver.observer(this, mMessageListTask);
		if (mShowWindowObserver != null) {
			mShowWindowObserver.addCallBack(mMessageListTask);
		}
		mMessageListTask.getMessgeListFromDatabase();

		mApksTask = ApksTask.getInstance(this,
				MessageApplication.HTTP_APKS_LATEST_STRING, null);
		mAppStoreTask = AppStoreTask.getInstance(this,
				MessageApplication.HTTP_APPSTORE_LATEST_STRING, null);

		if (isUseDesktopApp && mDesktopQSTask.isBitmapExitsAndUse())
			mDesktopQSTask.startShowViewWithBitMap();
		mDeviceCfg = new DeviceCfg(getApplication());
		mDeviceCfg.init();
		
		// 获取全部安装的程序 并且与数据库同步
		AppInfoOprea appInfoUtils = AppInfoOprea.getIntance();
		appInfoUtils.init(getApplication(), mDataBaseOpenHelper,mAppStoreTask);
		mOnlineOpera = OnlineOpera.getInstance(this, mDeviceCfg, getApplication());
		
		{
			mApplicationState = ApplicationState.getInstance(getApplication());
			mApplicationState.doResumed();
		}
		{
			mUsbOpera = UsbOpera.getInstance(this, mDeviceCfg, getApplication());
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.w(TAG, "onCreate");
		mDataBaseOpenHelper = DataBaseOpenHelper.getInstance(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 监听网络变化
		filter.addAction(ACTION_NEW_APPLICATION);
		filter.addAction(ACTION_INSTALL_RETURNCODE);//监听安装结果
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);//监听USB接口变化
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);//监听USB接口变化
		mShowWindowObserver = new ShowWindowObserver(mHandler);
		mDataBaseObserver=new DataBaseObserver(mHandler);
		mShowWindowObserver.observer(this);
		mMessageReceiptOprea = MessageReceiptOprea.getInstance(this);
		mMessageReceiptOprea.setDataBaseHelper(mDataBaseOpenHelper);
		registerReceiver(mBroadcastReceiver, filter);
		intAll();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		mShowWindowObserver.unObserver();
		mDataBaseObserver.unObserver();
		if (mDesktopQSTask != null) {
			mDesktopQSTask.mDesktopQRcodeView.release();
			mDesktopQSTask.release();
		}
		if (mBootAdsTask != null) {
			mBootAdsTask.nBootAdsView.release();
			mBootAdsTask.release();
		}
		if (mApplicationState != null) {
			mApplicationState.doPaused();
		}
		super.onDestroy();
		Intent intent = new Intent(this, MessageService.class);
		startService(intent);
	}

	// 开机清空90天前数据
	public void deleteDataBase() {
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, -90);// 把日期往前减少一天，若想把日期向后推一天则将负数改为正数
		date = calendar.getTime();
		long beforetme = calendar.getTime().getTime();
		mDataBaseOpenHelper.Delete(String.valueOf(beforetme));// 删除90天之前的消息
		doUpdateDatabase = true;
	}	
	
	// 开机清空原先保存在messageservice文件下图片
	public void deleteFile() {
    	File desktopfile = new File(Environment.getExternalStorageDirectory().getPath()+ "/messageservice/desktop.png");
    	File adfile = new File(Environment.getExternalStorageDirectory().getPath()+ "/messageservice/bootads.png");
		try {
	        if (desktopfile!=null&&desktopfile.isFile() && desktopfile.exists()) {
	        	desktopfile.delete();
	        }
	        if (adfile!=null&&adfile.isFile() && adfile.exists()) {
	        	adfile.delete();
	        }
	        deletefile(Environment.getExternalStorageDirectory().getPath() + "/messageservice/download_apk/hpmessage.apk");
    	}catch (Exception e1) {    
         	e1.printStackTrace();   
        }
	}
	
	/**
     * 删除某个文件夹下的所有文件夹和文件
     */
    public boolean deletefile(String delpath) throws Exception {
        try {
            File file = new File(delpath);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + "//" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                        LogUtils.w(TAG, delfile.getAbsolutePath() + "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deletefile(delpath + "//" + filelist[i]);
                    }
                }
                LogUtils.w(TAG, file.getAbsolutePath() + "删除成功");
                file.delete();
            }

        } catch (FileNotFoundException e) {
           e.printStackTrace();
        }
        return true;
    }

}
