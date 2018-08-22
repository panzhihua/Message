package com.rongyan.hpmessage.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.R.integer;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class FileUtils {

	//得到外部储存sdcard的状态
	private static String sdcard=Environment.getExternalStorageState();
	//外部储存sdcard存在的情况
	private static String state=Environment.MEDIA_MOUNTED;
	    
	private  static File file=Environment.getExternalStorageDirectory();
	    
	private  static StatFs statFs=new StatFs(file.getPath());

    private String FILE_NAME=Environment.getExternalStorageDirectory().getPath() + "/AppStore/boot.txt";
	/**
     * 计算Sdcard的剩余大小
     * @return MB
     */
    public static long getAvailableSize(){
        if(sdcard.equals(state)){
            //获得Sdcard上每个block的size
            long blockSize=statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable=statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal=blockSize*blockavailable;
            return blockavailableTotal;
        }else{
            return -1;
        }
    }
    
    /**
     * 判断是否有下载空间
     */
    public static boolean isDownLoad(long num){
        if(FileUtils.getAvailableSize()-num>5*1024*1024){
            return true;
        }
        return false;
    }


}
