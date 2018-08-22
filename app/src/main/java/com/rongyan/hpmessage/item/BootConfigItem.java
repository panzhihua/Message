package com.rongyan.hpmessage.item;

import java.util.List;

import android.R.integer;

public class BootConfigItem extends Result{
	
	private Data data;
	
	public Data getData() {
		return data;
	}
	
	public void setData(Data data) {
		this.data = data;
	}
	
	public static class Data{
		
		BootConfig boot_config;

		public BootConfig getBoot_config() {
			return boot_config;
		}

		public void setBoot_config(BootConfig boot_config) {
			this.boot_config = boot_config;
		}
	}
	
	public static class BootConfig{
		private QrCode qr_code;
		
		private ApksResponseItem.Apk latest_rongyan_notification_apk;
		
		private ApksResponseItem.Apk latest_rongyan_appstore_apk;
		
		private List<DesktopEntrancesItem.Data.Desktop_Entries> desktop_entries;
		
		private List<BootAdsResponseItem.Data.Ads> ads;
		
		private String log_level;

		public QrCode getQr_code() {
			return qr_code;
		}

		public void setQr_code(QrCode qr_code) {
			this.qr_code = qr_code;
		}

		public ApksResponseItem.Apk getLatest_rongyan_notification_apk() {
			return latest_rongyan_notification_apk;
		}

		public void setLatest_rongyan_notification_apk(
				ApksResponseItem.Apk latest_rongyan_notification_apk) {
			this.latest_rongyan_notification_apk = latest_rongyan_notification_apk;
		}

		public ApksResponseItem.Apk getLatest_rongyan_appstore_apk() {
			return latest_rongyan_appstore_apk;
		}

		public void setLatest_rongyan_appstore_apk(
				ApksResponseItem.Apk latest_rongyan_appstore_apk) {
			this.latest_rongyan_appstore_apk = latest_rongyan_appstore_apk;
		}

		public List<DesktopEntrancesItem.Data.Desktop_Entries> getDesktop_entries() {
			return desktop_entries;
		}

		public void setDesktop_entries(List<DesktopEntrancesItem.Data.Desktop_Entries> desktop_entries) {
			this.desktop_entries = desktop_entries;
		}

		public List<BootAdsResponseItem.Data.Ads> getAds() {
			return ads;
		}

		public void setAds(List<BootAdsResponseItem.Data.Ads> ads) {
			this.ads = ads;
		}

		public String getLog_level() {
			return log_level;
		}

		public void setLog_level(String log_level) {
			this.log_level = log_level;
		}
		
		public static class QrCode{
			
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
