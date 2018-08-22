package com.rongyan.hpmessage.messagelist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.rongyan.hpmessage.AbstractTask;
import com.rongyan.hpmessage.ShowWindowObserver;
import com.rongyan.hpmessage.cmns.MessageReceiptOprea;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.item.MessageListResponseItem.Data.Notifications;


public class MessageListTask extends AbstractTask implements ShowWindowObserver.CallBack{
	private final static String TAG="MessageListTask";
	static MessageListTask mMessageListTask;
	public MessageListView mMessageListView;
	List<Notifications> mList = new ArrayList<Notifications>();
	public static boolean isSysncDatabase = false;

	public static MessageListTask getInstance(Context context, String url,
			ShowWindowObserver observer) {
		if (mMessageListTask == null) {
			mMessageListTask = new MessageListTask(context, url, observer);			 
		}
		return mMessageListTask;
	}

	public static MessageListTask getInstance() {
		return mMessageListTask;
	}

	public MessageListTask(Context context, String url,
			ShowWindowObserver observer) {
		super(context, url, observer);
		mMessageListView = new MessageListView(context);
	}

	public void getMessgeListFromDatabase() {
		DataBaseOpenHelper openHelper = DataBaseOpenHelper
				.getInstance(mContext);
		List<Notifications> list = openHelper.getNotifications(0);
		if (list != null && !list.isEmpty()) {
			if (list.size() > 3) {
				mList = list.subList(0, 3);
			} else {
				mList = list;
			}
			setShowView(mList);
		} else {
			mList.clear();
			hideView();
		}
		isSysncDatabase = true;
	}

	/**
	 * HttpGetImageUtils 返回成功回调
	 */
	@Override
	public void setResponseData(String value) {
//		try {
//			LogUtils.w(TAG, value);
//			MessageListResponseItem item = (MessageListResponseItem) JsonUtils
//					.jsonToBean(value, MessageListResponseItem.class);
//			if (item != null  && item.getSuccess() && item.getData() != null) {
//				List<Notifications> list = item.getData().getNotifications();
//				if (list != null && list.size() > 0) {
//					if (list.size() > 2) {
//						mList = list.subList(0, 3);
//					} else {
//						mList = list;
//					}
//					setShowView(mList);
//				} 
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void setFailedMessage() {
		//startTimer(60000);
	}

	@Override
	public void startShowView() {
		if (mList != null && !mList.isEmpty()) {
			mMessageListView.startView();
		}
	}

	@Override
	public void hideView() {
		mMessageListView.removeView();
	}

	/**
	 * ShowWindowObserver.CallBack 回调
	 */

	@Override
	public void updateWindow(boolean show) {
		if (show) {
			startShowView();
		} else {
			hideView();
		}
	}
	
	public void updateList(Notifications notification){
//		if (mMessageListView != null) {
//			mList.add(0, notification);
//			if (mList.size() > 3) {
//				mList = mList.subList(0, 3);
//			}
//			setShowView(mList);
//		}
	}

	@Override
	public void setMessageListResponseData(String value) {
		// TODO Auto-generated method stub
		
	}

	public void setShowView(final List<Notifications> List){
		this.mList=List;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if(mList!=null&&!mList.isEmpty()){
					mMessageListView.setList(mList);
					if (mShowWindowObserver.isShowWindow()) {
						mMessageListView.startView();
					}
				}else{
					hideView();
				}
			}
		});
	}

}
