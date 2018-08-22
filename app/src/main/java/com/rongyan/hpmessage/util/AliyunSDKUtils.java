package com.rongyan.hpmessage.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;
import com.aliyun.sls.android.sdk.ClientConfiguration;
import com.aliyun.sls.android.sdk.LOGClient;
import com.aliyun.sls.android.sdk.LogException;
import com.aliyun.sls.android.sdk.SLSLog;
import com.aliyun.sls.android.sdk.core.auth.StsTokenCredentialProvider;
import com.aliyun.sls.android.sdk.core.callback.CompletedCallback;
import com.aliyun.sls.android.sdk.model.Log;
import com.aliyun.sls.android.sdk.model.LogGroup;
import com.aliyun.sls.android.sdk.request.PostLogRequest;
import com.aliyun.sls.android.sdk.result.PostLogResult;
import com.rongyan.hpmessage.MessageApplication;
import com.rongyan.hpmessage.item.StsItem;
import com.rongyan.hpmessage.item.StsTokenResult;


public class AliyunSDKUtils implements HttpGetUtils.CallBack{

    public static final String TAG = "AliyunSDKUtils";
    
    private HttpGetUtils mStsTokenUtils;

    private Handler mHandler = new Handler();
    
    private Timer mStsTokenTimer;

    private static ACache mCache;

    private static Context mContext;

    /**
     * 填入必要的参数
     */
    public String endpoint = "cn-hangzhou.log.aliyuncs.com";

    public String project = "rongyan";

    public String logStore = "notification";

    public String uuid=null;
    
    private boolean isPut=false;
    
    private LinkedBlockingQueue<StsItem> queue = new LinkedBlockingQueue<>();

    private static volatile AliyunSDKUtils aliyunsdkutils;

    private AliyunSDKUtils() {}

    public static AliyunSDKUtils getInstance(Context context) {
        if (aliyunsdkutils == null) {
            synchronized (AliyunSDKUtils.class) {
                if (aliyunsdkutils == null) {
                    aliyunsdkutils = new AliyunSDKUtils();
                    mCache=ACache.get(context);
                    mContext=context;
                }
            }
        }
        return aliyunsdkutils;
    }

    public void asyncUploadLog(String mSTS_AK,String mSTS_SK,String mSTS_TOKEN,String time,String type) {

//        STS使用方式
        String STS_AK = mSTS_AK;
        String STS_SK = mSTS_SK;
        String STS_TOKEN = mSTS_TOKEN;
        StsTokenCredentialProvider credentialProvider =
                new StsTokenCredentialProvider(STS_AK, STS_SK, STS_TOKEN);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        SLSLog.enableLog(); // log打印在控制台
        LOGClient logClient = new LOGClient(endpoint, credentialProvider, conf);
        /* 创建logGroup */
        LogGroup logGroup = new LogGroup(time, Build.SERIAL);

        /* 存入一条log */
        Log log = new Log();
        log.PutContent("type",type);
        log.PutContent("型号", Build.DISPLAY);
        log.PutContent("版本号", MessageApplication.getAppVersion(mContext));
        logGroup.PutLog(log);
        try {
            PostLogRequest request = new PostLogRequest(project, logStore, logGroup);
            logClient.asyncPostLog(request, new CompletedCallback<PostLogRequest, PostLogResult>() {
                @Override
                public void onSuccess(PostLogRequest request, PostLogResult result) {
                    LogUtils.w(TAG,"onSuccess");
                    isPut=false;
                    putSts();
                }

                @Override
                public void onFailure(PostLogRequest request, LogException exception) {
                    mCache.put("TIME","");
                    LogUtils.w(TAG,"onFailure:"+exception.getMessage());
                    isPut=false;
                    putSts();
                }
            });
        } catch (LogException e) {
        	 isPut=false;
        	 putSts();
            e.printStackTrace();
        }
    }
    
    public void startTimer(String type,String time) {
        if (ApplicationUtils.ismNetWorkEnable()){
            if (mStsTokenTimer != null) {
            	mStsTokenTimer.cancel();
            }
            mStsTokenTimer = new Timer();
            mStsTokenTimer.schedule(new StsToken(type,time),0);
        }
	}
    
    class StsToken extends TimerTask {

    	private String mType,mTime;
    	
    	StsToken(String type,String time){
    		mType=type;
    		mTime=time;
    	}
        @Override
        public void run() {
        	mStsTokenUtils = new HttpGetUtils(AliyunSDKUtils.this,MessageApplication.HTTP_STS_TOKEN_URL, mHandler,mType,mTime);
        	ThreadPoolUtils.newFixThreadPool(mStsTokenUtils);
        }
    }
    
    public void putLogTst(String type,int logLevel){
        LogUtils.w(TAG,type);
        String times=StringUtils.getSystemTime2();
        StsItem mStsItem=new StsItem();
        mStsItem.setType(type);
        mStsItem.setTimes(times);
        mStsItem.setLevel(logLevel);
        queue.add(mStsItem);
        putSts();
    }

