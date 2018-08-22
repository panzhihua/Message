package com.rongyan.hpmessage.messagelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rongyan.hpmessage.R;
import com.rongyan.hpmessage.item.MessageListResponseItem;

/**
 * Created by panzhihua on 2017/6/16. 消息详情页adapter
 */

public class MessageAdapter extends RootAdapter<MessageListResponseItem.Data.Notifications> {
	
	
	private int selectedId = -1;// 选中的位置  
	
	private int selectedType = -1;// 选中的位置  
	 
    public MessageAdapter(Context context) {
        super(context);
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        MessageAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new MessageAdapter.ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.message_item, null);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (MessageAdapter.ViewHolder) convertView.getTag();
        }
        MessageListResponseItem.Data.Notifications mNotification=mList.get(position);
        if(mNotification!=null){
        	viewHolder.titleTxt = (TextView)convertView.findViewById(R.id.item_title_txt);
            viewHolder.summaryTxt = (TextView)convertView.findViewById(R.id.item_summary_txt);
            viewHolder.iconImg = (ImageView)convertView.findViewById(R.id.item_icon_img);
            viewHolder.redotImg = (ImageView)convertView.findViewById(R.id.item_redot_img);
            viewHolder.itemFly = (FrameLayout)convertView.findViewById(R.id.item_fly);
        	if(mNotification.isRead()){//已读
        		viewHolder.titleTxt.setTextColor(context.getResources().getColor(R.color.gray_99));
        		viewHolder.summaryTxt.setTextColor(context.getResources().getColor(R.color.gray_99));
        		viewHolder.redotImg.setVisibility(View.GONE);
        	}else{//未读
        		viewHolder.titleTxt.setTextColor(context.getResources().getColor(R.color.gray_33));
        		viewHolder.summaryTxt.setTextColor(context.getResources().getColor(R.color.gray_66));
        		viewHolder.redotImg.setVisibility(View.VISIBLE);
        	}
        	if (selectedId == mNotification.getId()&&selectedType==mNotification.getType()) { 
        		viewHolder.itemFly.setBackgroundColor(context.getResources().getColor(R.color.gray_d8));  
        	}else{
        		viewHolder.itemFly.setBackgroundColor(context.getResources().getColor(R.color.white)); 
        	}
        	viewHolder.titleTxt.setText(mNotification.getTitle());
            viewHolder.summaryTxt.setText(mNotification.getSummary());
            Glide.with(context)
                    .load(mNotification.getPreview_icon())
                    .into(viewHolder.iconImg);
        }
        return convertView;
    }  
    
    public void setSelectedPosition(int id,int type) {    
    	selectedId = id;   
    	selectedType=type;
    } 

    class ViewHolder{
        private TextView titleTxt;

        private TextView summaryTxt;

        private ImageView iconImg;
        
        private ImageView redotImg;
        
        private FrameLayout itemFly;
    }
}
