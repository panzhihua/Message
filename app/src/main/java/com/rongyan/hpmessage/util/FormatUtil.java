/**
 * 转换类型类
 */
package com.rongyan.hpmessage.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.R.integer;
import android.content.Context;
import android.util.Log;

public class FormatUtil {
	//保留小数点两位
	public static DecimalFormat mDecimalFormat = new DecimalFormat("##0.00");  
	/**
	 * 提供精确的小数位四舍五入处理
	 * @param v 需要四舍五入的数字
	 * @param scale小数点后保留几位
	 * @return  四舍五入后的结果
	 */
    public static float round(float v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return (float) b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /**
     * long型 Bytes 类型转换为 KB MB GB
     * @param context context
     * @param number  转换的数据	
     * @param type  要转换为的类型 KB MB GB
     * @return 返回的数据
     */
    
    public static float formatFileSize(Context context, long number,String type) {
        if (context == null) {
            return 0.0f;
        }

        float result = number;
        if(type.equals("KB")){
        	 result = result / 1024;
        }else if(type.equals("MB")){
        	result = result / 1024 /1024;
        }else if(type.equals("GB")){
        	result = result / 1024 /1024/1024;
        } 
        return (float)(Math.round(result*100))/100;
    }

    /**
     * double 型转Int型
     * @param value
     * @return
     */
    
    public static int formatDoubleToFloat(Double value){
    	return Integer.parseInt(new java.text.DecimalFormat("0").format(value));
    }
    /**
     * float 转 int 四舍五入
     * @param value 传入的float值
     * @return 整形
     */
    public static int FormatIntSize(float value){
    	int i = 0;
    	if(value > 0){
    		i = (int) ((value*10 + 5)/10);
    	}else if(value < 0){
    		i = (int) ((value*10 - 5)/10);  
    	}
    	return i;
    }
    
    /**
     * @param s1 当前版本号
     * @param s2 最新版本号
     * @return true 更新 false 不更新
     */
    public static boolean compareVersion(String s1, String s2) {
    	try{
	    	if(s1!=null&&s2!=null){
		        int replace1 = Integer.valueOf(s1.replace(".", "0"));
		        int replace2 = Integer.valueOf(s2.replace(".", "0"));
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
	
}
