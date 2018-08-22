package com.rongyan.hpmessage.item;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取开机广告对象
 * 
 * @author liumeng
 * 
 */
public class BootAdsResponseItem {
	private String code;

	private boolean success;

	private String message;

	private Data data;
	
	public BootAdsResponseItem(){
		
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
		private List<Ads> ads;
		
		public void setAds(List<Ads> ads) {
			this.ads = ads;
		}

		public List<Ads> getAds() {
			return this.ads;
		}

		public static class Ads {
			private int id;

			private int notification_id;

			private String image_path;
			
			private String start_at;
			
			private String end_at;
			
			private boolean ad_only;

			public void setId(int id) {
				this.id = id;
			}

			public int getId() {
				return this.id;
			}

			public void setNotification_id(int notification_id) {
				this.notification_id = notification_id;
			}

			public int getNotification_id() {
				return this.notification_id;
			}

			public void setImage_path(String image_path) {
				this.image_path = image_path;
			}

			public String getImage_path() {
				return this.image_path;
			}

			public String getStart_at() {
				return start_at;
			}

			public void setStart_at(String start_at) {
				this.start_at = start_at;
			}

			public String getEnd_at() {
				return end_at;
			}

			public void setEnd_at(String end_at) {
				this.end_at = end_at;
			}

			public boolean isAd_only() {
				return ad_only;
			}

			public void setAd_only(boolean ad_only) {
				this.ad_only = ad_only;
			}
						
		}

	}

}
