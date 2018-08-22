package com.rongyan.hpmessage.messagelist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rongyan.hpmessage.R;
import com.rongyan.hpmessage.database.DatabaseColume;
import com.rongyan.hpmessage.item.MessageListResponseItem;
import com.rongyan.hpmessage.util.LogUtils;

/**
 * Created by panzhihua on 2017/6/16. 消息预览adapter
 */

public class MessagePromptAdapter extends
		RootAdapter<MessageListResponseItem.Data.Notifications> {

	public MessagePromptAdapter(Context context) {
		super(context);
	}

	@Override
	protected View getExView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.message_prompt_item, null);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final MessageListResponseItem.Data.Notifications mNotification = mList
				.get(position);
		if (mNotification != null) {
			viewHolder.titleTxt = (TextView) convertView
					.findViewById(R.id.item_title_txt);
			viewHolder.summaryTxt = (TextView) convertView
					.findViewById(R.id.item_summary_txt);
			viewHolder.iconImg = (ImageView) convertView
					.findViewById(R.id.item_icon_img);
			viewHolder.itemFly=(FrameLayout) convertView
					.findViewById(R.id.item_fly);
        	viewHolder.titleTxt.setText(mNotification.getTitle());
			viewHolder.summaryTxt.setText(mNotification.getSummary());
			if(mNotification.getPreview_icon()!=null){
				try{
					Glide.with(context).load(mNotification.getPreview_icon()).error(R.drawable.activity)
							.into(viewHolder.iconImg);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				
			}
			viewHolder.itemFly.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	Intent mIntent = new Intent(context, MessageActivity.class);
	            	mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	            	mIntent.putExtra("id", mNotification.getId()); 
	            	mIntent.putExtra("type", mNotification.getType());
	            	context.startActivity(mIntent);
	            }
	        });
		}
		return convertView;
	}

	class ViewHolder {
		private TextView titleTxt;

		private TextView summaryTxt;

		private ImageView iconImg;
		
		private FrameLayout itemFly;
	}
}
