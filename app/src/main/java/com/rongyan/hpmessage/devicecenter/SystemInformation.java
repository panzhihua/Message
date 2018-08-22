package com.rongyan.hpmessage.devicecenter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.rongyan.hpmessage.util.FormatUtil;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;

@SuppressLint("NewApi")
public class SystemInformation {
	static Context mContext;
	private static long total, idle;
	double usage = 0;
	private static SystemInformation mSystemInformation = null;
	static long mTotalStorage;
	private static OnlineOpera mOpera;
	static ConcurrentHashMap<String, Float> mPkgSizeMap = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, AppCpu> mPkgCpuMap = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, Float> mPkgCpuValueMap = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, Integer> mPkgMemoryMap = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, Integer> mPkgSizePercentageMap = new ConcurrentHashMap<>();
	static ConcurrentHashMap<String, Integer> mPkgMemoryPercentageMap = new ConcurrentHashMap<>();
	ActivityManager mActivityManager;
	StatFs mDataStafsFs;
	private int mCurrentSystemCpuUsed = 0;
	private long lastSystemNetworkTrafficIn=0,lastSystemNetworkTrafficOut=0;

	public void setOnlineOpera(OnlineOpera opera) {
		mOpera = opera;
	}

	public static SystemInformation getInstance(Context context) {
		if (mSystemInformation == null) {
			mSystemInformation = new SystemInformation(context);
		}
		return mSystemInformation;
	}

	public SystemInformation(Context context) {
		mContext = context;
		mActivityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		mDataStafsFs = new StatFs(Environment.getDataDirectory().getPath());
	}
	
