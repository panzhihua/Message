package com.rongyan.hpmessage.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import com.rongyan.hpmessage.item.Apps;
import com.rongyan.hpmessage.item.SystemDataItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;


public class ApplicationUtils {

	private static final String TAG = "ApplicationUtils";

	private static boolean mNetWorkEnable;
	private static final String SHARED_FILE_NAME = "message";
	public static final String ACTIVITIES_TIME_STRING = "activities_time";

	public final static String CACHE = "rongyan";	
	public static final String OPEN_NUMBER_STRING = "open_number";//主动弹出次数
	public static final String DAY_STRING = "day";//主动弹出日期
	
	public static int collection_duration=0;// 本地采集数据的间隔
	
	public static int report_duration=0;// 数据上报的间隔
	
	public static int heartbeat_duration=0;// 新接口数据上报的间隔
	
	public static String mUUID = "";// 机器唯一编码

	public static String mDeviceToken=null;//devicetoken

    private static String mBROKER = "";//渠道商代码，如 二维火 -> EWH

    private static String mMODEL = "";//设备型号

    private static String mVERSION = "";//软件版本

	private static String mFireShopId = "0";// 主收银店铺名称

	private static String mRetailFireShopIdString = "0";// 零售店铺名称

	private static String mOrderDeskFireShopIdString = "0";// 副收银店铺名称

	private Context mContext;

	private SharedPreferences mPreferences;

	private static ApplicationUtils mApplicationUtils;
	
	public static ApplicationUtils getIntance(Context context){
	    if(mApplicationUtils == null){
	    	mApplicationUtils = new ApplicationUtils(context);
	    }
	    return mApplicationUtils;
	}

	public ApplicationUtils(Context context) {
		mContext = context;
		mPreferences = mContext.getSharedPreferences(SHARED_FILE_NAME,
				Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_WRITEABLE);
	}
	
