package com.rongyan.hpmessage.item;

import android.R.integer;

/**
 * 云箭推送格式
 * 
 * @author liumeng
 * 
 */
public class MessagePushItem {
		int id;
		long pushed_at;
		String title;
		String summary;
		boolean return_receipt;
		String preview_icon;
		boolean notice_user;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public long getPushed_at() {
			return pushed_at;
		}

		public void setPushed_at(long pushed_at) {
			this.pushed_at = pushed_at;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public boolean isReturn_receipt() {
			return return_receipt;
		}

		public void setReturn_receipt(boolean return_receipt) {
			this.return_receipt = return_receipt;
		}

		public String getPreview_icon() {
			return preview_icon;
		}

		public void setPreview_icon(String preview_icon) {
			this.preview_icon = preview_icon;
		}
		
		public boolean isNotice_user() {
	        return notice_user;
	    }

	    public void setNotice_user(boolean notice_user) {
	        this.notice_user = notice_user;
	    }

}
