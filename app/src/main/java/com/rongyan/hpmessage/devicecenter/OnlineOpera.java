package com.rongyan.hpmessage.devicecenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.OnDataBaseListener;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.devicecenter.ApplicationState.Callbacks;
import com.rongyan.hpmessage.devicecenter.ApplicationState.Session;
import com.rongyan.hpmessage.item.StateItem;
import com.rongyan.hpmessage.util.FormatUtil;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.StringUtils;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class OnlineOpera implements Callbacks{
	private SystemInformation mSystemInformation;
	private Context mContext;
	private DeviceCfg mDeviceCfg;
	private static OnlineOpera mOnlineOpera;
	private ApplicationState mState;
	private Session mSession;
	private MessageApplication mApplication;
	private Map<String, PackageInfo> mPackageInfos;
	private DataBaseOpenHelper mDataBaseOpenHelper;
	public static final int UPDATE_APP_TOTAL_STORAGE = 1;
	Float mCurrentStorageFloat = 0f;

	public static OnlineOpera getInstance(Context context,DeviceCfg cfg,Application application){
		if(mOnlineOpera == null){
			mOnlineOpera = new OnlineOpera(context,cfg,application);
		}
		return mOnlineOpera;
	}
	
    public OnlineOpera(Context context,DeviceCfg cfg,Application application) {
    	mApplication = MessageApplication.getInstance();
		mState = ApplicationState.getInstance(mApplication);
		mDataBaseOpenHelper=DataBaseOpenHelper.getInstance(context);
		mSession = mState.newSession(this);
    	mContext = context;
    	mSystemInformation = SystemInformation.getInstance(context); 
        mSystemInformation.setOnlineOpera(this);
    	mDeviceCfg = cfg;
	}

    @SuppressLint("NewApi") 
    public StateItem getOnlineItem(){
    	StateItem stateItem=new StateItem();
    	StateItem.Device_State deviceState=new StateItem.Device_State();
    	deviceState.setCpu_usage(mSystemInformation.getCurrentSystemUsedCpu());
    	deviceState.setMemory_usage(mSystemInformation.getSystemUsedMemory());
    	deviceState.setDisk_usage(mSystemInformation.getSystemUsedDataDisk());
    	deviceState.setNet_out(mSystemInformation.getSystemNetworkTrafficOut());
    	deviceState.setNet_in(mSystemInformation.getSystemNetworkTrafficIn());
    	deviceState.setNet_stat(mDeviceCfg.getEthernetState());
    	deviceState.setUsb1_stat(mDeviceCfg.getUSB_1());
    	deviceState.setUsb2_stat(mDeviceCfg.getUSB_2());
    	deviceState.setUsb3_stat(mDeviceCfg.getUSB_3());
    	deviceState.setUsb4_stat(mDeviceCfg.getUSB_4());
    	deviceState.setCollected_at(StringUtils.getSystemDate());
    	LogUtils.w("OnlineTask", mSystemInformation.getUsedPercentValue()+"%");
    	LogUtils.w("OnlineTask", mSystemInformation.getSystemUsedMemory()+"%");
    	LogUtils.w("OnlineTask", "=="+mSystemInformation.getSystemUsedDataDisk()+"%");
    	List<StateItem.Package_States> packageStatesList=new ArrayList<>();
    	for(Map.Entry<String, PackageInfo> info:mPackageInfos.entrySet()){
    		ApplicationInfo appInfo = info.getValue().applicationInfo;
			if(DeviceCfg.filterApp(appInfo)){
				StateItem.Package_States item = new StateItem.Package_States();
				item.setOpen_times(mDataBaseOpenHelper.getAppInfoUsedTime(info.getValue().packageName));
	    		item.setPackage_name(info.getValue().packageName);
	    		int uid=info.getValue().applicationInfo.uid;
	    		item.setVersion_code(info.getValue().versionCode);
	    		item.setVersion_name(info.getValue().versionName);
	    		item.setNet_flow(Integer.valueOf(StringUtils.getTcp_rcv(uid))+Integer.valueOf(StringUtils.getTcp_snd(uid)));
	    		float cpu = mSystemInformation.getCurrentAppCpuTime(info.getValue().packageName);
	    		item.setCpu_usage(FormatUtil.FormatIntSize(cpu));
	    		int memorysize = mSystemInformation.getCurrentAppUsedMemoryPercentage(info.getValue().packageName);
	    		item.setMemory_usage(memorysize);
	    		int diskpercentage = mSystemInformation.getCurrentPkgSizePercentage(info.getValue().packageName);
	    		item.setDisk_usage(diskpercentage);
	    		item.setCollected_at(StringUtils.getSystemDate());
	    		packageStatesList.add(item);
			}
    	}
    	stateItem.setDevice_state(deviceState);
    	stateItem.setPackage_states(packageStatesList);
    	return stateItem;
    }
    
	@Override
	public void onPackageListChanged() {
		mPackageInfos = mSession.getPackageInfos();
	}

	@Override
	public void onAddPackage(String pkgname, PackageInfo info) {
		if(mPackageInfos.get(pkgname) == null){
			mPackageInfos.put(pkgname, info);
		}
	}

	@Override
	public void onRemovePackage(String pkgname) {
        if(mPackageInfos.get(pkgname) != null){
        	mPackageInfos.remove(pkgname);
        }
	}

}
