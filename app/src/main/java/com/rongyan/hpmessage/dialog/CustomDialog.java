package com.rongyan.hpmessage.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.rongyan.hpmessage.R;


/**
 * 自定义toast类型弹出框
 * Created by panzhihua on 2017/7/18.
 */

public class CustomDialog {

    private Context mContext;

    private Dialog dialog;

    private int dialogNum=0;

    private Handler handler = new Handler();

    private boolean isFirst=false;

    public CustomDialog(Context context) {
        mContext=context;
        if(!isFirst) {
            isFirst=true;
            handler.postDelayed(runnable, 5000); //每隔1s执行
        }
    }

    public void showDailog() {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        if (!dialog.isShowing()) {
                            dialog.show();
                            dialogNum++;
                        } else {
                            dialogNum++;
                        }
                    } else {
                        dialog = new Dialog(mContext, R.style.AlertDialog);
                        View vv = LayoutInflater.from(mContext).inflate(R.layout.dialog_normal_layout, null);

                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setContentView(vv);
                        dialog.show();
                        dialogNum++;
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void hideDailog(){
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialogNum--;
                    if (dialog != null) {
                        if (dialogNum < 1) {
                            dialogNum = 0;
                            dialog.dismiss();
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                if(dialogNum>0){
                    hideDailog();
                }
                handler.postDelayed(this, 3000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

}