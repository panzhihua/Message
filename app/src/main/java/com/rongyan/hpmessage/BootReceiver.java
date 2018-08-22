/*
 * 开机启动接受boot complete消息 启动Device服务
 */
package com.rongyan.hpmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Intent intent = new Intent(arg0, MessageService.class);
		arg0.startService(intent);
	}

}
