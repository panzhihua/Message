package com.rongyan.hpmessage.devicecenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.apks.AppStoreTask;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.devicecenter.ApplicationState.Callbacks;
import com.rongyan.hpmessage.devicecenter.ApplicationState.Session;
import com.rongyan.hpmessage.item.AppItem;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

public class AppInfoOprea implements Callbacks {
	private Application mContext;
	private List<AppItem> appList = null;
	private Map<String, PackageInfo> PackageInfos=new HashMap<>();
	private DataBaseOpenHelper mDatabaseHelper;
	private static AppInfoOprea mAppInfoOprea;
	private Session mSession;
	ApplicationState mState;
	private AppStoreTask mAppStoreTask;
	private boolean isFirst=true;

	public static AppInfoOprea getIntance() {
		if (mAppInfoOprea == null) {
			mAppInfoOprea = new AppInfoOprea();
		}
		return mAppInfoOprea;
	}

	public AppInfoOprea() {
		
	}

	public void init(Application context, DataBaseOpenHelper databaseHelper,AppStoreTask appStoreTask) {
		mContext = context;
		mDatabaseHelper = databaseHelper;
		mState = ApplicationState.getInstance(mContext);
		mSession = mState.newSession(this);
		mAppStoreTask=appStoreTask;
	}

	public List<AppItem> getInstallAppList(boolean refresh) {
		if (appList == null || refresh) {
			boolean databaserefresh = false;
			appList = new ArrayList<AppItem>();
			if (mDatabaseHelper.getAppInfoList().size() <= 0) {
				databaserefresh = true;
			}
			if(PackageInfos!=null){
				for (Map.Entry<String, PackageInfo> entry : PackageInfos.entrySet()) {
					ApplicationInfo appInfo = entry.getValue().applicationInfo;
					if(DeviceCfg.filterApp(appInfo)){
						AppItem item = new AppItem();
						item.setPackage_(entry.getValue().packageName);
						item.setVersion(entry.getValue().versionName);
						item.setVersionCode(entry.getValue().versionCode);
						item.setUseRate(mDatabaseHelper.getAppInfoUsedTime(entry
								.getValue().packageName));
						appList.add(item);
					}
				}
			}
			if (databaserefresh) {// 如果数据库内容为空 add数据
				mDatabaseHelper.add(appList);
			}else{// 如果数据库内容不为空 update数据
				if(appList!=null&&!appList.isEmpty()){
					for(int i=0;i<appList.size();i++){
						mDatabaseHelper.UpdateAppInfoString(appList.get(i));
					}
				}
			}
			if(isFirst){
				isFirst=false;
				if(appList!=null&&!appList.isEmpty()){
					for(int i=0;i<appList.size();i++){
						if(appList.get(i).getPackage_().equals("com.rongyan.appstore")){
							mAppStoreTask.setVersion(appList.get(i).getVersion());
							mAppStoreTask.startTimer(0);
							break;
						}
					}
				}
			}
		}
		return appList;
	}

	public AppItem isInInstalledApp(String pkgname) {
		appList = getInstallAppList(false);
		for (AppItem item : appList) {
			if (item.getPackage_().equals(pkgname))
				return item;
		}
		return null;
	}

	@Override
	public void onRemovePackage(String pkgname) {
		if (PackageInfos.get(pkgname) != null) {
			PackageInfos.remove(pkgname);
			mDatabaseHelper.DeleteUserInfo(pkgname);
		}
	}

	@Override
	public void onAddPackage(String pkgname, PackageInfo info) {
		if (PackageInfos.get(pkgname) == null) {
			PackageInfos.put(pkgname, info);
			if (!mDatabaseHelper.haveAppInfo(pkgname)) {
				AppItem item = new AppItem();
				item.setPackage_(pkgname);
				item.setVersion(info.versionName);
				item.setVersionCode(info.versionCode);
				item.setUseRate(mDatabaseHelper.getAppInfoUsedTime(pkgname));
				mDatabaseHelper.SaveUserInfo(item);
			}
			getInstallAppList(true);
		}
	}

	@Override
	public void onPackageListChanged() {
		PackageInfos = mSession.getPackageInfos();
		getInstallAppList(true);
	}
}
