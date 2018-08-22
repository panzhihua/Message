package com.rongyan.hpmessage.messagelist;

import android.content.Context;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.ShowWindowObserver;
import com.rongyan.hpmessage.util.LogUtils;

public class MessageReadTask extends AbstractTask{
	
	private final static String TAG="MessageReadTask";
	
	public MessageReadTask(Context context, String url,
			ShowWindowObserver observer) {
		super(context, url, observer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setResponseData(String value) {
		LogUtils.w(TAG, value);

	}

	@Override
	public void setFailedMessage() {
		LogUtils.w(TAG, "setFailedMessage");
	}

	@Override
	public void startShowView() {
		//not used
	}

	@Override
	public void hideView() {
		//not used

	}

	@Override
	public void setMessageListResponseData(String value) {
		// TODO Auto-generated method stub
		
	}
}
