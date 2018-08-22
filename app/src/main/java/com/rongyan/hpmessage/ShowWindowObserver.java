package com.rongyan.hpmessage;

import java.util.ArrayList;
import java.util.List;

import com.rongyan.hpmessage.util.LogUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

public class ShowWindowObserver extends ContentObserver {
	private final String TAG = "ShowWindowObserver";
	private Handler mHandler;
	private Context mContext;
	List<CallBack> mCallBacks = new ArrayList<CallBack>();

	public abstract interface CallBack {
		public abstract void updateWindow(boolean show);
	}

	public void addCallBack(CallBack callback) {
		mCallBacks.add(callback);
	}

	public ShowWindowObserver(Handler handler) {
		super(handler);
		mHandler = handler;
		// TODO Auto-generated constructor stub
	}

	public void observer(Context context) {
		try{
			mContext = context;
			ContentResolver resolver = mContext.getContentResolver();
			resolver.registerContentObserver(
					Settings.System.getUriFor("showwindow"), false, this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void unObserver() {
		mContext.getContentResolver().unregisterContentObserver(this);
	}

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		final int show = Settings.System.getInt(mContext.getContentResolver(),
				"showwindow", 0);
		LogUtils.w(TAG, "onChange="+show);
		if (mCallBacks.size() > 0) {
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
					for (CallBack callBack : mCallBacks) {
						callBack.updateWindow(show == 1);
					}
//				}
//			});
		}

	}

	public boolean isShowWindow() {
		final int show = Settings.System.getInt(mContext.getContentResolver(),
				"showwindow", 0);
		LogUtils.w(TAG, "show="+show);
		return show == 1;
	}
}
