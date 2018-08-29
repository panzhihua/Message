package com.rongyan.hpmessage.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import com.rongyan.hpmessage.MessageService;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

public class NewHttpGetUtils extends Thread {

	private final static String TAG="NewHttpGetUtils";
	
	private String mURL;

	private Handler mHandler;

	private CallBack mCallBack;

	public interface CallBack {

		void setResponseData(String value);

		void setFailedResponse(String value);
		
		void setTimeoutResponse(String value);
	}

	public NewHttpGetUtils( CallBack callBack, String url, Handler handler) {
		mURL = url;
		mCallBack = callBack;
		mHandler = handler;
	}

	public void run() {
		HttpURLConnection connection = null;
		BufferedReader bufferedReader = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(mURL);
			connection = (HttpURLConnection) url.openConnection();
			// 设置请求方法，默认是GET
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setUseCaches(true);
			if(Build.SERIAL!=null&&!Build.SERIAL.equals("")){
				connection.addRequestProperty("device-sn", Build.SERIAL);
			}else{
				return;
			}
			LogUtils.w(TAG, "MessageService.mDeviceToken:"+ApplicationUtils.mDeviceToken);
			if (ApplicationUtils.mDeviceToken != null) {
				connection.addRequestProperty("message-device-token",
						ApplicationUtils.mDeviceToken);
			}
			if(ApplicationUtils.getmBROKER()!=null&&!ApplicationUtils.getmBROKER().equals("")) {
				connection.setRequestProperty("device-broker",
						ApplicationUtils.getmBROKER());
			}else{
				return;
			}
			if(ApplicationUtils.getUUID()!=null&&!ApplicationUtils.getUUID().equals("")) {
				connection.addRequestProperty("deivce-uuid",
						ApplicationUtils.getUUID());
			}else{
				return;
			}
			if(ApplicationUtils.getmMODEL()!=null&&!ApplicationUtils.getmMODEL().equals("")) {
				connection.setRequestProperty("device-model", ApplicationUtils.getmMODEL());
			}else{
				return;
			}
			if(ApplicationUtils.getmVERSION()!=null&&!ApplicationUtils.getmVERSION().equals("")) {
				connection.setRequestProperty("device-model-version", ApplicationUtils.getmVERSION());
			}else{
				return;
			}
			connection.setRequestProperty("device-build-display",Build.DISPLAY);
			final int code = connection.getResponseCode();
			LogUtils.w(TAG, mURL+":"+code);
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
						mCallBack.setResponseData(sendString.toString());
					}
				});
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mCallBack.setFailedResponse(String.valueOf(code));
					}
				});
			}
		} catch (final Exception e) {
			e.printStackTrace();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (e instanceof EOFException) {//抛出此类异常，表示连接丢失，也就是说网络连接的另一端非正常关闭连接
						mCallBack.setTimeoutResponse(e.toString());
					} else if (e instanceof ConnectException) {//抛出此类异常，表示无法连接，也就是说当前主机不存在
						mCallBack.setTimeoutResponse(e.toString());
					} else if (e instanceof SocketException) {//抛出此类异常，表示连接正常关闭，也就是说另一端主动关闭连接
						mCallBack.setTimeoutResponse(e.toString());
					} else if (e instanceof BindException) {//抛出此类异常，表示端口已经被占用。
						mCallBack.setTimeoutResponse(e.toString());
					} else{
						mCallBack.setFailedResponse(e.toString());
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
