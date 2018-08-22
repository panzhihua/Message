package com.rongyan.hpmessage.cache;

import com.rongyan.hpmessage.cache.Cache.ICacheMethod;

public class CacheItem {

	private String key;
	private Object value;
	private long timeOut;
	private ICacheMethod callback = null;

	public CacheItem() {

	}

	public ICacheMethod getCallback() {
		return callback;
	}

	public void setCallback(ICacheMethod callback) {
		this.callback = callback;
	}

	public CacheItem(String key, Object value) {
		this.key = key;
		this.value = value;
		this.timeOut = 0;
	}

	public CacheItem(String key, Object value, long timeOut) {
		this.key = key;
		this.value = value;
		this.timeOut = timeOut;
	}

	public CacheItem(String key, Object value, long timeOut,
			ICacheMethod callback) {
		this.key = key;
		this.value = value;
		this.timeOut = timeOut;
		this.callback = callback;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}
}