    public void putSts(String type, String times,int logLevel){
        int level=0;
        String sts_ak = mCache.getAsString("STS_AK");
        String sts_sk = mCache.getAsString("STS_SK");
        String sts_token = mCache.getAsString("STS_TOKEN");
        String time = mCache.getAsString("TIME");
        String levelString = mCache.getAsString("LEVEL");
        if("debug".equals(levelString)){
            level=0;
        }else if("info".equals(levelString)){
            level=1;
        }else if("warn".equals(levelString)){
            level=2;
        }else if("error".equals(levelString)){
            level=3;
        }else if("fatal".equals(levelString)){
            level=4;
        }
        if(logLevel<level){
            return;
        }
        isPut=true;
//        String sts_ak = "STS.NHsTexCMsVcQWZt15MzRxmbJq";
//        String sts_sk = "6BPZG2V9NC2ZASpbMdY2GBMyavFu2jW8mcD6LPZwaitF";
//        String sts_token ="CAIS4gJ1q6Ft5B2yfSjIr4vGH9/MrpJS4aG6VXzF1TUYdt1Ugqfhkzz2IHxPfXBqBu0bsvQ+lGFZ5/oclr53TJBeWUveYPx56JhN9gKtVJLGv82+/OSgWAYiuSzBZSTg1er+Ps86JbG0I4VgAjKpvyt3xqSAM1fGdle5MJqPpId6Z9AMJGeRZiZHA9EkWGkerMgVZ0PWLuqJNRHRnm3KAW5vtREGjQEZ06mkxdCG4RfzlUDzzvRvx778OZ+5dcJhTq8ddd6+x75ycaWT2ygV6xFM+7V2i7EWoGue4IjMWAEBvU3cY7ONro93Ngh7a6MmXK5Zq+Dh0OZ5puGUk4P40ApKJ+wSUznYXJ3lyc3IAuSoOvRBLOqiZS2ciYjfaMGv41p9PUh2bl0aJ4ATTVZrEgEpRz3gLauqxUvHeA/LSdLejPpviMAqkgi0rYvadgneH+uDsykfNtonYlk2OgQGZ/ZEtBXu2WYagAGv9SICfbbcJTXa/9NdPQg0c8k+HDWqj28T9fJOn3vCE0ML0a2UVzVcxo3pSJCAGK5JXSZH4tBggl+kuhsj+6FdJYWnxvKDyIerX9VXh+VB1bIN/mknabE20zXdMk4aEY9edjvLIcI60yFZCr8eggpqF1JFoRnhgO8eiTB+0F8ZNg==";
//        String time ="1531390516";
        long difference_time=0;
        if(time!=null&&!time.equals("")){
            difference_time=StringUtils.getSystemTime()-Long.valueOf(time);
        }
        if(sts_ak!=null&&!sts_ak.equals("")&&sts_sk!=null&&!sts_sk.equals("")&&sts_token!=null&&!sts_token.equals("")&&difference_time<0){
            asyncUploadLog(sts_ak,sts_sk,sts_token, times,type);
        }else{
        	startTimer(type,times);
        }
    }
    
    public static boolean isDebug(){
        boolean isDebug = mContext.getApplicationInfo()!=null&&
                (mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        return isDebug;

    }

	@Override
	public void setResponseData(String value,String type,String time) {
		LogUtils.w(TAG, value);
		try {
			StsTokenResult item = (StsTokenResult) JsonUtils
                    .jsonToBean(value, StsTokenResult.class);
            if (item != null && item.isSuccess()) {          
            	mCache.put("STS_AK", item.getData().getAccess_key_id());
                mCache.put("STS_SK",item.getData().getAccess_key_secret());
                mCache.put("STS_TOKEN",item.getData().getSecurity_token());
                mCache.put("TIME",String.valueOf(item.getData().getExpiration()));
                mCache.put("LEVEL",item.getData().getLog_level());
                LogUtils.w(TAG,"STS_AK:"+item.getData().getAccess_key_id()+"STS_SK:"+item.getData().getAccess_key_secret()+"STS_TOKEN:"+item.getData().getSecurity_token()+"LEVEL:"+item.getData().getLog_level());
                asyncUploadLog(item.getData().getAccess_key_id(),item.getData().getAccess_key_secret(),item.getData().getSecurity_token(),time,type);
            }
		}catch(Exception e){
			 isPut=false;
			 putSts();
			LogUtils.w(TAG, e.toString());
			e.printStackTrace();
		}
	
	}

	@Override
	public void setFailedResponse() {
		 isPut=false;
		 putSts();
		LogUtils.w(TAG, "setFailedResponse");
	}
	
	private void putSts(){
		 if(!isPut){
        	StsItem stsItem=queue.poll();
            if(stsItem!=null){
                putSts(stsItem.getType(),stsItem.getTimes(),stsItem.getLevel());
            }
	     }
	}

}
