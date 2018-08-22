package com.rongyan.hpmessage.database;

import com.rongyan.hpmessage.util.LogUtils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MessageProvider extends ContentProvider{
	
	private final String TAG = "MessageProvider";
	
	public static final String SCHEME = "content"; // 源码里面规定这样写，所以这个地方改变不了  
	  
    public static final String HOST = "com.rongyan";  
    public static final String PORT = "497393102";  
    public static final String PATH_MESSAGE = "message";
    public static final String PATH_AD = "ad";
    public static final String PATH_APP = "app";
  
    public static final int ALARMS = 1;  
    public static final String SHARE_LIST_TYPE = "com.rongyan.message.dir/";  
    public static final int ALARMS_ID = 2;  
    public static final String SHARE_TYPE = "com.rongyan.message.item/";  
  
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);  
  
    private MessageOpenHelper mDB = null;  


    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + HOST + ":" + PORT + "/" + PATH_MESSAGE); 
    
    public static final Uri AD_URI = Uri.parse(SCHEME + "://" + HOST + ":" + PORT + "/" + PATH_AD);
    
    public static final Uri APP_URI = Uri.parse(SCHEME + "://" + HOST + ":" + PORT + "/" + PATH_APP);
  
    // 添加Uri的匹配方式，返回的就是上面自定义的整数类型，1代表操作的是一个批量，2操作的是单独的一个对象  
    static{  
        sURIMatcher.addURI(HOST + ":" + PORT, PATH_MESSAGE , ALARMS);  
        sURIMatcher.addURI(HOST + ":" + PORT, PATH_AD , ALARMS); 
        sURIMatcher.addURI(HOST + ":" + PORT, PATH_APP , ALARMS); 
    }  
  
    @Override  
    public boolean onCreate(){  
        mDB = new MessageOpenHelper(getContext()); // 获取数据库的引用  
        return mDB != null;  
    }  
  
    @Override  
    public Uri insert(Uri uri, ContentValues values){  
        // 首先是看Uri和我们自定义的是否匹配，匹配则将数据属性插入到数据库中并同步更新  
        SQLiteDatabase db = mDB.getWritableDatabase();  
        if (sURIMatcher.match(uri) != ALARMS){  
            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);  
        }    

        if(uri.toString().contains(PATH_MESSAGE)){
	        ContentValues filteredValues = new ContentValues();  
	        filteredValues.put(DatabaseColume.MessageInfo.ID, values.getAsInteger(DatabaseColume.MessageInfo.ID)); 
	        filteredValues.put(DatabaseColume.MessageInfo.TITLE, values.getAsString(DatabaseColume.MessageInfo.TITLE)); 
	        filteredValues.put(DatabaseColume.MessageInfo.SUMMARY, values.getAsString(DatabaseColume.MessageInfo.SUMMARY)); 
	        filteredValues.put(DatabaseColume.MessageInfo.TIME, values.getAsString(DatabaseColume.MessageInfo.TIME)); 
	        filteredValues.put(DatabaseColume.MessageInfo.PREVIEW_ICON, values.getAsString(DatabaseColume.MessageInfo.PREVIEW_ICON));
	        filteredValues.put(DatabaseColume.MessageInfo.ISREAD, values.getAsInteger(DatabaseColume.MessageInfo.ISREAD));  
	        filteredValues.put(DatabaseColume.MessageInfo.TYPE, values.getAsInteger(DatabaseColume.MessageInfo.TYPE));  
	        long rowID = db.insert(DatabaseColume.MessageInfo.TABLENAME, null, filteredValues);  
	        if (rowID != -1){  
	            getContext().getContentResolver().notifyChange(uri, null);  
	        }  
	        return CONTENT_URI;
        }else if(uri.toString().contains(PATH_AD)){
        	ContentValues filteredValues = new ContentValues();  
	        filteredValues.put(DatabaseColume.AdInfo.ID, values.getAsInteger(DatabaseColume.AdInfo.ID)); 
	        filteredValues.put(DatabaseColume.AdInfo.TIME, values.getAsInteger(DatabaseColume.AdInfo.TIME)); 
	        filteredValues.put(DatabaseColume.AdInfo.NUMBER, values.getAsInteger(DatabaseColume.AdInfo.NUMBER)); 
	        long rowID = db.insert(DatabaseColume.AdInfo.TABLENAME, null, filteredValues);  
	        if (rowID != -1){  
	            getContext().getContentResolver().notifyChange(uri, null);  
	        }  
	        return AD_URI;
        }else {
        	ContentValues filteredValues = new ContentValues();  
	        filteredValues.put(DatabaseColume.AppInfo.PKGNAME, values.getAsString(DatabaseColume.AppInfo.PKGNAME)); 
	        filteredValues.put(DatabaseColume.AppInfo.PKGVERSIONSTRING, values.getAsString(DatabaseColume.AppInfo.PKGVERSIONSTRING));
	        filteredValues.put(DatabaseColume.AppInfo.PKGVERSIONCODESTRING, values.getAsInteger(DatabaseColume.AppInfo.PKGVERSIONCODESTRING));
	        filteredValues.put(DatabaseColume.AppInfo.USEDTIMESTRING, values.getAsString(DatabaseColume.AppInfo.USEDTIMESTRING)); 
	        long rowID = db.insert(DatabaseColume.AppInfo.TABLENAME, null, filteredValues);  
	        if (rowID != -1){  
	            getContext().getContentResolver().notifyChange(uri, null);  
	        }  
	        return APP_URI;
        }
    }  
  
    @Override  
    public int delete(Uri uri, String selection, String[] selectionArgs){  
        // 首先是看Uri和我们自定义的是否匹配，，匹配则进行删除   
        SQLiteDatabase db = mDB.getWritableDatabase();  
        int count = 0;  
        int match = sURIMatcher.match(uri);  
        switch (match){  
            case ALARMS:  
            case ALARMS_ID:  
                String where = null;  
                // 这里对selection进行匹配操作，看你传递的是一个批量还是一个单独的文件  
                if (selection != null){  
                    if (match == ALARMS){  
                        where = "( " + selection + " )";  
                    }  
                    else{  
                        where = "( " + selection + " ) AND ";  
                    }  
                }  
                else{  
                    where = "";  
                } 
                if(uri.toString().contains(PATH_MESSAGE)){
	                if (match == ALARMS_ID){  
	                    // 如果你传递的是一个单独的文件，也就是Uri后面添加了/item的，那么在这里把该值与数据库中的属性段进行比较，返回sql语句中的where  
	                    String segment = uri.getPathSegments().get(1);  
	                    long rowId = Long.parseLong(segment);  
	                    where += " ( " + DatabaseColume.MessageInfo.ID + " = " + rowId + " ) ";  
	                }  
	                count = db.delete(DatabaseColume.MessageInfo.TABLENAME, where, selectionArgs);  
                }else if(uri.toString().contains(PATH_APP)){
                	if (match == ALARMS_ID){  
	                    // 如果你传递的是一个单独的文件，也就是Uri后面添加了/item的，那么在这里把该值与数据库中的属性段进行比较，返回sql语句中的where  
	                    String segment = uri.getPathSegments().get(1);  
	                    long rowId = Long.parseLong(segment);  
	                    where += " ( " + DatabaseColume.AppInfo.PKGNAME + " = " + rowId + " ) ";  
	                }  
	                count = db.delete(DatabaseColume.AppInfo.TABLENAME, where, selectionArgs); 
                }
                break;  
            default:  
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);  
        }  
        getContext().getContentResolver().notifyChange(uri, null);  
        return count;  
    }  
  
    @Override  
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){  
        // 基本同上了  
        SQLiteDatabase db = mDB.getWritableDatabase();  
  
        int count;  
        long rowId = 0;  
  
        int match = sURIMatcher.match(uri);  
        switch (match){  
            case ALARMS:  
            case ALARMS_ID:{  
                String myWhere;  
                if (selection != null){  
                    if (match == ALARMS){  
                        myWhere = "( " + selection + " )";  
                    }  
                    else{  
                        myWhere = "( " + selection + " ) AND ";  
                    }  
                }else{  
                    myWhere = "";  
                }  
                if(uri.toString().contains(PATH_MESSAGE)){
	                if (match == ALARMS_ID){  
	                    String segment = uri.getPathSegments().get(1);  
	                    rowId = Long.parseLong(segment);  
	                    myWhere += " ( " + DatabaseColume.MessageInfo.ID + " = " + rowId + " ) ";  
	                }  
	  
	                if (values.size() > 0){  
	                    count = db.update(DatabaseColume.MessageInfo.TABLENAME, values, myWhere, selectionArgs);  
	                }else{  
	                    count = 0;  
	                }  
                }else if(uri.toString().contains(PATH_AD)){
                	 if (match == ALARMS_ID){  
 	                    String segment = uri.getPathSegments().get(1);  
 	                    rowId = Long.parseLong(segment);  
 	                    myWhere += " ( " + DatabaseColume.AdInfo.ID + " = " + rowId + " ) ";  
 	                }  
 	  
 	                if (values.size() > 0){  
 	                    count = db.update(DatabaseColume.AdInfo.TABLENAME, values, myWhere, selectionArgs);  
 	                }else{  
 	                    count = 0;  
 	                }  
                }else {
	               	 if (match == ALARMS_ID){  
		                    String segment = uri.getPathSegments().get(1);  
		                    rowId = Long.parseLong(segment);  
		                    myWhere += " ( " + DatabaseColume.AppInfo.PKGNAME + " = " + rowId + " ) ";  
		                }  
		  
		                if (values.size() > 0){  
		                    count = db.update(DatabaseColume.AppInfo.TABLENAME, values, myWhere, selectionArgs);  
		                }else{  
		                    count = 0;  
		                }  
               }
                break;  
            }  
            default:{  
                throw new UnsupportedOperationException("Cannot update URI: " + uri);  
            }  
        }  
        getContext().getContentResolver().notifyChange(uri, null);  
  
        return count;  
    }  
  
    @Override  
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){  
        SQLiteDatabase db = mDB.getReadableDatabase();  
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder(); //SQLiteQueryBuilder是一个构造SQL查询语句的辅助类  
  
        int match = sURIMatcher.match(uri);  
        switch (match){  
            case ALARMS:{  
            	if(uri.toString().contains(PATH_MESSAGE)){
            		qb.setTables(DatabaseColume.MessageInfo.TABLENAME);  
            	}else if(uri.toString().contains(PATH_AD)){
            		qb.setTables(DatabaseColume.AdInfo.TABLENAME);  
            	}else{
            		qb.setTables(DatabaseColume.AppInfo.TABLENAME); 
            	}
                break;  
            }  
            case ALARMS_ID:{ 
            	if(uri.toString().contains(PATH_MESSAGE)){
	                qb.setTables(DatabaseColume.MessageInfo.TABLENAME);  
	                qb.appendWhere(DatabaseColume.MessageInfo.ID + "=");  
	                qb.appendWhere(uri.getPathSegments().get(1)); 
            	}else if(uri.toString().contains(PATH_AD)){
            		qb.setTables(DatabaseColume.AdInfo.TABLENAME);  
	                qb.appendWhere(DatabaseColume.AdInfo.ID + "=");  
	                qb.appendWhere(uri.getPathSegments().get(1)); 
            	}else{
            		qb.setTables(DatabaseColume.AppInfo.TABLENAME);
            		qb.appendWhere(DatabaseColume.AppInfo.PKGNAME + "=");  
 	                qb.appendWhere(uri.getPathSegments().get(1)); 
            	}
                break;  
            }  
            default:  
                throw new IllegalArgumentException("Unknown URI: " + uri);  
        }  
        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);  
  
        if (ret != null){  
            ret.setNotificationUri(getContext().getContentResolver(), uri);             
        }        
        return ret;  
    }

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}
 
}
