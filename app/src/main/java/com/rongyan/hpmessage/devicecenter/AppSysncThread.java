package com.rongyan.hpmessage.devicecenter;


import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.item.AppItem;

import android.content.Context;
import android.util.Log;

public class AppSysncThread extends Thread {
	private String mPkgname;
	private DataBaseOpenHelper mDataBaseOpenHelper;

	public AppSysncThread(Context context, String pkgname) {
		mPkgname = pkgname;
		mDataBaseOpenHelper = DataBaseOpenHelper.getInstance(context);
	}

	@Override
	public void run() {
		synchronized (this) {
			AppInfoOprea appInfoUtils = AppInfoOprea.getIntance();
			AppItem item = appInfoUtils.isInInstalledApp(mPkgname);

			if (item != null) {
				item.setUseRate(item.getUseRate() + 1);
				synchronized (this) {
					if (mDataBaseOpenHelper.haveAppInfo(mPkgname)) {
						mDataBaseOpenHelper.UpdateAppInfoString(item);
					} else {
						mDataBaseOpenHelper.SaveUserInfo(item);
					}
				}
			}
		}
	}
}
