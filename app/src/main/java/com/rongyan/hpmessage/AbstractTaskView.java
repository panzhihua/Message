package com.rongyan.hpmessage;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public abstract class AbstractTaskView {
   protected Context mContext;
   protected WindowManager mWindowManager;
   protected WindowManager.LayoutParams mLayoutParams;
   protected View mView;//要显示的view
   protected boolean isshow = false;
   
   public AbstractTaskView(Context context){
	   mContext = context;
	   mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
   }
   public boolean isShow(){
	   return isshow;
   }

   public void setShow(boolean show){
	   this.isshow=show;
   }

   protected abstract void startView();
   
   protected abstract void removeView();
	
}
