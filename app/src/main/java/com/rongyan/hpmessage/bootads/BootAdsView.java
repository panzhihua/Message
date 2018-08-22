package com.rongyan.hpmessage.bootads;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.sdk.android.man.MANPageHitBuilder;
import com.rongyan.hpmessage.AbstractTaskView;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.MessageService;
import com.rongyan.hpmessage.R;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.database.DatabaseColume;
import com.rongyan.hpmessage.item.AdsItem;
import com.rongyan.hpmessage.item.BootAdsResponseItem.Data.Ads;
import com.rongyan.hpmessage.messagelist.MessageActivity;
import com.rongyan.hpmessage.messagelist.MessageReadTask;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.MyBitmapUtils;
import com.rongyan.hpmessage.util.StringUtils;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BootAdsView extends AbstractTaskView{
	private final static String TAG="BootAdsView";
	private BootAdsTask mBootAdsTask;
    private ImageView mADImage;
    private Bitmap mBitmap;
    private ImageView mOperaImage;
    private View mAdsView;//右边弹框页面
    private int mNotificationId=-1;
    private boolean ad_only=true;
    private Animation in_animation,out_animation;
    private static boolean isOpened = true;
    private ApplicationUtils mApplicationUtils;
    private long start_standTime;
    private MANPageHitBuilder pageHitBuilder;
    private DataBaseOpenHelper mDataBaseOpenHelper;
    private MyBitmapUtils myBitmapUtils;
    private int i;
    private List<String> mByteList;
    private List<Ads> adsList;
    private boolean isBoolen;
    private int pageNum=0;
    public boolean isOpen=false;
    private int oldNotificationId=-1;
    private int id=-1;
    
    //私有化构造函数
    public BootAdsView(BootAdsTask bootAdsTask,Context context,DataBaseOpenHelper dataBaseOpenHelper) {
    	super(context);
    	mView = setUpView();
    	mBootAdsTask=bootAdsTask;
    	if (mApplicationUtils == null) {
			mApplicationUtils = ApplicationUtils.getIntance(mContext);
		}
    	mDataBaseOpenHelper=dataBaseOpenHelper;
    	pageHitBuilder = new MANPageHitBuilder("BootAdsView");
    	myBitmapUtils= new MyBitmapUtils();
    }

    private View setUpView() {
    	View view = LayoutInflater.from(mContext).inflate(R.layout.ad_toast, null);
    	try{        
	        mOperaImage = (ImageView) view.findViewById(R.id.operaimage);
	        mADImage = (ImageView) view.findViewById(R.id.ad_img);
	        mAdsView = (LinearLayout) view.findViewById(R.id.ad_ly);
	        in_animation = AnimationUtils.loadAnimation(mContext, R.anim.dock_right_enter);
	        out_animation = AnimationUtils.loadAnimation(mContext, R.anim.dock_right_exit);
	        in_animation.setAnimationListener(new AnimationListener() {
	        	@Override
	        	public void onAnimationStart(Animation animation) {
	        		setView(true,false);
	        	}
	        	@Override
	        	public void onAnimationRepeat(Animation animation) {
	        	}
	        	@Override
	        	public void onAnimationEnd(Animation animation) { 
	        		
	        	}
	        });
	        out_animation.setAnimationListener(new AnimationListener() {
	        	@Override
	        	public void onAnimationStart(Animation animation) {
	        		
	        	}
	        	@Override
	        	public void onAnimationRepeat(Animation animation) {
	        	}
	        	@Override
	        	public void onAnimationEnd(Animation animation) {
	        		setView(false,false);
	        	}
	        });
	        mOperaImage.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	if(isOpened){
	            		setView(false,false);
	            	}else{
	            		if(mByteList!=null&&!mByteList.isEmpty()){
		            		if(setImageView(false)){
		            			mAdsView.startAnimation(in_animation);
		            		}
	            		}else{
	            			setView(true,false);
	            		}
	            	}           	
	            }
	        });
	        mADImage.setOnClickListener(new View.OnClickListener() {	
				@Override
				public void onClick(View arg0) {		
					if(!ad_only){
						Intent mIntent = new Intent(mContext, MessageActivity.class);
		            	mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		            	mIntent.putExtra("id", mNotificationId);  
		            	mIntent.putExtra("type", Integer.valueOf(DatabaseColume.MESSAGE));  
		            	mContext.startActivity(mIntent);
					}
				}
			});
	        View.OnTouchListener touchListener = new View.OnTouchListener() {
	            float startX;
	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                switch (event.getAction()) {
	                    case MotionEvent.ACTION_DOWN:
	                        startX = event.getRawX();
	                        break;
	                    case MotionEvent.ACTION_MOVE:
	                        break;
	                    case MotionEvent.ACTION_UP:
	                        float endX = event.getRawX();
	                        if (endX - startX < -6) {
	                        	if(!isOpened){
	                        		if(mByteList!=null&&!mByteList.isEmpty()){
	            	            		if(setImageView(false)){
	            	            			mAdsView.startAnimation(in_animation);
	            	            		}
	                        		}else{
	                        			setView(true,false);
	                        		}
	                        	}
	                            return true;
	                        }else if(endX - startX>6) {
	                        	if(isOpened){
	                        		setView(false,false);
	                        	}
	                            return true;
	                        }
	                        break;
	                    }
	                return false;
	            }
	        };
	        mOperaImage.setOnTouchListener(touchListener);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return view;
    }
	public void setImageView(Bitmap bitmap,int notificationId) {
		mBitmap = bitmap;
		mNotificationId=notificationId;
		oldNotificationId=mNotificationId;
		mADImage.setImageBitmap(mBitmap);
	}

	public void setInitList(){
		mNotificationId=oldNotificationId;
		id=-1;
		ad_only=true;
		mByteList=new ArrayList<String>();
		adsList=new ArrayList<Ads>();
		pageNum=0;
	}
	
	public void setInitI(){
		if(mByteList!=null&&!mByteList.isEmpty()){
			i=mByteList.size()-1;
		}else{
			i=0;
		}
	}
	
	public void setImageViewList(String mByte,Ads ad) {
		if(mByte!=null){
			if(adsList.isEmpty()){
				mByteList.add(mByte);
				adsList.add(ad);
			}else{
				boolean isLike=false;
				for(Ads ads:adsList){
					if(ads.getId()==ad.getId()){
						isLike=true;
						return;
					}
				}
				if(!isLike){
					mByteList.add(mByte);
					adsList.add(ad);
				}
			}
		}
	}
	
	public boolean setImageView(boolean initiative) {
		isBoolen=false;
		try{
			if(mByteList!=null&&!mByteList.isEmpty()){
				if(i<0||i>mByteList.size()-1){
					i=mByteList.size()-1;
				}
				if(initiative){
					if(moreTime(adsList.get(i))&&moreOpen(adsList.get(i))){
						pageNum=0;
						mBitmap = myBitmapUtils.disPlay(mByteList.get(i),MyBitmapUtils.TYPE_AD);
						mNotificationId=adsList.get(i).getNotification_id();
						ad_only=adsList.get(i).isAd_only();
						oldNotificationId=mNotificationId;
						id=adsList.get(i).getId();
						mADImage.setImageBitmap(mBitmap);
						i--;
						isBoolen=true;
						return isBoolen;
					}else{
						i--;
						pageNum++;
						if(pageNum>mByteList.size()){
							i++;
							isBoolen=false;
							return isBoolen;
						}
						setImageView(initiative);
					}
				}else{
					if(moreTime(adsList.get(i))){
						pageNum=0;
						mBitmap = myBitmapUtils.disPlay(mByteList.get(i),MyBitmapUtils.TYPE_AD);
						mNotificationId=adsList.get(i).getNotification_id();
						ad_only=adsList.get(i).isAd_only();
						oldNotificationId=mNotificationId;
						id=adsList.get(i).getId();
						mADImage.setImageBitmap(mBitmap);
						i--;
						isBoolen=true;
						return isBoolen;
					}else{
						i--;
						pageNum++;
						if(pageNum>mByteList.size()){
							i++;
							isBoolen=false;
							return isBoolen;
						}
						setImageView(initiative);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isBoolen;
	}

	public void release(){
		if(mBitmap != null && !mBitmap.isRecycled()){
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	@Override
	protected void startView() {
		try{
			if(!isshow){
		        isshow=true;
		        if(mView == null){
		        	mView=setUpView();
		        }
		        if (mLayoutParams == null) {
		        	mLayoutParams = new WindowManager.LayoutParams();
		        	mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;	
		        	mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		        	mLayoutParams.gravity =Gravity.RIGHT;
		        	mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		        	mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		        	mLayoutParams.format = PixelFormat.RGBA_8888;
		        	mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		        }	     
		        if(mWindowManager!=null){
		        	mWindowManager.addView(mView, mLayoutParams);
		        }
		        start_standTime=System.currentTimeMillis();
			}else{
				if(isOpen){
					isOpen=false;
					setView(true,true);
				}else{
					setView(false,false);
				}
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void removeView() {
		if (isshow && mView!=null && null != mView.getParent()) {
			LogUtils.w(TAG, "removeView()");
            mWindowManager.removeView(mView);
            setView(false,false);
            pageHitBuilder.setDurationOnPage(System.currentTimeMillis()-start_standTime);
            pageHitBuilder.setProperty("mNotificationId",String.valueOf(mNotificationId));
            pageHitBuilder.build();
            isshow=false;
        }
	}
	
	public void setView(boolean isShow,boolean initiative){
		if(isShow){
			if(initiative){
				if(mDataBaseOpenHelper.isExistAds(id)){
					LogUtils.w(TAG, "AA");
					AdsItem mAdsItem=mDataBaseOpenHelper.getAds(id);
					mAdsItem.setNumber(mAdsItem.getNumber()+1);
					mAdsItem.setTime((int)StringUtils.getSystemTime());
					mDataBaseOpenHelper.Update(mAdsItem);
					mApplicationUtils.writePerferencesValue(ApplicationUtils.DAY_STRING, StringUtils.getStrDate(0));
					mApplicationUtils.writePerferencesValue(ApplicationUtils.OPEN_NUMBER_STRING,mApplicationUtils.getPerferencesIntValue(ApplicationUtils.OPEN_NUMBER_STRING)+1);
				}else{
					LogUtils.w(TAG, "BB");
					// 把广告放入数据库											
					AdsItem mAdsItem = new AdsItem();
					mAdsItem.setId(id);													
					mAdsItem.setTime((int)StringUtils.getSystemTime());
					mAdsItem.setNumber(1);
					mDataBaseOpenHelper.AddAds(mAdsItem);
					mApplicationUtils.writePerferencesValue(ApplicationUtils.DAY_STRING, StringUtils.getStrDate(0));
					mApplicationUtils.writePerferencesValue(ApplicationUtils.OPEN_NUMBER_STRING,mApplicationUtils.getPerferencesIntValue(ApplicationUtils.OPEN_NUMBER_STRING)+1);
				}
			}
			start_standTime=System.currentTimeMillis();
			mADImage.setVisibility(View.VISIBLE);
			mOperaImage.setBackgroundResource(R.drawable.btn_close_selector);
			postReadTask(mNotificationId);
			isOpened = true;
		}else{
			pageHitBuilder.setDurationOnPage(System.currentTimeMillis()-start_standTime);
            pageHitBuilder.setProperty("mNotificationId",String.valueOf(mNotificationId));
            pageHitBuilder.build();
			mADImage.setVisibility(View.GONE);
			mOperaImage.setBackgroundResource(R.drawable.btn_open_selector);
			isOpened = false;
		}
	}

	//判断广告是否在显示时间段
	private boolean moreTime(Ads ad){
		if(ad.getStart_at()==null||ad.getEnd_at()==null||ad.getStart_at().equals("")||ad.getEnd_at().equals("")){
			return true;
		}else{
			Date d = new Date();  
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");  
	        String dateNowStr = sdf.format(d);
	        return ApplicationUtils.compareTime(ad.getStart_at(),dateNowStr,ad.getEnd_at());
		}
	}
	
	//判断广告是否符合弹出条件
	private boolean moreOpen(Ads ad){
		AdsItem adsItem=mDataBaseOpenHelper.getAds(ad.getId());
		if(adsItem!=null){
			LogUtils.w(TAG, StringUtils.getSystemTime()+"::"+adsItem.getTime());
			if((StringUtils.getSystemTime()-adsItem.getTime())/(24*60*60)>1&&adsItem.getNumber()<3){//判断广告是否在24小时内在本机上显示过或者已经在本机上显示过3次
				LogUtils.w(TAG, mApplicationUtils.getPerferencesStringValue(ApplicationUtils.DAY_STRING)+"::"+StringUtils.getStrDate(0));
				if(mApplicationUtils.getPerferencesStringValue(ApplicationUtils.DAY_STRING).equals(StringUtils.getStrDate(0))){//判断日期
					LogUtils.w(TAG, "NUM="+mApplicationUtils.getPerferencesIntValue(ApplicationUtils.OPEN_NUMBER_STRING));
					if(mApplicationUtils.getPerferencesIntValue(ApplicationUtils.OPEN_NUMBER_STRING)<2){//判断一天内弹出次数
						LogUtils.w(TAG, "return true");
						return true;
					}
				}else {//如果日期不一样就把弹出次数清空
					mApplicationUtils.writePerferencesValue(ApplicationUtils.OPEN_NUMBER_STRING,0);
					LogUtils.w(TAG, "return true");
					return true;
				}
			}
		}else{
			LogUtils.w(TAG, "return true");
			return true;
		}
		LogUtils.w(TAG, "return false");
		return false;
	}
	
	public void setOpen(boolean open){
		this.isOpen=open;
	}
	
	public boolean getOpen(){
		return isOpen;
	}
	
	public void postReadTask(int id){
		try{
			LogUtils.w(TAG, "postReadTask=="+id);
			String url = MessageApplication.HTTP_NOTIFICATION_PUSHRECEIPT_STRING
					+ id + "/read";
			MessageReadTask task = new MessageReadTask(
					mContext, url, null);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("sn_no", Build.SERIAL);
			map.put("uuid", MessageApplication.getUUID());
			map.put("device_token", MessageService.mDeviceToken);
			map.put("fire_shop_no",
					MessageApplication.getentityId());
			map.put("actived_at",
					ApplicationUtils
							.getIntance(mContext)
							.getPerferencesStringValue(
									ApplicationUtils.ACTIVITIES_TIME_STRING));
			String receiptString = JsonUtils.beanToJson(map);
			task.setData(receiptString, true);
			task.startTimer(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
