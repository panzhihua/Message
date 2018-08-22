package com.rongyan.hpmessage.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.rongyan.hpmessage.MessageService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

public class NewHttpPostUtils extends Thread {

	private final static String TAG="NewHttpPostUtils";
	
	private String mData = null;

	private Handler mHandler;

	private String mURL;

	private CallBack mCallBack;

	public  interface CallBack {

		void setPostResponseData(String value);

		void setPostFailedResponse(String value);
		
		void setPostTimeoutResponse(String value);
	}

	public NewHttpPostUtils(CallBack callBack, String url, Handler handler, String value) {
		mCallBack=callBack;
		mHandler = handler;
		mURL = url;
		mData = value;
	}

	@Override
	public void run() {
		if(mData==null||!JsonUtils.isJson(mData)){
			return;
		}
		BufferedReader bufferedReader = null;
		InputStream inputStream = null;
		HttpURLConnection urlConnection=null;
		try {
			URL url = new URL(mURL);
			LogUtils.w(TAG, "url:" + url);
			byte[] postData = mData.getBytes( StandardCharsets.UTF_8 );
			int postDataLength = postData.length;
		    urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Accept-Charset", "utf-8");
			urlConnection.setRequestProperty("Content-Type",
					"application/json");
			urlConnection.setRequestProperty("Connection", "close");
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(postDataLength));
			urlConnection.setReadTimeout(5000);
			urlConnection.setConnectTimeout(5000);
			urlConnection.setUseCaches(false);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			if(Build.SERIAL!=null&&!Build.SERIAL.equals("")){
				urlConnection.addRequestProperty("device-sn", Build.SERIAL);
			}else{
				return;
			}
			if (MessageService.mDeviceToken != null) {
				urlConnection.addRequestProperty("message-device-token",
						MessageService.mDeviceToken);
			}
			if(ApplicationUtils.getmBROKER()!=null&&!ApplicationUtils.getmBROKER().equals("")) {
				urlConnection.setRequestProperty("device-broker",
						ApplicationUtils.getmBROKER());
			}else{
				return;
			}
			if(ApplicationUtils.getUUID()!=null&&!ApplicationUtils.getUUID().equals("")) {
				urlConnection.addRequestProperty("deivce-uuid",
						ApplicationUtils.getUUID());
			}else{
				return;
			}
			if(ApplicationUtils.getmMODEL()!=null&&!ApplicationUtils.getmMODEL().equals("")) {
				urlConnection.setRequestProperty("device-model", ApplicationUtils.getmMODEL());
			}else{
				return;
			}
			if(ApplicationUtils.getmVERSION()!=null&&!ApplicationUtils.getmVERSION().equals("")) {
				urlConnection.setRequestProperty("device-model-version", ApplicationUtils.getmVERSION());
			}else{
				return;
			}
			urlConnection.setRequestProperty("device-build-display",Build.DISPLAY);
			DataOutputStream output = new DataOutputStream(
					urlConnection.getOutputStream());
			output.write(postData);
			output.flush();
			output.close();
			final int code = urlConnection.getResponseCode();
			LogUtils.w(TAG, mURL+":"+code);
			if (code== 200) {
				inputStream = urlConnection.getInputStream();
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
						mCallBack.setPostResponseData(sendString.toString());
					}
				});
			} else {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mCallBack.setPostFailedResponse(String.valueOf(code));
					}
				});
			}
		} catch (final Exception e) {
			e.printStackTrace();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (e instanceof EOFException) {//抛出此类异常，表示连接丢失，也就是说网络连接的另一端非正常关闭连接
						mCallBack.setPostTimeoutResponse(e.toString());
					} else if (e instanceof ConnectException) {//抛出此类异常，表示无法连接，也就是说当前主机不存在
						mCallBack.setPostTimeoutResponse(e.toString());
					} else if (e instanceof SocketException) {//抛出此类异常，表示连接正常关闭，也就是说另一端主动关闭连接
						mCallBack.setPostTimeoutResponse(e.toString());
					} else if (e instanceof BindException) {//抛出此类异常，表示端口已经被占用。
						mCallBack.setPostTimeoutResponse(e.toString());
					} else{
						mCallBack.setPostFailedResponse(e.toString());
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
			if(urlConnection!=null){
				try {
					urlConnection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

