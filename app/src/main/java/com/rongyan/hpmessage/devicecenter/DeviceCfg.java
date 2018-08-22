package com.rongyan.hpmessage.devicecenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

public class DeviceCfg {
	
	private static final String SHARED_FILE_NAME = "devicefile";
	private static final String SYSTEM_KERNEL_VERSION_STRING = "kernelversion";
	private Context mContext;
	private boolean mNetWorkEnable = false;
	private int mEthernetEnable = 0;
	private int USB_1=0;
	private int USB_2=0;
	private int USB_3=0;
	private int USB_4=0;
	private String entity_idString;
	private SharedPreferences mPreferences;

	public DeviceCfg(Context context) {
		mContext = context;
	}

	public void init() {
		mPreferences = mContext.getSharedPreferences(SHARED_FILE_NAME,
				Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_WRITEABLE);
	}
	public SharedPreferences getmPreferences() {
		return mPreferences;
	}

	private void writePerferencesValue(String key, int value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	private int getPerferencesIntValue(String key){
		return mPreferences.getInt(key, -1);
	}

	private String getPerferencesStringValue(String key) {
		return mPreferences.getString(key, "");
	}

	private void writePerferencesValue(String key, String value) {
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void setNetWorkEnable(boolean enable) {
		mNetWorkEnable = enable;
	}

	public boolean getNetWorkEnable() {
		return mNetWorkEnable;
	}
	
	public void setEthernetEable(int state){
		mEthernetEnable = state;
	}
	
	public int getEthernetState(){
		return mEthernetEnable;
	}

	public void setKernelVersion(String value) {
		writePerferencesValue(SYSTEM_KERNEL_VERSION_STRING, value);
	}

	public String getKernelVersion() {
		return getPerferencesStringValue(SYSTEM_KERNEL_VERSION_STRING);
	}
	
	public int getUSB_1() {
        return USB_1;
    }

    public void setUSB_1(int USB_1) {
        this.USB_1 = USB_1;
    }

    public int getUSB_2() {
        return USB_2;
    }

    public void setUSB_2(int USB_2) {
        this.USB_2 = USB_2;
    }

    public int getUSB_3() {
        return USB_3;
    }

    public void setUSB_3(int USB_3) {
        this.USB_3 = USB_3;
    }

    public int getUSB_4() {
        return USB_4;
    }

    public void setUSB_4(int USB_4) {
        this.USB_4 = USB_4;
    }
 
    public String getEntity_idString() {
		return entity_idString;
	}

	public void setEntity_idString(String entity_idString) {
		this.entity_idString = entity_idString;
	}

	/**
	 * 判断应用是否为install应用
	 * @param 获取到的 ApplicationInfo
	 * @return 如果为系统应用返回false 安装的应用则返回true
	 */
	public static boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_SYSTEM) > 0 || ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) > 0)) {			
			return false;
		}
		return true;
	}
}
