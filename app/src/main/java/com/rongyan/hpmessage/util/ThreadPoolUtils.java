package com.rongyan.hpmessage.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtils {

	private static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(5);
	
	public static void newFixThreadPool(Runnable mRunnable){  
		cachedThreadPool.submit(mRunnable);
	}   
}
