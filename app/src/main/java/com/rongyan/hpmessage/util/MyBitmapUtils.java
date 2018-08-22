package com.rongyan.hpmessage.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 自定义的BitmapUtils,实现三级缓存
 */
public class MyBitmapUtils {
	private final String TAG = "MyBitmapUtils";
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    public static int TYPE_AD=1,TYPE_DESKTOP=2;

    public MyBitmapUtils(){
        mMemoryCacheUtils=new MemoryCacheUtils();
        mLocalCacheUtils=new LocalCacheUtils();
        mNetCacheUtils=new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
    }

    public void disPlay(ImageView ivPic, String url,int type) {
        Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);
            LogUtils.w(TAG,"--getBitmapFromMemory....."+type);
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url,type);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            LogUtils.w(TAG,"---getBitmapFromLocal....."+type);
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        
        //网络缓存
        LogUtils.w(TAG,"---downLoadBitmap....."+type);
        mNetCacheUtils.getBitmapFromNet(ivPic,url);
    }
    
    
    public Bitmap disPlay(String url,int type){
    	Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            LogUtils.w(TAG,"---getBitmapFromMemory....."+type);
            return bitmap;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url,type);
        if(bitmap !=null){
        	LogUtils.w(TAG,"---getBitmapFromLocal....."+type);
            return bitmap;
        }
        
        //网络缓存
        LogUtils.w(TAG,"---downLoadBitmap....."+type);
        return mNetCacheUtils.downLoadBitmapTwo(url,type);
    }
}

