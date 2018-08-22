package com.rongyan.hpmessage.database;
import com.rongyan.hpmessage.util.LogUtils;

import com.rongyan.hpmessage.util.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageOpenHelper extends SQLiteOpenHelper{
	private final static String TAG = "MessageOpenHelper";
	
    private SQLiteDatabase mDB = null; 
    
    public static final int DB_VERSION = 3;

    private final String messagesql = "CREATE TABLE " + DatabaseColume.MessageInfo.TABLENAME + "("
			+ "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," +DatabaseColume.MessageInfo.ID
			+ " INTEGER," + DatabaseColume.MessageInfo.TITLE + " TEXT,"
			+ DatabaseColume.MessageInfo.SUMMARY + " TEXT,"
			+ DatabaseColume.MessageInfo.TIME + " TEXT,"
			+ DatabaseColume.MessageInfo.PREVIEW_ICON + " TEXT,"
			+ DatabaseColume.MessageInfo.ISREAD + " INTEGER,"
			+ DatabaseColume.MessageInfo.TYPE + " INTEGER)" ; 
    
    private final String adsql = "CREATE TABLE " + DatabaseColume.AdInfo.TABLENAME + "("
			+ "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," 
    		+ DatabaseColume.AdInfo.ID+ " INTEGER," 
			+ DatabaseColume.AdInfo.TIME + " INTEGER,"
			+ DatabaseColume.AdInfo.NUMBER + " INTEGER)" ; 
    
    private final String devicesql = "CREATE TABLE " + DatabaseColume.AppInfo.TABLENAME + "("
			+ "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," 
    		+ DatabaseColume.AppInfo.PKGNAME+ " TEXT," 
			+ DatabaseColume.AppInfo.PKGVERSIONSTRING + " TEXT," 
			+ DatabaseColume.AppInfo.PKGVERSIONCODESTRING + " INTEGER," 
    		+ DatabaseColume.AppInfo.USEDTIMESTRING+ " TEXT)";

    public MessageOpenHelper(Context context){  
        super(context, DatabaseColume.DB_NAME, null, DB_VERSION);  
    }  

    @Override  
    public void onCreate(SQLiteDatabase db){  
        mDB = db;  
        mDB.execSQL(messagesql); 
        mDB.execSQL(adsql);
        mDB.execSQL(devicesql);
        LogUtils.w(TAG, "onCreate");
    }  

    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){  
        // 升级
    	mDB = db; 
    	if(oldVersion<3){
        	LogUtils.w(TAG, "mDB.execSQL(devicesql)");
    		mDB.execSQL(devicesql);
    	}
    	
    	if(oldVersion<2){
    		LogUtils.w(TAG, "mDB.execSQL(adsql)");
    		mDB.execSQL(adsql);
    	}
    }  
}
