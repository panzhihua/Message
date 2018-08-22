package com.rongyan.hpmessage;

import java.util.ArrayList;
import java.util.List;

import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.item.MessageListResponseItem.Data.Notifications;
import com.rongyan.hpmessage.messagelist.MessageListTask;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class DataBaseObserver extends ContentObserver {

	public Uri CONTENT_URI = Uri.parse("content://com.rongyan:497393102/message");
	
	private List<Notifications> mList = new ArrayList<Notifications>();
	
	private Context mContext;
	
	private Handler mHandler;
	
	private MessageListTask mMessageListTask;
	
	private DataBaseOpenHelper openHelper;
	
	public void observer(Context context,MessageListTask messageListTask) {
		mContext = context;
		mMessageListTask=messageListTask;
		mContext.getContentResolver().registerContentObserver(
				CONTENT_URI, true, this);
		openHelper = DataBaseOpenHelper.getInstance(context);
	}
	
	public void unObserver() {
		mContext.getContentResolver().unregisterContentObserver(this);
	}

	public DataBaseObserver(Handler handler) {
		super(handler);
		mHandler = handler;
	}

	public void queryUnReadMessage(){
		List<Notifications> list = openHelper.getNotifications(0);
		if (list != null && !list.isEmpty()) {
			if (list.size() > 3) {
				mList = list.subList(0, 3);
			} else {
				mList = list;
			}
		} else {
			mList.clear();
		}
		mMessageListTask.setShowView(mList);
	}

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
	    queryUnReadMessage();
    }

}
