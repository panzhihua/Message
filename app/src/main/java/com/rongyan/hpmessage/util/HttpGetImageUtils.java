package com.rongyan.hpmessage.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.MessageService;
import com.rongyan.hpmessage.item.BootAdsResponseItem.Data.Ads;

import android.R.integer;
import android.os.Build;
/**
 * add by liumeng 获取指定 URL 图片
 * @author 刘萌
 *
 */
public class HttpGetImageUtils implements Runnable {

	private String mURL = null;
	private CallBack mCallBack;
	private Ads mAds;
	private int order;
	private final String TAG = "HttpGetImageUtils";

	public abstract interface CallBack {
		public abstract void saveImage(InputStream image,Ads ads,int order);
		public abstract void setFailedResponse();
	}

	public HttpGetImageUtils(String url ,Ads ads,CallBack callback,int i) {
		mURL = url;
		mAds=ads;
		mCallBack = callback;
		order=i;
	}

	@Override
	public void run() {
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		if (mURL != null) {
			try {
				URL url = new URL(mURL);
				if (url != null) {
					httpURLConnection = (HttpURLConnection) url
							.openConnection();
					// 设置连接网络的超时时间
					httpURLConnection.setConnectTimeout(10000);
					httpURLConnection.setDoInput(true);
					httpURLConnection.setUseCaches(true);
					// 设置本次http请求使用get方式请求
					httpURLConnection.setRequestMethod("GET");
					if (ApplicationUtils.getmSN()!=null&&!ApplicationUtils.getmSN().equals("")) {
						httpURLConnection.addRequestProperty("RONGYAN-sn-no", ApplicationUtils.getmSN());
					}
					if (!ApplicationUtils.getUUID().equals("")) {
						httpURLConnection.addRequestProperty("RONGYAN-uuid",
								ApplicationUtils.getUUID());
					}
					if (ApplicationUtils.mDeviceToken != null) {
						httpURLConnection.addRequestProperty("RONGYAN-devicetoken",
								ApplicationUtils.mDeviceToken);
					}
					httpURLConnection.setRequestProperty("RONGYAN-fire-shop-no",
							ApplicationUtils.getentityId());
					httpURLConnection.setRequestProperty("RONGYAN-model-version", Build.MODEL);
					httpURLConnection.setRequestProperty("RONGYAN-android-version",
							Build.VERSION.RELEASE);
					httpURLConnection.setRequestProperty("RONGYAN-soft-version", Build.DISPLAY);
					int responseCode = httpURLConnection.getResponseCode();
					LogUtils.w(TAG, "get image responseCode :"+responseCode);
					if (responseCode == 200) {
						// 从服务器获得一个输入流
						inputStream = httpURLConnection.getInputStream();
						mCallBack.saveImage(inputStream,mAds,order);
					}else{
						mCallBack.setFailedResponse();
					}
					
				}
			} catch (MalformedURLException e) {
				mCallBack.setFailedResponse();
			} catch (IOException e) {
				mCallBack.setFailedResponse();
			} finally {
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

}