	public static String getAppVersion(Context context){  
		String localVersion = "";
        try {
            localVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
	}

	public static boolean ismNetWorkEnable() {
		return mNetWorkEnable;
	}

	public static void setmNetWorkEnable(boolean mNetWorkEnable) {
		ApplicationUtils.mNetWorkEnable = mNetWorkEnable;
	}
	
	public void writePerferencesValue(String key, int value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	public int getPerferencesIntValue(String key){
		return mPreferences.getInt(key, 0);
	}

	public String getPerferencesStringValue(String key) {
		return mPreferences.getString(key, "");
	}
	
	public Boolean getPerferencesBooleanValue(String key) {
		return mPreferences.getBoolean(key, true);
	}

	public void writePerferencesValue(String key, String value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
    public static boolean compareVersion(String s1, String s2) {
    	try{
	    	if(s1!=null&&s2!=null){
	    		Log.w(TAG,s1+"==="+s2);
		        int replace1 = Integer.valueOf(s1.replace(".", "0"));
		        int replace2 = Integer.valueOf(s2.replace(".", "0"));
		        Log.w(TAG,replace1+"==="+replace2);
		        if(replace1<replace2){
		        	return true;
		        }else{
		        	return false;
		        } 
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public static boolean compareTime(String s1, String s2,String s3) {
    	try{
	    	if(s1!=null&&s2!=null&&s3!=null){
		        int replace1 = Integer.valueOf(s1.replace(":", "0"));
		        int replace2 = Integer.valueOf(s2.replace(":", "0"));
		        int replace3 = Integer.valueOf(s3.replace(":", "0"));		      
		        if(replace1<replace2&&replace2<replace3){
		        	return true;
		        }else{
		        	return false;
		        } 
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
	 
	 public static boolean isMainThread() {
		    return Looper.getMainLooper().getThread() == Thread.currentThread();
	 }

	public static String getmUUID() {
		return mUUID;
	}

	public static void setmUUID(String mUUID) {
		ApplicationUtils.mUUID = mUUID;
	}

	public static String getmBROKER() {
		return mBROKER;
	}

	public static void setmBROKER(String mBROKER) {
		ApplicationUtils.mBROKER = mBROKER;
	}

	public static String getmMODEL() {
		return mMODEL;
	}

	public static void setmMODEL(String mMODEL) {
		ApplicationUtils.mMODEL = mMODEL;
	}

	public static String getmVERSION() {
		return mVERSION;
	}

	public static void setmVERSION(String mVERSION) {
		ApplicationUtils.mVERSION = mVERSION;
	}
	 
	 /**
     * 获取UUID
     */

    public static String getUUID() {
        if (mUUID == null || mUUID.equals("0")|| mUUID.equals("")) {
            mUUID = getProperty("ro.aliyun.clouduuid", "0");
        }
        return mUUID;
    }

    /**
     * 获取 属性值
     *
     * @param key
     *            属性key
     * @param defaultValue
     *            默认值
     * @return 如果获取不到 就返回默认值
     */

    @SuppressWarnings({ "finally", "unused" })
    private static String getProperty(final String key,
                                      final String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

	/**
	 * 获取收银版本二维火店铺ID
	 *
	 * @return
	 */
	public static String getentityId() {
		try {
			if (mFireShopId == null || mFireShopId.equals("0")) {
				String content = ""; // 文件内容字符串
				File file = new File("/mnt/sdcard/zmcash/system_data.txt");
				if (file.exists()) {
					try {
						InputStream instream = new FileInputStream(file);
						if (instream != null) {
							InputStreamReader inputreader = new InputStreamReader(
									instream);
							BufferedReader buffreader = new BufferedReader(
									inputreader);
							String line;
							// 分行读取
							while ((line = buffreader.readLine()) != null) {
								content += line;
							}
							instream.close();
							if (!content.equals("")) {
								SystemDataItem item = (SystemDataItem) JsonUtils
										.jsonToBean(content, SystemDataItem.class);
								mFireShopId = item.getEntity_id();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// 如果取不到二维火店铺ENTRYID 则需要取副收银端的ENTRYID
			if (mFireShopId == null || mFireShopId.equals("0")) {
				mFireShopId = getOrderDeskHEntityId();
			}
			// 如果取不到副收银店铺ENTRYID 则需要取零售端的ENTRYID
			if (mFireShopId == null || mFireShopId.equals("0")) {
				mFireShopId = getRetailEntityId();
			}
			if (mFireShopId == null) {
				mFireShopId = "0";
			}
			LogUtils.w(TAG, "mFireShopId=" + mFireShopId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mFireShopId;
	}

	/**
	 * 获取零售版本二维火店铺ID
	 *
	 * @return
	 */

	public static String getRetailEntityId() {
		if (mRetailFireShopIdString == null
				|| mRetailFireShopIdString.equals("0")) {
			String content = ""; // 文件内容字符串
			File file = new File(
					"/mnt/sdcard/2dfire_retail_cashdesk/system_data.txt");
			if (file.exists()) {
				try {
					InputStream instream = new FileInputStream(file);
					if (instream != null) {
						InputStreamReader inputreader = new InputStreamReader(
								instream);
						BufferedReader buffreader = new BufferedReader(
								inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							content += line;
						}
						instream.close();
						if (!content.equals("")) {
							SystemDataItem item = (SystemDataItem) JsonUtils
									.jsonToBean(content, SystemDataItem.class);
							mRetailFireShopIdString = item.getEntity_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LogUtils.w(TAG, "mRetailFireShopIdString=" + mRetailFireShopIdString);
		return mRetailFireShopIdString;
	}

	/**
	 * 获取二维火副收银店铺ID
	 */
	public static String getOrderDeskHEntityId() {
		if (mOrderDeskFireShopIdString == null
				|| mOrderDeskFireShopIdString.equals("0")) {
			String content = ""; // 文件内容字符串
			File file = new File(
					"/mnt/sdcard/zm_orderDeskH/system_data.txt");
			if (file.exists()) {
				try {
					InputStream instream = new FileInputStream(file);
					if (instream != null) {
						InputStreamReader inputreader = new InputStreamReader(
								instream);
						BufferedReader buffreader = new BufferedReader(
								inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							content += line;
						}
						instream.close();
						if (!content.equals("")) {
							SystemDataItem item = (SystemDataItem) JsonUtils
									.jsonToBean(content, SystemDataItem.class);
							mOrderDeskFireShopIdString = item.getEntity_id();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LogUtils.w(TAG, "mOrderDeskFireShopIdString=" + mOrderDeskFireShopIdString);
		return mOrderDeskFireShopIdString;
	}
    
    /**
     * 判断是否有下载空间
     */
    public static boolean isDownLoad(Apps app){
        if(FileUtils.getAvailableSize()-app.getPackage_size()>100*1024*1024){
            return true;
        }
        return false;
    }
}