	/**
	   * 计算已使用内存的百分比，并返回。
	   */
	public int getUsedPercentValue() {
	    long totalMemorySize = getTotalMemory();
	    long availableSize = getAvailableMemory() / 1024;
	    int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
	    return percent;
	  }
	  /**
	   * 获取当前可用内存，返回数据以字节为单位。
	   */
	  public long getAvailableMemory() {
	    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
	    mActivityManager.getMemoryInfo(mi);
	    return mi.availMem;
	  }
	  /**
	   * 获取系统总内存,返回字节单位为KB
	   * @return 系统总内存
	   */
	  public static long getTotalMemory() {
	    long totalMemorySize = 0;
	    String dir = "/proc/meminfo";
	    try {
	      FileReader fr = new FileReader(dir);
	      BufferedReader br = new BufferedReader(fr, 2048);
	      String memoryLine = br.readLine();
	      String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
	      br.close();
	      //将非数字的字符替换为空
	      totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return totalMemorySize;
	  }

	/**
	 * 获取内存使用率
	 * 
	 * @return 返回内存使用率
	 */
	public int getSystemUsedMemory() {
		MemoryInfo mi = new MemoryInfo();
		mActivityManager.getMemoryInfo(mi);
		return getUsedPercentage(mi.totalMem, mi.availMem, "KB");
	}

	public long getSystemTotalMemory() {
		MemoryInfo mi = new MemoryInfo();
		mActivityManager.getMemoryInfo(mi);
		return mi.totalMem;// 返回byte
	}

	/**
	 * 更新所有动律应用的内存使用率
	 */

	public void updateAppsUsedMemoryPercentage() {
		for (Entry<String, Integer> info : mPkgMemoryMap.entrySet()) {
			int value = updateMemoryPercentage(info.getValue());
			mPkgMemoryPercentageMap.put(info.getKey(), value);
		}
	}

	/**
	 * 获取单个应用的内存占有率
	 * 
	 * @param pkgname
	 *            应用的包名
	 * @return 应用的内存占有率
	 */

	public int getCurrentAppUsedMemoryPercentage(String pkgname) {
		if (mPkgMemoryPercentageMap.get(pkgname) != null) {
			return mPkgMemoryPercentageMap.get(pkgname);
		}
		return 0;
	}

	/**
	 * 计算每个应用的内存占用率
	 * 
	 * @param value
	 *            内存大小 单位 MB 乘以1000 放大1000倍
	 * @return 内存占有率百分比
	 */

	private int updateMemoryPercentage(int value) {
		long usedvalue = value*100;
		long totalvalue = getSystemTotalMemory() / 1024 / 1024;
		if(totalvalue==0L){
			return FormatUtil.FormatIntSize(usedvalue / 1);
		}
		return FormatUtil.FormatIntSize(usedvalue / totalvalue);
	}

	public int getUsedPercentage(final long totalsize, final long availsize,
			String type) {
		float avilvalue = FormatUtil.formatFileSize(mContext, availsize, type);
		float totalvalue = FormatUtil.formatFileSize(mContext, totalsize, type);
		return (int) (FormatUtil.round((1 - avilvalue / totalvalue), 2) * 100);
	}

	private static float getFloatValue(long value) {
		float result = value;
		result = result / 1024 / 1024 / 1024;
		// return (float) (Math.round(result * 100)) / 100;
		return FormatUtil.round(result, 2);
	}

	/**
	 * 更新app应用使用的内存大小
	 * 
	 * @param pkgname
	 *            包名
	 * @param memorySize
	 *            大小 单位MB
	 */
	public void putAppMemoryValue(String pkgname, int memorySize) {
		synchronized (mPkgMemoryMap) {
			mPkgMemoryMap.put(pkgname, memorySize);
		}
	}

	/**
	 * 获取单个应用内存使用大小 单位MB
	 * 
	 * @param pkgname
	 *            传入报名
	 * @return 返回当前包所占的大小 但是MB
	 */

	public int getCurrentAppMemoryValue(String pkgname) {
		synchronized (mPkgMemoryMap) {
			if (mPkgMemoryMap.get(pkgname) != null) {
				return mPkgMemoryMap.get(pkgname);
			}
			return 0;
		}
	}

	/**
	 * 获取当前系统的CPU使用率
	 * 
	 * @return
	 */

	public int getCurrentSystemUsedCpu() {
		return mCurrentSystemCpuUsed;
	}

	/**
	 * 更新CPU使用率
	 * 
	 */

	public void updateSystemUsedCpu() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"/proc/stat"), 1000);
			String load = reader.readLine();
			reader.close();
			String[] cpuInfos = load.split(" ");
			long currTotal = Long.parseLong(cpuInfos[2])
					+ Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
					+ Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
					+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
			long currIdle = Long.parseLong(cpuInfos[5]);
			usage = ((currTotal - total) - (currIdle - idle)) * 100.0f
					/ (currTotal - total);
			this.total = currTotal;
			this.idle = currIdle;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		mCurrentSystemCpuUsed = FormatUtil.formatDoubleToFloat(usage);
	}

	/**
	 * data 分区使用率 内部存储使用率
	 * 
	 * @return
	 */

	public int getSystemUsedDataDisk() {
		return getUsedPercentage(getSDCardTotalSize(),
				getSDCardAvailableSize() , "MB");
	}
	
	/** 
     * @return 返回sd卡总共的容量 
     * */  
    @SuppressLint("NewApi")  
    public static long getSDCardTotalSize() {  
        long size = 0;  
        if (isSDCardMounted()) {  
            StatFs statFs = new StatFs(getSDCardBasePath());  
            if (Build.VERSION.SDK_INT >= 18) {  
                size = statFs.getTotalBytes();  
            } else {  
                size = statFs.getBlockCount() * statFs.getBlockSize();  
            }  
            return size;  
        }  
        return 0;  
    }  
    
    /** 
     * @return 返回sd卡可用的容量 
     * */  
    @SuppressLint("NewApi")  
    public static long getSDCardAvailableSize() {  
        long size = 0;  
        if (isSDCardMounted()) {  
            StatFs statFs = new StatFs(getSDCardBasePath());  
            if (Build.VERSION.SDK_INT >= 18) {  
                size = statFs.getAvailableBytes();  
            } else {  
                size = statFs.getAvailableBlocks() * statFs.getBlockSize();  
            }  
            return size;  
        }  
        return 0;  
    }  
    
    /** 
     * @des 判断sd卡是否装载 
     *  
     * @return true：已经被装载可以使用 
     * */  
  
    public static boolean isSDCardMounted() {  
        return Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED);  
    }  
  
    /** 
     * @des 获得sdcard的根目录 
     *  
     * @return sd卡根目录地址 
     * */  
    public static String getSDCardBasePath() {  
        if (isSDCardMounted()) {  
            return Environment.getExternalStorageDirectory().getAbsolutePath();  
        } else {  
            return null;  
        }  
    }  

	public static String[] getInputStreamStrings(String filename) {
		String[] currentStrings = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename)), 1000);
			String load = reader.readLine();
			reader.close();
			currentStrings = load.split(" ");
		} catch (IOException e) {
		}
		return currentStrings;

	}

	/**
	 * 获取对应APP的CPU使用率
	 * 
	 * @param pkgname
	 *            应用的包名
	 * @return app的cpu使用率
	 */

	public float getCurrentAppCpuTime(String pkgname) {
		synchronized (mPkgCpuValueMap) {
			if (mPkgCpuValueMap.get(pkgname) != null) {
				return mPkgCpuValueMap.get(pkgname);
			}
			return 0f;
		}

	}

	/**
	 * 更新APP cpu使用率
	 * 
	 * @param pkgname
	 *            对应的包名
	 * @param pid
	 *            进程的pid
	 */

	public void updateAppCpuTime(String pkgname, int pid) {
		synchronized (mPkgCpuValueMap) {
			if (mPkgCpuMap.get(pkgname) == null) {
				AppCpu appCpu = new AppCpu(pid, pkgname);
				mPkgCpuMap.put(pkgname, appCpu);
			}
			Float value = mPkgCpuMap.get(pkgname).updateAppPidUsedCpu();
			mPkgCpuValueMap.put(pkgname, value);
		}
	}

	class AppCpu {
		private long processAppCpuTimeF, totalCpuTimeF;
		int mPid;
		String mPkgName;

		AppCpu(int pid, String pkgname) {
			mPid = pid;
			mPkgName = pkgname;
		}

		public float updateAppPidUsedCpu() {
			float usedpercentage = 0;
			String[] appCpuInfos = getInputStreamStrings("/proc/" + mPid
					+ "/stat");
			String[] totalCpuInfo = getInputStreamStrings("/proc/stat");

			long currentAppCpuTime = 0;
			long currentTotalTime = 0;
			try {
				if ((appCpuInfos != null && appCpuInfos.length > 17) && (totalCpuInfo != null && totalCpuInfo.length > 9)) {
					currentAppCpuTime = Long.parseLong(appCpuInfos[13])
							+ Long.parseLong(appCpuInfos[14])
							+ Long.parseLong(appCpuInfos[15])
							+ Long.parseLong(appCpuInfos[16]);

					currentTotalTime = Long.parseLong(totalCpuInfo[2])
							+ Long.parseLong(totalCpuInfo[3])
							+ Long.parseLong(totalCpuInfo[4])
							+ Long.parseLong(totalCpuInfo[6])
							+ Long.parseLong(totalCpuInfo[5])
							+ Long.parseLong(totalCpuInfo[7])
							+ Long.parseLong(totalCpuInfo[8]);
					usedpercentage = 100
							* (currentAppCpuTime - processAppCpuTimeF)
							/ (currentTotalTime - totalCpuTimeF);
					processAppCpuTimeF = currentAppCpuTime;
					totalCpuTimeF = currentTotalTime;
				}

			} catch (ArrayIndexOutOfBoundsException e) {

			} catch (Exception e) {
				e.printStackTrace();
			}
			return usedpercentage;
		}
	}

	/**
	 * 获取系统网络流量大小
	 * 
	 * @return 获取系统网络流量大小
	 */

	public float getSystemNetworkTraffic() {
		long count = TrafficStats.getTotalTxBytes()
				+ TrafficStats.getTotalRxBytes();
		return FormatUtil.formatFileSize(mContext, count, "MB");
	}
	
	public int getSystemNetworkTrafficIn() {
		long count =TrafficStats.getTotalRxBytes()-lastSystemNetworkTrafficIn;
		lastSystemNetworkTrafficIn=TrafficStats.getTotalRxBytes();
		return (int)FormatUtil.formatFileSize(mContext, count, "KB");
	}
	
	public int getSystemNetworkTrafficOut() {
		long count = TrafficStats.getTotalTxBytes()-lastSystemNetworkTrafficOut;
		lastSystemNetworkTrafficOut=TrafficStats.getTotalTxBytes();
		return (int)FormatUtil.formatFileSize(mContext, count, "KB");
	}

	/**
	 * 获取当前应用存储大小
	 * 
	 * @param pkgname
	 *            包名
	 * @return 包的大小 单位MB
	 */

	public Float getCurrentPkgSize(String pkgname) {
		if (mPkgSizeMap.get(pkgname) != null) {
			return mPkgSizeMap.get(pkgname);
		}
		return 0f;
	}

	public void updateAppsPkgSizePercentage() {
		for (Map.Entry<String, Float> info : mPkgSizeMap.entrySet()) {
			int percent = updateAppPkgSizePercentage(info.getValue());
			mPkgSizePercentageMap.put(info.getKey(), percent);
		}
	}

	private int updateAppPkgSizePercentage(Float value) {
		long usedvalue = (long) (value * 100);
		long totalvalue = mDataStafsFs.getTotalBytes() / 1024 / 1024;
		int percent = FormatUtil.FormatIntSize(usedvalue / totalvalue);
		if (percent == 0) {
			percent = 1;
		}
		return percent;
	}

	/**
	 * 获取应用的磁盘占有率
	 * 
	 * @param pkgname
	 *            应用的包名
	 * @return 返回磁盘占有率
	 */

	public int getCurrentPkgSizePercentage(String pkgname) {
		if (mPkgSizePercentageMap.get(pkgname) != null) {
			return mPkgSizePercentageMap.get(pkgname);
		}
		return 0;
	}

	/**
	 * 获取Android Native App的缓存大小、数据大小、应用程序大小
	 * 
	 * @param context
	 *            Context对象
	 * @param pkgName
	 *            需要检测的Native App包名
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void getPkgSize(final Context context, final String pkgName)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		// getPackageSizeInfo是PackageManager中的一个private方法，所以需要通过反射的机制来调用
		Method method = PackageManager.class.getMethod("getPackageSizeInfo",
				new Class[] { String.class, IPackageStatsObserver.class });
		// 调用 getPackageSizeInfo 方法，需要两个参数：1、需要检测的应用包名；2、回调
		method.invoke(context.getPackageManager(), new Object[] { pkgName,
				new IPackageStatsObserver.Stub() {
					public void onGetStatsCompleted(PackageStats pStats,
							boolean succeeded) throws RemoteException {
						synchronized (mPkgSizeMap) {
							mTotalStorage = pStats.cacheSize + pStats.dataSize
									+ pStats.codeSize;
							float cachesize = FormatUtil.formatFileSize(
									mContext, pStats.cacheSize, "MB");
							float dataSize = FormatUtil.formatFileSize(
									mContext, pStats.dataSize, "MB");
							float codeSize = FormatUtil.formatFileSize(
									mContext, pStats.codeSize, "MB");
							mPkgSizeMap.put(
									pkgName,
									(float) (Math
											.round((cachesize + dataSize + codeSize) * 100)) / 100);// ｘ小数点精确到2位
						}
					}
				} });
	}
}
