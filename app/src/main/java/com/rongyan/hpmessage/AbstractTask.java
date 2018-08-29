package com.rongyan.hpmessage;

import java.util.Timer;
import java.util.TimerTask;

import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.HttpDownAPKUtils;
import com.rongyan.hpmessage.util.HttpGetUtils;
import com.rongyan.hpmessage.util.HttpPostUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

public abstract class AbstractTask {
	public Handler mHandler = new Handler();
	public Context mContext;
	public String mURL;
	private Timer mTimer=null;
	protected ShowWindowObserver mShowWindowObserver;
	public boolean issuccess;// 获取URL是否成功
	public boolean showWindow = false;//窗口是否显示
	protected Bitmap mBitmap = null;

	public abstract void setResponseData(String value);
	
	public abstract void setMessageListResponseData(String value);

	public abstract void startShowView();

	public abstract void hideView();

	public abstract void setFailedMessage();

	boolean mUsedPost = false;//是否使用post请求 默认是get方式

	String mData = null;
	
	HttpDownAPKUtils mHttpDownAPKUtils;

	public AbstractTask(Context context, String url, ShowWindowObserver observer) {
		mContext = context;
		mURL = url;
		mShowWindowObserver = observer;
	}
	
	public void setData(String data,boolean post){
		mData = data;
		mUsedPost = true;
	}

	public void startTimer(long time) {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (ApplicationUtils.ismNetWorkEnable()) {
			mTimer = new Timer();
			mTimer.schedule(new MyTask(), time);
		}
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {
			if (mUsedPost) {
				startHttpPostConnect();
			} else {
				startHttpConnect();
			}
		}
	}

	protected void startHttpConnect() {
		if (ApplicationUtils.ismNetWorkEnable()) {
			ThreadPoolUtils.newFixThreadPool(new HttpGetUtils(this, mURL, mHandler));
		}
	}

	protected void startHttpPostConnect() {
		if (ApplicationUtils.ismNetWorkEnable() && mData != null) {
			if(mURL != null){
				ThreadPoolUtils.newFixThreadPool(new HttpPostUtils(mData, mHandler, mURL,this));
			}
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

	public void release(){
		if(mBitmap != null && !mBitmap.isRecycled()){
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	public void recycle(Bitmap bitmap){
		bitmap.recycle();
	}
}
