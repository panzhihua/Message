package com.rongyan.hpmessage.cmns;
/**
 * 云箭消息推送回执
 */
import android.content.Context;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.ShowWindowObserver;
import com.rongyan.hpmessage.item.ReceiptResponseItem;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;

public class MessageReceiptTask extends AbstractTask {

	private final static String TAG="MessageReceiptTask";
	
	public MessageReceiptTask(Context context, String url,
			ShowWindowObserver observer) {
		super(context, url, observer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setResponseData(String value) {
		try {
			LogUtils.w(TAG,value);
			ReceiptResponseItem item = (ReceiptResponseItem) JsonUtils
					.jsonToBean(value, ReceiptResponseItem.class);
			if (!item.isSuccess()) {
				startTimer(10000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public void setFailedMessage() {
		LogUtils.w(TAG,"setFailedMessage");
		startTimer(10000);
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
