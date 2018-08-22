package com.rongyan.hpmessage.item;
/**
 * 通过消息id获取指定消息详细消息内容
 * @author liumeng
 *
 */
public class MessageinfoResponseItem {
	private boolean success;//根据这个字段来判断调用是否成功
	private String code;
	private String message;//失败时会放错误信息
	private Data data;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
    public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public static class Data{
		Notificationinfo notification;

		public Notificationinfo getNotification() {
			return notification;
		}

		public void setNotification(Notificationinfo notification) {
			this.notification = notification;
		}
		
	}
	public static class Notificationinfo{
		int id;
		String title;//标题
		String summary;//概述
		String content;//正文内容
		String notification_type;//类别, 可选值 []
		String preview_icon;//预览图标地址
		long pushed_at;
		Ad ad;
		public int getId() {
			return id;
		}
		public long getPushed_at() {
			return pushed_at;
		}
		public void setPushed_at(long pushed_at) {
			this.pushed_at = pushed_at;
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

	    public String getSummary() {
	        return summary;
	    }

	    public void setSummary(String summary) {
	        this.summary = summary;
	    }
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getNotification_type() {
			return notification_type;
		}
		public void setNotification_type(String notification_type) {
			this.notification_type = notification_type;
		}
		public String getPreview_icon() {
			return preview_icon;
		}
		public void setPreview_icon(String preview_icon) {
			this.preview_icon = preview_icon;
		}
		public Ad getAd() {
			return ad;
		}
		public void setAd(Ad ad) {
			this.ad = ad;
		}
		
		public static class Ad{
			int id;
			String image_path;
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
			public String getImage_path() {
				return image_path;
			}
			public void setImage_path(String image_path) {
				this.image_path = image_path;
			}
		}		
	}
  
}
