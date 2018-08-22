package com.rongyan.hpmessage.messagelist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.MessageService;
import com.rongyan.hpmessage.OnDataBaseListener;
import com.rongyan.hpmessage.R;
import com.rongyan.hpmessage.cache.Cache;
import com.rongyan.hpmessage.database.DataBaseOpenHelper;
import com.rongyan.hpmessage.dialog.CustomDialog;
import com.rongyan.hpmessage.item.MessageListResponseItem;
import com.rongyan.hpmessage.item.MessageinfoResponseItem;
import com.rongyan.hpmessage.item.MessageinfoResponseItem.Notificationinfo;
import com.rongyan.hpmessage.util.AliyunSDKUtils;
import com.rongyan.hpmessage.util.ApplicationUtils;
import com.rongyan.hpmessage.util.HttpGetUtils;
import com.rongyan.hpmessage.util.JsonUtils;
import com.rongyan.hpmessage.util.LogUtils;
import com.rongyan.hpmessage.util.ThreadPoolUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MessageActivity extends Activity implements HttpGetUtils.CallBack,OnDataBaseListener{
	
	private final static String TAG="MessageActivity";
	
	private ListView message_Listview;
	
	private LinearLayout message_Lay,link_Lay;
	
	private TextView message_Time_Txt,message_Title_Txt,message_Hint_Txt;
	
	private WebView message_Content_WebView;
	
	private FrameLayout message_Content_Fly;

    private MessageAdapter mMessageAdapter;
    
    private CustomDialog mCustomDialog;
    
	public String mURL = MessageApplication.HTTP_NOTICATIONS_URL;
	
	public String url;
	
	private Timer mTimer;
	
	private DataBaseOpenHelper openHelper;
	
	public Handler mHandler = new Handler();
	
	private int id=-1;
	
	private int type=-1;
	
	private List<MessageListResponseItem.Data.Notifications> mList = new ArrayList<MessageListResponseItem.Data.Notifications>();
	
	private int selectId=0;
	
	private int selectType=0;
	
	private boolean isShow=false;//判断详情页面是否显示
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        Intent intent = new Intent(this, MessageService.class);
		startService(intent);
    }

    private void init() {
        initView();
        initEvent();
        initData();
    }
    
    private void initView() {
    	ActionBar actionBar = getActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.message));
            actionBar.setDisplayShowHomeEnabled(false);
        }
    	message_Listview = (ListView) findViewById(R.id.message_listview);
    	message_Title_Txt = (TextView) findViewById(R.id.message_title_txt);
        message_Time_Txt = (TextView) findViewById(R.id.message_time_txt);
        message_Lay = (LinearLayout) findViewById(R.id.message_lay);
        message_Hint_Txt = (TextView) findViewById(R.id.message_hint_txt);
        message_Content_Fly = (FrameLayout) findViewById(R.id.message_content_fly);  
        link_Lay = (LinearLayout) findViewById(R.id.link_lay);
    }
    
    private void initEvent(){
    	message_Listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if(mList!=null&&!mList.isEmpty()) {
                	message_Content_Fly.setVisibility(View.INVISIBLE);
                	MessageListResponseItem.Data.Notifications aNotifications=mList.get(arg2);               	
                	if(aNotifications!=null){              
                		selectId=mList.get(arg2).getId();
                		selectType=mList.get(arg2).getType();
                		mMessageAdapter.setSelectedPosition(selectId,selectType);
                		mMessageAdapter.setList(mList);
                		message_Title_Txt.setText(aNotifications.getTitle());
                		message_Time_Txt.setText(getDateTime(aNotifications.getPushed_at())); 
                		updatePageProperties(selectId);
                		postReadTask(selectId);
                		//如果缓存里面已经有这条消息 将不做处理
            			if (Cache.get(selectId+"+"+selectType) == null) {
            				url=mURL+selectId;
            				startTimer(0);
            			}else{
            				message_Content_Fly.setVisibility(View.VISIBLE);
            			    message_Content_WebView.loadDataWithBaseURL(null, String.valueOf(Cache.get(selectId+"+"+selectType)), "text/html", "utf-8", null);
            			                    
            			}
                	}
                }
            }
        });
    }
    
    private void initData() {
    	mCustomDialog=new CustomDialog(MessageActivity.this);
    	 mMessageAdapter=new MessageAdapter(getApplication());
         message_Listview.setAdapter(mMessageAdapter);
         openHelper = DataBaseOpenHelper.getInstance(this);
         openHelper.setOnDataBaseListener(this);
    }
    
    private void initWebView(){
    	try{
	    	message_Content_WebView = new WebView(getApplicationContext()); 
	    	message_Content_Fly.addView(message_Content_WebView);
	    	message_Content_WebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
	        message_Content_WebView.getSettings().setUseWideViewPort(true);
	        message_Content_WebView.setWebViewClient(new WebViewClient(){
	        	public WebResourceResponse shouldInterceptRequest(WebView view,  
	        	        String url) {  
	        	    return null;  
	        	}  
	        	/** 
	        	 * 在结束加载网页时会回调 
	        	 */  
	        	public void onPageFinished(WebView view, String url)  {
	        		new Thread(new Runnable() {
	        	           @Override
	        	           public void run() {
	                    	if(isShow&&mList!=null&&!mList.isEmpty()){
		                    	for(int i=0;i<mList.size();i++){
		        					if(mList.get(i).getId()==selectId&&mList.get(i).getType()==selectType){
		        						mList.get(i).setRead(true);
		        						runOnUiThread(new Runnable() {
		        		                    @Override
		        		                    public void run() {
		        		                    	mMessageAdapter.setList(mList);
		        		                    }
		        						});
						        		openHelper.Update(String.valueOf(selectId),String.valueOf(selectType),1);
		        					}
		                    	}
	                    	}
	        	          }
	        		}).start();
	        	}
	        });  
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void initWebView(String url){
    	try{
    		message_Content_WebView = new WebView(getApplicationContext()); 
    		link_Lay.addView(message_Content_WebView);
    		message_Content_WebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
    		message_Content_WebView.getSettings().setUseWideViewPort(true);
    		message_Content_WebView.getSettings().setJavaScriptEnabled(true);
    		message_Content_WebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    		message_Content_WebView.setWebChromeClient(new WebChromeClient());
    		String ua = message_Content_WebView.getSettings().getUserAgentString();  
    		LogUtils.w("pipa", "ua="+ua);
    		message_Content_WebView.getSettings().setUserAgentString(ua+"; RongyanNotification;"+Build.SERIAL);  
    		message_Content_WebView.loadUrl(url);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        	LogUtils.w("pipa", "newProgress="+newProgress);
            if (newProgress > 80) {
            	mCustomDialog.hideDailog();
            } else {
                
            }
            super.onProgressChanged(view, newProgress);
        }

    }
    
    @Override
    protected void onStart(){
    	super.onStart();    
    	isShow=true;
        id = getIntent().getIntExtra("id", -1);  
        if(id!=-999){
        	getActionBar().show();
        	link_Lay.setVisibility(View.GONE);
	        type = getIntent().getIntExtra("type", -1);
	        initWebView();
	    	refresh();
        }else{
        	mCustomDialog.showDailog();
        	getActionBar().hide();
        	message_Lay.setVisibility(View.GONE);
        	message_Hint_Txt.setVisibility(View.GONE);
        	link_Lay.setVisibility(View.VISIBLE);
        	String url = getIntent().getStringExtra("url");  
        	LogUtils.w(TAG, "url:"+url);
        	initWebView(url);
        }
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);		
	}
    
    private void startTimer(long time) {
		if (mTimer != null) {
			mTimer.cancel();
		}
		if (ApplicationUtils.ismNetWorkEnable()) {
			mTimer = new Timer();
			mTimer.schedule(new MyTask(), time);
		}
	}

	class MyTask extends TimerTask {
		@Override
		public void run() {
			startHttpConnect();			
		}

	}

	private void startHttpConnect() {
		if (ApplicationUtils.ismNetWorkEnable()) {
			ThreadPoolUtils.newFixThreadPool(new HttpGetUtils(this,url, mHandler));
		}
	}

	@Override
	public void setResponseData(String value,String type,String time) {
		try {
			LogUtils.w(TAG, "selectType="+selectType+"|selectId="+selectId);
			MessageinfoResponseItem item = (MessageinfoResponseItem) JsonUtils
					.jsonToBean(value, MessageinfoResponseItem.class);
			if (item!=null && item.isSuccess() && item.getData() != null) {
				final Notificationinfo mNotificationinfo = item.getData().getNotification();
				if(mNotificationinfo!=null){
					//把消息内容放入缓存
        			if (Cache.get(String.valueOf(mNotificationinfo.getId())+"+"+selectType) == null) {
        				Cache.add(String.valueOf(mNotificationinfo.getId()+"+"+selectType), mNotificationinfo.getContent());
        			}
        			message_Content_Fly.setVisibility(View.VISIBLE);
			        message_Content_WebView.loadDataWithBaseURL(null, mNotificationinfo.getContent(), "text/html", "utf-8", null);  
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setFailedResponse() {
		//startTimer(60000);
	}
	
	private void refresh(){
		try{
			runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
					message_Content_Fly.setVisibility(View.INVISIBLE);
					if(openHelper==null){
						openHelper = DataBaseOpenHelper.getInstance(getApplicationContext());
					}
					mList = openHelper.getNotifications(1);
					if(mList!=null && !mList.isEmpty()){
						message_Lay.setVisibility(View.VISIBLE);
						message_Hint_Txt.setVisibility(View.GONE);
						if(id==-1||type==-1){
							selectId=mList.get(0).getId();
							selectType=mList.get(0).getType();
							mMessageAdapter.setSelectedPosition(selectId,selectType); 
			            	mMessageAdapter.setList(mList);
			        		message_Title_Txt.setText(mList.get(0).getTitle());
			        		message_Time_Txt.setText(getDateTime(mList.get(0).getPushed_at())); 
			        		updatePageProperties(selectId);
			        		postReadTask(selectId);
			        		message_Listview.post(new Runnable() {
							    @Override
							    public void run() {
							    	message_Listview.smoothScrollToPosition(0);
							    }
							});
			        		message_Listview.post(new Runnable() {
							    @Override
							    public void run() {
							    	message_Listview.smoothScrollToPosition(0);
							    }
							});
			        		//如果缓存里面已经有这条消息 将不做处理
			    			if (Cache.get(String.valueOf(selectId)+"+"+selectType) == null) {
			    				url=mURL+selectId;
			    				startTimer(0);
			    			}else{
			    				message_Content_Fly.setVisibility(View.VISIBLE);
			    				message_Content_WebView.loadDataWithBaseURL(null, String.valueOf(Cache.get(String.valueOf(selectId)+"+"+selectType)), "text/html", "utf-8", null);  
			    			}
						}else{
							boolean selected=true;
							for(int i=0;i<mList.size();i++){
								if(mList.get(i).getId()==id&&mList.get(i).getType()==type){
									selected=false;
									selectId=mList.get(i).getId();
									selectType=mList.get(i).getType();
									mMessageAdapter.setSelectedPosition(selectId,selectType); 
				                	mMessageAdapter.setList(mList);
				            		message_Title_Txt.setText(mList.get(i).getTitle());
				            		message_Time_Txt.setText(getDateTime(mList.get(i).getPushed_at())); 
				            		updatePageProperties(selectId);
				            		postReadTask(selectId);
				            		final int selectI=i;
				            		message_Listview.post(new Runnable() {
				    				    @Override
				    				    public void run() {
				    				    	message_Listview.smoothScrollToPosition(selectI);
				    				    }
				    				});
				            		//如果缓存里面已经有这条消息 将不做处理
				        			if (Cache.get(String.valueOf(selectId)+"+"+selectType) == null) {
				        				url=mURL+selectId;
				        				startTimer(0);
				        			}else{
				        				message_Content_Fly.setVisibility(View.VISIBLE);
				        				message_Content_WebView.loadDataWithBaseURL(null, String.valueOf(Cache.get(String.valueOf(selectId)+"+"+selectType)), "text/html", "utf-8", null);  
				        			}
								}
							}
							if(selected){
								selectId=mList.get(0).getId();
								selectType=mList.get(0).getType();
								mMessageAdapter.setSelectedPosition(selectId,selectType); 
				            	mMessageAdapter.setList(mList);
				        		message_Title_Txt.setText(mList.get(0).getTitle());
				        		message_Time_Txt.setText(getDateTime(mList.get(0).getPushed_at())); 
				        		updatePageProperties(selectId);
				        		postReadTask(selectId);
				        		message_Listview.post(new Runnable() {
			    				    @Override
			    				    public void run() {
			    				    	message_Listview.smoothScrollToPosition(0);
			    				    }
			    				});
				        		message_Listview.post(new Runnable() {
			    				    @Override
			    				    public void run() {
			    				    	message_Listview.smoothScrollToPosition(0);
			    				    }
			    				});
				        		//如果缓存里面已经有这条消息 将不做处理
				    			if (Cache.get(String.valueOf(selectId)+"+"+selectType) == null) {
				    				url=mURL+selectId;
				    				startTimer(0);
				    			}else{
				    				message_Content_Fly.setVisibility(View.VISIBLE);
				    				message_Content_WebView.loadDataWithBaseURL(null, String.valueOf(Cache.get(String.valueOf(selectId)+"+"+selectType)), "text/html", "utf-8", null);  
				    			}
							}
						}
					}else{
						message_Lay.setVisibility(View.GONE);
						message_Hint_Txt.setVisibility(View.VISIBLE);
					}
	            }
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	@Override
	public void onPause(){
		super.onPause();
		isShow=false;
		message_Content_WebView.removeAllViews(); 
		message_Content_WebView.destroy();
	}

	@Override
	public void onDestroy(){
		openHelper.setOnDataBaseListener(null);
		setContentView(R.layout.view_null);
		System.gc();
		super.onDestroy();  		
	}
	
	public String getDateTime(long pushed_at){ 
		if(pushed_at>10000000000L){
			pushed_at=pushed_at/1000;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date(pushed_at*1000L);
        String dates = simpleDateFormat.format(date);	        
		return dates;
    }

	@Override
	public void onDataBaseClick() {
		refresh();
	} 
	
	//Activity页面增加属性统计
	private void updatePageProperties(int item_id){
		Map<String, String> lMap = new HashMap<String, String>();
		lMap.put("item_id",String.valueOf(item_id));
		MANService manService = MANServiceProvider.getService();
		manService.getMANPageHitHelper().updatePageProperties(lMap);
	}
	
	private void postReadTask(int id){
		try{
			String url = MessageApplication.HTTP_NOTIFICATION_PUSHRECEIPT_STRING
					+ id + "/read";
			MessageReadTask task = new MessageReadTask(
					this, url, null);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("sn_no", Build.SERIAL);
			map.put("uuid", MessageApplication.getUUID());
			map.put("device_token", MessageService.mDeviceToken);
			map.put("fire_shop_no",
					MessageApplication.getentityId());
			map.put("actived_at",
					ApplicationUtils
							.getIntance(this)
							.getPerferencesStringValue(
									ApplicationUtils.ACTIVITIES_TIME_STRING));
			String receiptString = JsonUtils.beanToJson(map);
			task.setData(receiptString, true);
			task.startTimer(0);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK&&message_Content_WebView.canGoBack()) {
			message_Content_WebView.goBack();
			return true;
		}else {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
