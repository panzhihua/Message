package com.rongyan.hpmessage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.rongyan.hpmessage.bootads.BootAdsTask;
import com.rongyan.hpmessage.desktop.DesktopQSTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 三级缓存之本地缓存
 */
public class LocalCacheUtils {

    private final static String TAG = "LocalCacheUtils";
    /**
     * 从本地读取图片
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url,int type){
        String fileName;
        LogUtils.w(TAG,"getBitmapFromLocal:"+url);
        try {
            fileName =url.substring(url.length()-14);
            File file = null;
            if(type==MyBitmapUtils.TYPE_AD){
            	file=new File(BootAdsTask.MPICTUREPATH_STRING,fileName);
            }else if(type==MyBitmapUtils.TYPE_DESKTOP){
            	file=new File(DesktopQSTask.MPICTUREPATH_STRING,fileName);
            }
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;//表示16位位图 565代表对应三原色占的位数
            opt.inPurgeable = true;// 允许可清除
            opt.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
            Bitmap bitmap =BitmapFactory.decodeStream(new FileInputStream(file),null,opt);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url,Bitmap bitmap,int type){
        LogUtils.w(TAG,"setBitmapToLocal:"+url);
        try {
            String fileName =url.substring(url.length()-14);
            File file = null;
            if(type==MyBitmapUtils.TYPE_AD){
            	file=new File(BootAdsTask.MPICTUREPATH_STRING,fileName);
            }else if(type==MyBitmapUtils.TYPE_DESKTOP){
            	file=new File(DesktopQSTask.MPICTUREPATH_STRING,fileName);
            }
            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            if (!parentFile.exists()){
                parentFile.mkdirs();
            }

            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

