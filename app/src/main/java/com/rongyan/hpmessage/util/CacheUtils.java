package com.rongyan.hpmessage.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import com.rongyan.hpmessage.MessageApplication;

public class CacheUtils {

    /**
     * 缓存boolean类型数值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 得到boolean类型数值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 缓存String类型数值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 得到String类型数值
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 缓存int类型数值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    /**
     * 得到int类型数值
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    /**
     * 缓存float类型数值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putFloat(Context context, String key, float value) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        sp.edit().putFloat(key, value).commit();
    }

    /**
     * 得到float类型数值
     *
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(ApplicationUtils.CACHE, Context.MODE_PRIVATE);
        return sp.getFloat(key, 0);
    }

}
