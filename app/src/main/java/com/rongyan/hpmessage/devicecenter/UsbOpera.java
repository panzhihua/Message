package com.rongyan.hpmessage.devicecenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
public class UsbOpera {
	
	private Context mContext;
	private DeviceCfg mDeviceCfg;
	private static UsbOpera mUsbOpera;
	private UsbManager mUsbManager;
	public static UsbOpera getInstance(Context context,DeviceCfg cfg,Application application){
		if(mUsbOpera == null){
			mUsbOpera = new UsbOpera(context,cfg,application);
		}
		return mUsbOpera;
	}
	
    public UsbOpera(Context context,DeviceCfg cfg,Application application) {
    	mContext = context;
    	mDeviceCfg = cfg;
    	mUsbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
	}

    @SuppressLint("NewApi")
	public void getUsbInfo(int time){
		String usbMessage="";
		int i=0;
		mDeviceCfg.setUSB_1(0);
		mDeviceCfg.setUSB_2(0);
		mDeviceCfg.setUSB_3(0);
		mDeviceCfg.setUSB_4(0);
		HashMap<String,UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
        Iterator<UsbDevice> iterator = deviceHashMap.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = iterator.next();
            if(!usbMessage.equals("")){
            	usbMessage=usbMessage+" & ";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            	usbMessage=usbMessage+device.getManufacturerName()+"|"+device.getProductName();
            }           
            if(detectionUSB(device.getVendorId(),device.getProductId())){
            	continue;
            }
            if(i==0){
            	mDeviceCfg.setUSB_1(1);
            }else if(i==1){
            	mDeviceCfg.setUSB_2(1);
            }else if(i==2){
            	mDeviceCfg.setUSB_3(1);
            }else if(i==3){
            	mDeviceCfg.setUSB_4(1);
            }
            i++;
        }
	}
    
    public boolean detectionUSB(int Vid,int Pid){
		List<Integer> vidList = Arrays.asList(8183,2965,13398,1061);
		List<Integer> pidList = Arrays.asList(19,30507,4658,33113);
		for(int i=0;i<vidList.size();i++){
			if(vidList.get(i)==Vid&&pidList.get(i)==Pid){
				return true;
			}
		}
		return false;
	}
}
