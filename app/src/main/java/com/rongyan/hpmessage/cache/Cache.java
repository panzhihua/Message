package com.rongyan.hpmessage.cache;

import java.util.Date;
import java.util.Hashtable;
/**
 * 缓存对象
 * @author liumeng
 *
 */
public class Cache {
	public static Hashtable<String, Object> mMessageListTable = new Hashtable<String, Object>();
	
	public interface ICacheMethod {
		public void execute(String key);
	}
	
	public Cache() {

	}

	/**
	 *  添加cache,不过期
	 * @param key 
	 * @param value
	 */
	public synchronized static void add(String key, Object value) {
		add(key, value, -1);
	}

	/**
	 * 添加cache有过期时间
	 * @param key
	 * @param value
	 * @param timeOut
	 */
	public synchronized static void add(String key, Object value, long timeOut) {
		add(key, value, timeOut, null);
	}

	/**
	 *  添加cache有过期时间并且具有回调方法
	 * @param key
	 * @param value
	 * @param timeOut
	 * @param callback
	 */
	public synchronized static void add(String key, Object value, long timeOut,
			ICacheMethod callback) {
		if (timeOut > 0) {
			timeOut += new Date().getTime();
		}
		CacheItem item = new CacheItem(key, value, timeOut, callback);
		mMessageListTable.put(key, item);
	}

	/**
	 *  获取cache 对象
	 * @param key
	 * @return 返回Cache对象
	 */
	public synchronized static Object get(String key) {
		Object obj = mMessageListTable.get(key);
		if (obj == null) {
			return null;
		}
		CacheItem item = (CacheItem) obj;
		boolean expired = cacheExpired(key);
		if (expired == true) // 已过期
		{
			if (item.getCallback() == null) {
				remove(key);
				return null;
			} else {
				ICacheMethod callback = item.getCallback();
				callback.execute(key);
				expired = cacheExpired(key);
				if (expired == true) {
					remove(key);
					return null;
				}
			}
		}
		return item.getValue();
	}

	/**
	 *  移除cache
	 * @param key
	 */
	public synchronized static void remove(String key) {
		Object obj = mMessageListTable.get(key);
		if (obj != null) {
			obj = null;
		}
		mMessageListTable.remove(key);
	}

	/**
	 *  判断是否过期
	 * @param key
	 * @return
	 */
	private static boolean cacheExpired(String key) {
		CacheItem item = (CacheItem) mMessageListTable.get(key);
		if (item == null) {
			return false;
		}
		long milisNow = new Date().getTime();
		long milisExpire = item.getTimeOut();
		if (milisExpire <= 0) { // 不过期
			return false;
		} else if (milisNow >= milisExpire) {
			return true;
		} else {
			return false;
		}
	}

}
