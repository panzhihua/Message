package com.rongyan.hpmessage.messagelist;

import java.util.List;

import com.rongyan.hpmessage.AbstractTaskView;
import com.rongyan.hpmessage.R;
import com.rongyan.hpmessage.item.MessageListResponseItem;
import com.rongyan.hpmessage.item.MessageListResponseItem.Data.Notifications;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;

public class MessageListView extends AbstractTaskView{

    private MessagePromptAdapter mMessagePromptAdapter;

    //私有化构造函数
    public MessageListView(Context context) {
    	super(context);
    	mMessagePromptAdapter=new MessagePromptAdapter(context);
    	mView = setUpView();
    }

    private View setUpView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_prompt, null);
        ListView mListview = (ListView)view.findViewById(R.id.message_prompt_listview);
        FrameLayout mCloseView = (FrameLayout)view.findViewById(R.id.message_close_fly);
        mListview.setAdapter(mMessagePromptAdapter);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	removeView();
            }
        });
        return view;
    }
    
    public  void setList(List<MessageListResponseItem.Data.Notifications> mNotificationList){
		if(mMessagePromptAdapter!=null){
			mMessagePromptAdapter.setList(mNotificationList);
		}
    }
    
    public  void addList(Notifications mNotificationList){
		if(mMessagePromptAdapter!=null){
			mMessagePromptAdapter.appendTop(mNotificationList);
		}
    }

	@Override
	protected void startView() {
		try{
			if(!isshow){
	    		if(mView == null){
		        	mView=setUpView();
		        }
		        if (mLayoutParams == null) {
		        	mLayoutParams = new WindowManager.LayoutParams();
		        	mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		        	mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		        	mLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
		        	mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		        	mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		        	mLayoutParams.format = PixelFormat.RGBA_8888;
		        }
		        if(mWindowManager==null){
		        	mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		        }
		        mWindowManager.addView(mView, mLayoutParams);	        
		        isshow=true;
	    	}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void removeView() {
		if (isshow && mView!=null && null != mView.getParent()) {
            mWindowManager.removeView(mView);
            isshow=false;
        }		
	}
}
