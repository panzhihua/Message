package com.rongyan.hpmessage;


import java.util.Date;


import com.rongyan.hpmessage.cmns.MessageReceiptOprea;
import com.rongyan.hpmessage.item.MessagePushItem;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.yunos.push.PushClient;
import com.yunos.push.api.listener.PushConnectionListener;
import com.yunos.push.api.listener.PushGetDeviceTokenListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	private Handler mHandler = new Handler();
	Context mActivity;
	Button mConnectButton;
	Button mGetToken;
	Button mAddButton;
	ImageView mImg;
	MessageReceiptOprea mMessageReceiptOprea;
	int id = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mConnectButton = (Button)findViewById(R.id.btnconnect);
		mActivity = getApplicationContext();
		mConnectButton.setOnClickListener(this);
		mGetToken = (Button)findViewById(R.id.gettoken);
		mGetToken.setOnClickListener(this);
		mAddButton = (Button)findViewById(R.id.add);
		mAddButton.setOnClickListener(this);
		mMessageReceiptOprea = MessageReceiptOprea.getInstance(this);
//		Intent intent = new Intent(this, MessageService.class);
//		startService(intent);
	}
	

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.btnconnect){
			PushClient.getInstance().connect(new PushConnectionListener() {
				@Override
				public void onConnect(final int errorCode) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (errorCode == 0) {
								Toast.makeText(mActivity, "connect success", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(mActivity, "connect failed: " + errorCode, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		}else if(view.getId() == R.id.gettoken){
			PushClient.getInstance().getDeviceToken(new PushGetDeviceTokenListener() {
				@Override
				public void onGetDeviceToken(final int errorCode, final String token) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(mActivity, "getDeviceToken errorCode:" + errorCode + "\ntoken: " + token,
									Toast.LENGTH_SHORT).show();
							LogUtils.w("liumeng0112", "getDeviceToken:"+ token);
						}
					});
				}
			});
		}else if(view.getId() == R.id.add){
			id=id+1;
			mAddButton.setText(""+id);
			MessagePushItem item = new MessagePushItem();
			item.setId(id);
			item.setPreview_icon("http://pic.58pic.com/58pic/16/69/38/42v58PICzEP_1024.jpg");
			item.setPushed_at(getSystemTime());
			item.setSummary("消息概括");
			item.setTitle("新消息"+id);
			item.setReturn_receipt(true);
			try {
				String data = JsonUtils.beanToJson(item);
				mMessageReceiptOprea.setData(data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public long getSystemTime(){
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		long unixTimestamp = System.currentTimeMillis(); 
		return unixTimestamp;
	}
}
