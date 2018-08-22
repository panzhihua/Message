package com.rongyan.hpmessage.devicecenter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.rongyan.hpmessage.util.FormatUtil;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Debug;

@SuppressLint("NewApi") 
public class ApplicationState {
	private Context mContext;
	final PackageManager mPm;
	ConcurrentHashMap<String, PackageInfo> mPackageInfos = new ConcurrentHashMap<String, PackageInfo>();
	List<Session> mSessions = new ArrayList<Session>();
	final int mRetrieveFlags;
	boolean mResumed = false;
	PackageIntentReceiver packageIntentReceiver = null;
    static Object sLock = new Object();
    static ApplicationState sInstance;
    ConcurrentHashMap<String, Integer> runningProcessMap = new  ConcurrentHashMap<String, Integer>();
    Timer mTimer;
    private SystemInformation mSystemInformation;
    private boolean mReceiverTag = false;   //广播接受者标识
    public static interface Callbacks {
        public void onPackageListChanged();
        public void onAddPackage(String pkgname,PackageInfo info);
        public void onRemovePackage(String pkgname);
    }
    public static ApplicationState getInstance(Application app) {

		synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ApplicationState(app);
            }
            return sInstance;
        }
    }
    public Session newSession(Callbacks callbacks) {
    	Session s = new Session(callbacks);
        synchronized (mPackageInfos) {
        	mSessions.add(s);
        }
        return s;
    }
    
    
    public class Session{
    	Callbacks callbacks;
    	public Session(Callbacks callback){
    		callbacks = callback;
    	}
    	public Map<String, PackageInfo> getPackageInfos(){
    		synchronized (mPackageInfos) {
    		    return mPackageInfos;
    		}
    	}
    }

	public ApplicationState(Application context) {
		mContext = context;
		mPm = mContext.getPackageManager();
		mRetrieveFlags = PackageManager.GET_UNINSTALLED_PACKAGES
				| PackageManager.GET_DISABLED_COMPONENTS
				| PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS;
		mSystemInformation = SystemInformation.getInstance(mContext);
	}


	public void doResumed() {
		try{
			if (packageIntentReceiver == null) {
				packageIntentReceiver = new PackageIntentReceiver();
				packageIntentReceiver.registerReceiver();
			}
			mPackageInfos.clear();
			List<PackageInfo> applications = mPm.getInstalledPackages(0);
			for (PackageInfo info : applications) {
				ApplicationInfo appInfo = info.applicationInfo;
				if (DeviceCfg.filterApp(appInfo)) {
					mPackageInfos.put(info.packageName, info);
				}
			}
			for(Session session:mSessions){
				session.callbacks.onPackageListChanged();
			}
		    if(mTimer != null){
		    	mTimer.cancel();
		    	mTimer = null;
		    }
		    mTimer = new Timer();
		    mTimer.schedule(new MyTask(), 1,10000);
		}catch(Exception e){
	    	e.printStackTrace();
	    }
	}
	
	public void doPaused(){
		if(packageIntentReceiver != null){
			packageIntentReceiver.unregisterReceiver();
		}
	    if(mTimer != null){
	    	mTimer.cancel();
	    	mTimer = null;
	    }
	}

	/**
	 * Receives notifications when applications are added/removed.
	 */
	private class PackageIntentReceiver extends BroadcastReceiver {
		void registerReceiver() {
			if (!mReceiverTag){     //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
				IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
				filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
				filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
				filter.addDataScheme("package");
				mContext.registerReceiver(this, filter);
				// Register for events related to sdcard installation.
				IntentFilter sdFilter = new IntentFilter();
				sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
				sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
				mContext.registerReceiver(this, sdFilter);
				mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
			}
		}

		void unregisterReceiver() {
			if (mReceiverTag) {   //判断广播是否注册
                mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
                mContext.unregisterReceiver(this);
			}
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String actionStr = intent.getAction();
			if (Intent.ACTION_PACKAGE_ADDED.equals(actionStr)) {
				Uri data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				addPackage(pkgName);
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(actionStr)) {
				Uri data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				removePackage(pkgName);
			} else if (Intent.ACTION_PACKAGE_CHANGED.equals(actionStr)) {
				Uri data = intent.getData();
				String pkgName = data.getEncodedSchemeSpecificPart();
				invalidatePackage(pkgName);
			} else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE
					.equals(actionStr)
					|| Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE
							.equals(actionStr)) {
				String pkgList[] = intent
						.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
				if (pkgList == null || pkgList.length == 0) {
					// Ignore
					return;
				}
				boolean avail = Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE
						.equals(actionStr);
				if (avail) {
					for (String pkgName : pkgList) {
						invalidatePackage(pkgName);
					}
					
				}
			}

		}
	}

	void addPackage(String pkgName) {
		synchronized (mPackageInfos) {
			try {
				PackageInfo info = mPm.getPackageInfo(pkgName, mRetrieveFlags);
				if (DeviceCfg.filterApp(info.applicationInfo)) {			
					if (!info.applicationInfo.enabled) {
						return;
					}
					//mPackageInfos.put(pkgName, info);
					for(Session session:mSessions){
						session.callbacks.onAddPackage(pkgName,info);
					}
				    if(mTimer != null){
				    	mTimer.cancel();
				    	mTimer = null;
				    }
				    mTimer = new Timer();
				    mTimer.schedule(new MyTask(), 1,10000);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void removePackage(String pkgname) {
		synchronized (mPackageInfos) {
			PackageInfo info = mPackageInfos.get(pkgname);
			if (info != null) {
				mPackageInfos.remove(info);
				for(Session session:mSessions){
					session.callbacks.onRemovePackage(pkgname);
				}
			}
		    if(mTimer != null){
		    	mTimer.cancel();
		    	mTimer = null;
		    }
		    mTimer = new Timer();
		    mTimer.schedule(new MyTask(), 1,10000);
		}
	}

	void invalidatePackage(String pkgName) {
		removePackage(pkgName);
		addPackage(pkgName);
	}
	void getRunningAppProcessInfo(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		runningProcessMap.clear();
		// 获得系统里正在运行的所有进程
		List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager
				.getRunningAppProcesses();
		synchronized (mPackageInfos) {
			if(runningAppProcessesList!=null&&!runningAppProcessesList.isEmpty()){
				for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
					// 进程ID号
					int pid = runningAppProcessInfo.pid;
					// 用户ID
					int uid = runningAppProcessInfo.uid;
					// 进程名
					String processName = runningAppProcessInfo.processName;
					// 占用的内存
					int[] pids = new int[] { pid };
					Debug.MemoryInfo[] memoryInfo = mActivityManager
							.getProcessMemoryInfo(pids);
					if(memoryInfo==null){
						continue;
					}
					int memorySize = memoryInfo[0].getTotalPss();
					runningProcessMap.put(processName, pid);
					for (Map.Entry<String, PackageInfo> info : mPackageInfos
							.entrySet()) {
						if (info.getValue().applicationInfo.processName
								.equals(processName)) {
							mSystemInformation.putAppMemoryValue(processName,
									FormatUtil.FormatIntSize(FormatUtil
											.formatFileSize(mContext,
													memorySize * 1024, "MB")));
							break;
						}
					}
				}
			}
			mSystemInformation.updateAppsUsedMemoryPercentage();
		}
	}
	
	class MyTask extends TimerTask{
		@Override
		public void run() {
			try{
				getRunningAppProcessInfo(mContext);
				mSystemInformation.updateSystemUsedCpu();
				synchronized (mPackageInfos) {
			    	for(Map.Entry<String, PackageInfo> info:mPackageInfos.entrySet()){
			    		for(Map.Entry<String, Integer> running:runningProcessMap.entrySet()){
			    			if(info.getValue().applicationInfo.processName.equals(running.getKey())){
			    				mSystemInformation.updateAppCpuTime(running.getKey(), running.getValue());
			    			}
			    		}
			    		try {
							SystemInformation.getPkgSize(mContext,info.getKey());
						} catch (NoSuchMethodException | InvocationTargetException
								| IllegalAccessException e) {
							e.printStackTrace();
						}
			    	}
			    	mSystemInformation.updateAppsPkgSizePercentage();	
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
}
