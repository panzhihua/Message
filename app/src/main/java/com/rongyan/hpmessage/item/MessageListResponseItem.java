package com.rongyan.hpmessage.item;

import java.util.List;

import android.R.integer;

/**
 * 获取消息列表
 * 
 * @author liumeng
 * 
 */
public class MessageListResponseItem {
	private String status;

	private boolean success;

	private String message;

	private Data data;

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return this.data;
	}

	public static class Data {
		private List<Notifications> notifications;

		public void setNotifications(List<Notifications> notifications) {
			this.notifications = notifications;
		}

		public List<Notifications> getNotifications() {
			return this.notifications;
		}

		public static class Notifications {
			private int id;

			private String title;

			private String summary;

			private String preview_icon;
			
			private long pushed_at;
			
			private boolean isRead;
			
			private String notification_type;
			
			private String content;
			
			private int type;

			public void setId(int id) {
				this.id = id;
			}

			public int getId() {
				return this.id;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getTitle() {
				return this.title;
			}

			public void setSummary(String summary) {
				this.summary = summary;
			}

			public String getSummary() {
				return this.summary;
			}

			public long getPushed_at() {
				return pushed_at;
			}

			public void setPushed_at(long pushed_at) {
				this.pushed_at = pushed_at;
			}

			public void setPreview_icon(String preview_icon) {
				this.preview_icon = preview_icon;
			}

			public String getPreview_icon() {
				return this.preview_icon;
			}

			public boolean isRead() {
		        return isRead;
		    }

		    public void setRead(boolean read) {
		        isRead = read;
		    }
		    
		    public String getNotification_type() {
		        return notification_type;
		    }

		    public void setNotification_type(String notification_type) {
		        this.notification_type = notification_type;
		    }
		    
		    public String getContent() {
		        return content;
		    }

		    public void setContent(String content) {
		        this.content = content;
		    } 
		    public int getType() {
		        return type;
		    }

		    public void setType(int type) {
		        this.type = type;
		    }
		}
	}
}
