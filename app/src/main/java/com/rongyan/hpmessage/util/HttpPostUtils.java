/**
 * http 璇锋眰绫�
 */
package com.rongyan.hpmessage.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.MessageService;
import com.rongyan.hpmessage.util.HttpGetUtils.CallBack;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;

@SuppressLint("NewApi")
public class HttpPostUtils implements Runnable {
	private final static String TAG="HttpPostUtils";
	String mData = null;
	private Handler mHandler;
	private String mURL;
	AbstractTask mAbstractTask;
	CallBack mCallBack;

	public abstract interface CallBack {
		public abstract void setResponseData(String value);

		public abstract void setFailedMessage();
	}
	
	public HttpPostUtils(String data, Handler handler, String url,
			AbstractTask task) {
		mData = data;
		mHandler = handler;
		mURL = url;
		mAbstractTask = task;
	}
	
	public HttpPostUtils(String data, Handler handler, String url,CallBack callBack) {
		mData = data;
		mHandler = handler;
		mURL = url;
		mCallBack = callBack;
	}

	public void setData(String value) {
		mData = value;
	}

	@Override
	public void run() {
		if (!JsonUtils.isJson(mData)) {
			return;
		}
		BufferedReader bufferedReader = null;
		InputStream inputStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(mURL);
			LogUtils.w(TAG, "sendurl:" + mURL);
			byte[] postData = mData.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Accept-Charset", "utf-8");
			urlConnection
					.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("Connection", "close");
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(postDataLength));
			LogUtils.w(TAG, "sn:" + Build.SERIAL);
			if (!Build.SERIAL.equals("unknown")) {
				urlConnection.addRequestProperty("RONGYAN-sn-no", Build.SERIAL);
			}
			if (!MessageApplication.getUUID().equals("")) {
				urlConnection.addRequestProperty("RONGYAN-uuid",
						MessageApplication.getUUID());
			}
			if (MessageService.mDeviceToken != null) {
				urlConnection.addRequestProperty("RONGYAN-devicetoken",
						MessageService.mDeviceToken);
			}
			urlConnection.setRequestProperty("RONGYAN-fire-shop-no",
					MessageApplication.getentityId());
			urlConnection.setRequestProperty("RONGYAN-model-version",
					Build.MODEL);
			urlConnection.setRequestProperty("RONGYAN-android-version",
					Build.VERSION.RELEASE);
			urlConnection.setRequestProperty("RONGYAN-soft-version",
					Build.DISPLAY);
			urlConnection.setReadTimeout(30000);
			urlConnection.setConnectTimeout(30000);
			urlConnection.setUseCaches(false);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			LogUtils.w(TAG, "send datasource:" + mData);
			DataOutputStream output = new DataOutputStream(
					urlConnection.getOutputStream());
			output.write(postData);
			output.flush();
			output.close();
			int code = urlConnection.getResponseCode();
			LogUtils.w(TAG, "url:" + url+"|code:"+ code);
			if (code == 200) {
				inputStream = urlConnection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				StringBuffer stringBuffer = new StringBuffer();
				String valueString;
				while ((valueString = bufferedReader.readLine()) != null) {
					stringBuffer.append(valueString);
				}
				final String sendString = stringBuffer.toString();
				LogUtils.w(TAG, "sendstring:" + sendString);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mAbstractTask != null) {
							mAbstractTask.setResponseData(sendString.toString());
						}else{
							mCallBack.setResponseData(sendString.toString());
						}
					}
				});
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mAbstractTask != null) {
							mAbstractTask.setFailedMessage();
						}else{
							mCallBack.setFailedMessage();
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mAbstractTask != null) {
						mAbstractTask.setFailedMessage();
					}else{
						mCallBack.setFailedMessage();
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
			if (urlConnection != null) {
				try {
					urlConnection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}