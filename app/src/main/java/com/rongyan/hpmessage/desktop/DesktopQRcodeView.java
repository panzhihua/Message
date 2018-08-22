package com.rongyan.hpmessage.desktop;

import com.rongyan.hpmessage.AbstractTaskView;
import com.rongyan.hpmessage.R;
import com.rongyan.hpmessage.item.DesktopEntrancesItem.Data.Desktop_Entries;
import com.rongyan.hpmessage.messagelist.MessageActivity;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class DesktopQRcodeView extends AbstractTaskView {
	private final String TAG = "DesktopQRcodeView";
	private int mWindowWidth, mWindowHeight;
	private ImageView mDesktopImage;
	private Bitmap mBitmap;
	private Desktop_Entries mDesktop_Entries;
	
	private static String TO_APP="to_app",TO_URL="to_url";

	public DesktopQRcodeView(Context context) {
		super(context);
		try{
			mContext = context;
			mWindowManager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
			mWindowHeight = mWindowManager.getDefaultDisplay().getHeight();
			mView = LayoutInflater.from(mContext).inflate(
					R.layout.desktopqs_layout, null);
			mDesktopImage = (ImageView) mView.findViewById(R.id.desktopview);
			initEvent();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void initEvent() {
		mDesktopImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{				
					if(mDesktop_Entries!=null&&mDesktop_Entries.getBehavior().equals(TO_URL)){
						Intent intent = new Intent();        
				        intent.setAction("android.intent.action.VIEW");    
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				        Uri content_url = Uri.parse(mDesktop_Entries.getUrl());   
				        intent.setData(content_url);  
				        mContext.startActivity(intent);
					}else if(mDesktop_Entries!=null&&mDesktop_Entries.getBehavior().equals(TO_APP)){
						Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mDesktop_Entries.getPackage_name());
						mContext.startActivity(intent);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	public void setImageView(Bitmap bitmap,Desktop_Entries desktop_Entries) {
		mBitmap = bitmap;
		mDesktop_Entries=desktop_Entries;
		mDesktopImage.setImageBitmap(mBitmap);
	}
	
	public void setImageDesktopEntries(Desktop_Entries desktop_Entries) {
		mDesktop_Entries=desktop_Entries;
	}

	public void release() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
	}

	@Override
	public void startView() {
		try{
			if (!isshow) {
				if (mLayoutParams == null) {
					mLayoutParams = new WindowManager.LayoutParams();
					mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
					mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
							| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL ;
					mLayoutParams.format = PixelFormat.RGBA_8888;
					mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
					mLayoutParams.height =WindowManager.LayoutParams.WRAP_CONTENT;	
					mLayoutParams.x = mWindowWidth - mLayoutParams.width;
					mLayoutParams.y = mWindowHeight - mLayoutParams.height;
				}
				mWindowManager.addView(mView, mLayoutParams);
				isshow = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void removeView() {
		if (mView != null && mView.getParent() != null && isshow) {
			mWindowManager.removeView(mView);
			isshow = false;
		}
	}
}
