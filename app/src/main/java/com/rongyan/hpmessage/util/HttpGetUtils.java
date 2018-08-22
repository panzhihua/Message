package com.rongyan.hpmessage.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.MessageService;

import android.os.Build;
import android.os.Handler;

/**
 * http 通过get方式获取json数据
 * 
 * @author liumeng
 * 
 */
public class HttpGetUtils implements Runnable {

	private final static String TAG="HttpGetUtils";

	String mURL;
	String mData;
	AbstractTask mAbstractTask;
	Handler mHandler;
	CallBack mCallBack;
	String mType;
	String mTime;

	public abstract interface CallBack {

		public abstract void setFailedResponse();
		
		public abstract void setResponseData(String value,String type,String time);
	}

	public HttpGetUtils(AbstractTask task, String url, Handler handler) {
		mURL = url;
		mAbstractTask = task;
		mHandler = handler;
	}

	public HttpGetUtils(CallBack callBack, String url, Handler handler) {
		mURL = url;
		mCallBack = callBack;
		mHandler = handler;
	}
	
	public HttpGetUtils(CallBack callBack, String url, Handler handler,String type,String time) {
		mURL = url;
		mCallBack = callBack;
		mHandler = handler;
		mTime=time;
		mType=type;
	}

	public void run() {
		HttpURLConnection connection = null;
		BufferedReader bufferedReader = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(mURL);
			LogUtils.w(TAG, "url:" + url);
			connection = (HttpURLConnection) url.openConnection();
			// 设置请求方法，默认是GET
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setReadTimeout(20000);
			connection.setConnectTimeout(20000);
			connection.setUseCaches(true);
			LogUtils.w(TAG, "sn:" + Build.SERIAL);
			if (!Build.SERIAL.equals("unknown")) {
				connection.addRequestProperty("RONGYAN-sn-no", Build.SERIAL);
			}
			if (!MessageApplication.getUUID().equals("")) {
				connection.addRequestProperty("RONGYAN-uuid",
						MessageApplication.getUUID());
			}
			if (MessageService.mDeviceToken != null) {
				connection.addRequestProperty("RONGYAN-devicetoken",
						MessageService.mDeviceToken);
			}
			connection.setRequestProperty("RONGYAN-fire-shop-no",
					MessageApplication.getentityId());
			connection.setRequestProperty("RONGYAN-model-version", Build.MODEL);
			connection.setRequestProperty("RONGYAN-android-version",
					Build.VERSION.RELEASE);
			connection
					.setRequestProperty("RONGYAN-soft-version", Build.DISPLAY);
			int code = connection.getResponseCode();
			LogUtils.w(TAG, "url:" + url+"|code:" + code);
			if (code == 200) {
				inputStream = connection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String valueString;
				while ((valueString = bufferedReader.readLine()) != null) {
					stringBuffer.append(valueString);
				}
				final String sendString = stringBuffer.toString();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mAbstractTask != null) {
							if (mURL.contains(MessageApplication.HTTP_NOTICATIONS_URL)) {
								mAbstractTask
										.setMessageListResponseData(sendString
												.toString());
							} else {
								mAbstractTask.setResponseData(sendString
										.toString());
							}
						} else {
							mCallBack.setResponseData(sendString.toString(),mType,mTime);
						}
					}
				});
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mAbstractTask != null) {
							mAbstractTask.setFailedMessage();
						} else {
							mCallBack.setFailedResponse();
						}
					}
				});
			}
		} catch (IOException e) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mAbstractTask != null) {
						mAbstractTask.setFailedMessage();
					} else {
						mCallBack.setFailedResponse();
					}
				}
			});
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
}
