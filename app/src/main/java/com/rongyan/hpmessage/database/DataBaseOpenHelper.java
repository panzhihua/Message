package com.rongyan.hpmessage.database;

import java.util.ArrayList;
import java.util.List;

import com.rongyan.hpmessage.OnDataBaseListener;
import com.rongyan.hpmessage.database.DatabaseColume;
import com.rongyan.hpmessage.database.MessageProvider;
import com.rongyan.hpmessage.item.AdsItem;
import com.rongyan.hpmessage.item.AppItem;
import com.rongyan.hpmessage.item.MessageListResponseItem.Data.Notifications;
import com.rongyan.hpmessage.util.LogUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseOpenHelper {
	
	private final static String TAG = "DataBaseOpenHelper";
	
	private Context mContext;
	
	private static DataBaseOpenHelper mDataBaseOpenHelper;
	
	private OnDataBaseListener mOnDataBaseListener=null;
	
	public static DataBaseOpenHelper getInstance(Context context){
		if(mDataBaseOpenHelper == null){
			mDataBaseOpenHelper = new DataBaseOpenHelper(context);
		}
		return mDataBaseOpenHelper;
	}
	
	public DataBaseOpenHelper(Context context) {
		mContext = context;
	}
	
	public void setOnDataBaseListener(OnDataBaseListener onDataBaseListener){
		mOnDataBaseListener=onDataBaseListener;
	}
	
	//查询消息 0表示未读，1表示所有
	public List<Notifications> getNotifications(int type){
		Cursor query = null;
		try{
			if(type==0){
				query = mContext.getContentResolver().query(MessageProvider.CONTENT_URI, null,  
						DatabaseColume.MessageInfo.ISREAD + "= 0", null, DatabaseColume.MessageInfo.TIME + " DESC");
			}else if(type==1){
				query = mContext.getContentResolver().query(MessageProvider.CONTENT_URI, null,  
						null, null, DatabaseColume.MessageInfo.TIME + " DESC");
			}
			if (query!=null&&query.getCount() >0) {
				List<Notifications> mNotificationList=new ArrayList<Notifications>();
				while (query.moveToNext())  
	            {  
					Notifications bean = new Notifications();  
	            	bean.setId(query.getInt(query.getColumnIndex(DatabaseColume.MessageInfo.ID)));
	            	bean.setTitle(query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.TITLE)));
	            	bean.setSummary(query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.SUMMARY)));
	            	bean.setPreview_icon(query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.PREVIEW_ICON)));
	            	bean.setPushed_at(Long.valueOf((query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.TIME)))));
	            	bean.setType(query.getInt(query.getColumnIndex(DatabaseColume.MessageInfo.TYPE)));
	            	if(query.getInt(query.getColumnIndex(DatabaseColume.MessageInfo.ISREAD))==0){
	            		bean.setRead(false); 
	            	}else{
	            		bean.setRead(true);
	            	}
	            	mNotificationList.add(bean);
	            }
				if(query != null){
					query.close();
				}
				return mNotificationList;
			}
			if(query != null){
				query.close();
			}
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	//根据id查询消息是否存在
	public boolean isExist(String id,String type){
		Cursor query = mContext.getContentResolver().query(MessageProvider.CONTENT_URI, null,DatabaseColume.MessageInfo.ID + "=" +id+" and "+DatabaseColume.MessageInfo.TYPE + "=" +type, null, null, null);
		int count = query.getCount();
		if(query != null){
			query.close();
		}
		if(count > 0 ){
			return true;
		}else{
			return false;
		}

	}
	
	//根据id查询消息
	public Notifications Query(String id,String type){
		Cursor query = mContext.getContentResolver().query(MessageProvider.CONTENT_URI,  null,  
				DatabaseColume.MessageInfo.ID + "="+id+" and "+DatabaseColume.MessageInfo.TYPE + "=" +type, null, null);
		if (query.getCount() >0) {
			while (query.moveToNext())  
            {  
				Notifications bean = new Notifications();  
				bean.setId(query.getInt(query.getColumnIndex(DatabaseColume.MessageInfo.ID)));
            	bean.setTitle(query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.TITLE)));
            	bean.setSummary(query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.SUMMARY)));
            	bean.setPreview_icon(query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.PREVIEW_ICON)));
            	bean.setPushed_at(Long.valueOf((query.getString(query.getColumnIndex(DatabaseColume.MessageInfo.TIME)))));
            	bean.setType(query.getInt(query.getColumnIndex(DatabaseColume.MessageInfo.TYPE)));
            	if(query.getInt(query.getColumnIndex(DatabaseColume.MessageInfo.ISREAD))==0){
            		bean.setRead(false);
                }else{
                	bean.setRead(true);
                }
    			if(query != null){
    				query.close();
    			}
    			return bean;
            }
		}
		if(query != null){
			query.close();
		}
		return null;
	}
	
	//插入数据库
	public void Add(Notifications notification){ 
		try{
			LogUtils.w(TAG, "ADD:"+notification.getId());
			ContentValues  bean = new ContentValues();  
			bean.put(DatabaseColume.MessageInfo.ID, notification.getId());
			bean.put(DatabaseColume.MessageInfo.TITLE, notification.getTitle());  
			bean.put(DatabaseColume.MessageInfo.SUMMARY,notification.getSummary());
			bean.put(DatabaseColume.MessageInfo.TIME,notification.getPushed_at());
			bean.put(DatabaseColume.MessageInfo.PREVIEW_ICON,notification.getPreview_icon());
			bean.put(DatabaseColume.MessageInfo.TYPE,notification.getType());
			if(notification.isRead()){
				bean.put("isread",1); 
			}else{
				bean.put("isread",0);  
			}
			mContext.getContentResolver().insert(MessageProvider.CONTENT_URI, bean);
			if(mOnDataBaseListener!=null){
				mOnDataBaseListener.onDataBaseClick();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//修改数据库
	public void Update(String id,String type,int isRead){ 
		try{
			ContentValues values = new ContentValues();  
			values.put("isread",isRead);
			mContext.getContentResolver().update(MessageProvider.CONTENT_URI, values, DatabaseColume.MessageInfo.ID+"="+id+" and "+DatabaseColume.MessageInfo.TYPE+"="+type, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//删除数据库
	public void Delete(String time){ 
		try{
			mContext.getContentResolver().delete(MessageProvider.CONTENT_URI, DatabaseColume.MessageInfo.TIME+"<?", new String[] { time }); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//删除属性是广告的消息
	public void DeleteAD(String type){ 
		try{
			mContext.getContentResolver().delete(MessageProvider.CONTENT_URI, DatabaseColume.MessageInfo.TYPE+"=?", new String[] { type });
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//广告插入数据库
	public void AddAds(AdsItem adsItem){ 
		try{
			ContentValues  bean = new ContentValues();  
			bean.put(DatabaseColume.AdInfo.ID, adsItem.getId());
			bean.put(DatabaseColume.AdInfo.TIME, adsItem.getTime());  
			bean.put(DatabaseColume.AdInfo.NUMBER,adsItem.getNumber());
			mContext.getContentResolver().insert(MessageProvider.AD_URI, bean);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//根据id查询广告是否存在
	public boolean isExistAds(int id){
		Cursor query = mContext.getContentResolver().query(MessageProvider.AD_URI, null,DatabaseColume.AdInfo.ID + " = " +id, null, null);
		int count = query.getCount();
		if(query != null){
			query.close();
		}
		if(count > 0 ){
			return true;
		}else{
			return false;
		}
	}
	
	//查询广告
	public AdsItem getAds(int id){
		Cursor query = mContext.getContentResolver().query(MessageProvider.AD_URI, null,  
					DatabaseColume.AdInfo.ID + " = "+id, null, null);
		if (query!=null&&query.getCount() >0) {
			AdsItem mAdsItem=new AdsItem();
			while (query.moveToNext())  
            {  
				mAdsItem.setId(query.getInt(query.getColumnIndex(DatabaseColume.AdInfo.ID)));
				mAdsItem.setTime(query.getInt(query.getColumnIndex(DatabaseColume.AdInfo.TIME)));
				mAdsItem.setNumber(query.getInt(query.getColumnIndex(DatabaseColume.AdInfo.NUMBER)));           	
            }
			if(query != null){
				query.close();
			}
			return mAdsItem;
		}
		if(query != null){
			query.close();
		}
		return null;
	}
	
	//修改数据库广告
	public void Update(AdsItem adsItem){ 
		try{
			ContentValues values = new ContentValues();  
			values.put("ad_time",adsItem.getTime());
			values.put("ad_number",adsItem.getNumber());
			mContext.getContentResolver().update(MessageProvider.AD_URI, values, DatabaseColume.AdInfo.ID+"="+adsItem.getId(), null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 取出表中所有的记录
	 * 
	 * @return返回ApplicationItem List
	 */
	public List<AppItem> getAppInfoList() {
		List<AppItem> appList = new ArrayList<AppItem>();
		try{		
			Cursor cursor = mContext.getContentResolver().query(MessageProvider.APP_URI, null,null, null, null);
			while (cursor.moveToNext()) {
				AppItem app = new AppItem();
				app.setPackage_(cursor.getString(cursor
						.getColumnIndex(DatabaseColume.AppInfo.PKGNAME)));
				app.setVersion(cursor.getString(cursor
						.getColumnIndex(DatabaseColume.AppInfo.PKGVERSIONSTRING)));
				app.setVersionCode(cursor.getInt(cursor
						.getColumnIndex(DatabaseColume.AppInfo.PKGVERSIONCODESTRING)));
				app.setUseRate(Integer.valueOf(cursor.getString(cursor
						.getColumnIndex(DatabaseColume.AppInfo.USEDTIMESTRING))));
				appList.add(app);
			}
			if(cursor!=null){
				cursor.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return appList;
	}

	/**
	 * add persons
	 * 
	 * @param items
	 */
	public void add(List<AppItem> items) {
		try{
			if(items!=null&&!items.isEmpty()){
				for(AppItem appitem:items){
					ContentValues values = new ContentValues();
					values.put(DatabaseColume.AppInfo.PKGNAME, appitem.getPackage_());
					values.put(DatabaseColume.AppInfo.PKGVERSIONSTRING, appitem.getVersion());
					values.put(DatabaseColume.AppInfo.PKGVERSIONCODESTRING, appitem.getVersionCode());
					values.put(DatabaseColume.AppInfo.USEDTIMESTRING, appitem.getUseRate());
					mContext.getContentResolver().insert(MessageProvider.APP_URI, values);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 判断数据库中有无包含某个包的记录
	 */
	public Boolean haveAppInfo(String pkgname) {
		try{
			Cursor query = mContext.getContentResolver().query(MessageProvider.APP_URI, null,DatabaseColume.AppInfo.PKGNAME + " = '" +pkgname+"'", null, null);
			int count = query.getCount();
			if(query != null){
				query.close();
			}
			if(count > 0 ){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public int getAppInfoUsedTime(String pkgname) {
		int usedtime = 0;
		try{
			Cursor cursor = mContext.getContentResolver().query(MessageProvider.APP_URI, null,DatabaseColume.AppInfo.PKGNAME + " = '" +pkgname+"'", null, null);
			while (cursor.moveToNext()) {
				String usedtimefromdatabase = cursor.getString(cursor
						.getColumnIndex(DatabaseColume.AppInfo.USEDTIMESTRING));
				usedtime = Integer.valueOf(usedtimefromdatabase);
			}
			if(cursor != null){
				cursor.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return usedtime;
	}

	/**
	 * 根据包名更新表中的记录
	 */
	public void UpdateAppInfoString(AppItem item) {
		try{
			Log.w("mytest", "update table mPkgname:" + item.getPackage_()
					+ "  usedtime:" + item.getUseRate());
			ContentValues values = new ContentValues();
			values.put(DatabaseColume.AppInfo.PKGNAME, item.getPackage_());
			values.put(DatabaseColume.AppInfo.PKGVERSIONSTRING, item.getVersion());
			values.put(DatabaseColume.AppInfo.PKGVERSIONCODESTRING, item.getVersionCode());
			values.put(DatabaseColume.AppInfo.USEDTIMESTRING, item.getUseRate());
			mContext.getContentResolver().update(MessageProvider.APP_URI, values, DatabaseColume.AppInfo.PKGNAME+"= '"+item.getPackage_()+"'", null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 插入数据
	 */
	public void SaveUserInfo(AppItem item) {
		try{
			ContentValues values = new ContentValues();
			values.put(DatabaseColume.AppInfo.PKGNAME, item.getPackage_());
			values.put(DatabaseColume.AppInfo.PKGVERSIONSTRING, item.getVersion());
			values.put(DatabaseColume.AppInfo.PKGVERSIONCODESTRING, item.getVersionCode());
			values.put(DatabaseColume.AppInfo.USEDTIMESTRING, item.getUseRate());
			mContext.getContentResolver().insert(MessageProvider.APP_URI, values);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//删除数据
	public void DeleteUserInfo(String package_name){ 
		try{
			mContext.getContentResolver().delete(MessageProvider.APP_URI, DatabaseColume.AppInfo.PKGNAME+"=?", new String[] { package_name });
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
