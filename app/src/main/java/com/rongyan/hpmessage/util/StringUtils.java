package com.rongyan.hpmessage.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.R.integer;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;

public class StringUtils {

	private final static String TAG="StringUtils";
	
	public static SimpleDateFormat date_Formater = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
     * 可以获取后退N天的日期
     */
    public static String getStrDate(int backDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,backDay);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String back = sdf.format(calendar.getTime());
        return back;
    }
	
    public static long getSystemTime() {
		long unixTimestamp = System.currentTimeMillis() / 1000;
		return unixTimestamp;
	}
    
    public static String getSystemTime2() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
    	java.util.Date curDate = new java.util.Date(System.currentTimeMillis());
        return formatter.format(curDate);
	}
    
    public static String getSystemDate(){
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ENGLISH);     
    	String date =sDateFormat.format(new java.util.Date()); 
    	return date;
    }
    
    public static String getLocalMacAddress() {  
	   String macSerial = null;
       String str = "";
       try {
               Process pp = Runtime.getRuntime().exec(
                               "cat /sys/class/net/wlan0/address ");
               InputStreamReader ir = new InputStreamReader(pp.getInputStream());
               LineNumberReader input = new LineNumberReader(ir);

               for (; null != str;) {
                       str = input.readLine();
                       if (str != null) {
                               macSerial = str.trim();// 去空格
                               break;
                       }
               }
       } catch (IOException ex) {
               // 赋予默认值
               ex.printStackTrace();
       }
       return macSerial;
    }
    
    public static String getTcp_snd(int uid) {  
 	   String macSerial = null;
        String str = "";
        String runString="cat /proc/uid_stat/"+uid+"/tcp_snd";
        try {
            Process pp = Runtime.getRuntime().exec(runString);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        if(macSerial!=null){
        	LogUtils.w("PANZHIHUA", "Tcp_snd:"+uid+":"+macSerial);
        }else{
        	macSerial="0";
        }
        return macSerial;
     }
    
    public static String getTcp_rcv(int uid) {  
  	   String macSerial = null;
         String str = "";
         String runString="cat /proc/uid_stat/"+uid+"/tcp_rcv";
         try {
             Process pp = Runtime.getRuntime().exec(runString);
             InputStreamReader ir = new InputStreamReader(pp.getInputStream());
             LineNumberReader input = new LineNumberReader(ir);

             for (; null != str;) {
                 str = input.readLine();
                 if (str != null) {
                     macSerial =str.trim();// 去空格
                     break;
                 }
             }
         } catch (IOException ex) {
             // 赋予默认值
             ex.printStackTrace();
         }
         if(macSerial!=null){
         	LogUtils.w("PANZHIHUA", "getTcp_rcv:"+uid+":"+macSerial);
         }else{
         	macSerial="0";
         }
         return macSerial;
      }
    
    public static void getTraffic(Context context) {  
    	//获取到配置权限信息的应用程序  
        PackageManager pms = context.getPackageManager();;  
        List<PackageInfo> packinfos = pms  
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);  
        for(PackageInfo packinfo : packinfos){  
            //获取该应用的所有权限信息  
            String[] permissions = packinfo.requestedPermissions;  
            if(permissions!=null&&permissions.length>0){  
                for(String permission : permissions){  
                    //筛选出具有Internet权限的应用程序  
                    if("android.permission.INTERNET".equals(permission)){                        
                        //获取到应用的uid（user id）  
                        int uid = packinfo.applicationInfo.uid; 
//                        LogUtils.w("PANZHIHUA", packinfo.packageName);
                        //TrafficStats对象通过应用的uid来获取应用的下载、上传流量信息  
                        getTcp_snd(uid); 
                        getTcp_rcv(uid); 
                    }  
                }  
            }  
        } 
    }
    
    public static int getDev() {
    	int traffic=0;
    	String macSerial = null;
        String str = "";
        String runString="cat /proc/net/dev";
        try {
            Process pp = Runtime.getRuntime().exec(runString);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                	String[] parts = str.split(" ");
                	for(int j=0;j<parts.length;j++){
                		if(parts[j]!=null&&!parts[j].equals("")){
                			int a=0;
                			try {
                				if(parts[j].matches("-?\\d+")){
                			     a= Integer.parseInt(parts[j]);
                				}
                			} catch (NumberFormatException e) {
                			    e.printStackTrace();
                			} finally{
                				traffic=traffic+a;
                			}
                		}
                	}
                    macSerial = str.trim()+"\n"+macSerial;// 去空格
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
       return traffic;
    }
  
}
