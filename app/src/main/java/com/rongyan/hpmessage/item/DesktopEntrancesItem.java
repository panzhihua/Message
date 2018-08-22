package com.rongyan.hpmessage.item;

import java.util.List;

public class DesktopEntrancesItem {
	private String code;

	private boolean success;

	private String message;

	private Data data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
		
		private List<Desktop_Entries> desktop_entries;

		public List<Desktop_Entries> getDesktop_entries() {
			return desktop_entries;
		}

		public void setDesktop_entries(List<Desktop_Entries> desktop_entries) {
			this.desktop_entries = desktop_entries;
		}

		public static class Desktop_Entries {
			
			private String title;// 标题
			
			private String icon_url; // 图标地址
            
			private String behavior; // 点击后的行为 to_app 打开应用， to_url 跳转 url
            
			private String url; // 跳转的 url
            
			private String package_name; // 打开的应用
            
			private String position;// 显示位置
            
			private boolean closable;// 是否可关闭

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getIcon_url() {
				return icon_url;
			}

			public void setIcon_url(String icon_url) {
				this.icon_url = icon_url;
			}

			public String getBehavior() {
				return behavior;
			}

			public void setBehavior(String behavior) {
				this.behavior = behavior;
			}

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getPackage_name() {
				return package_name;
			}

			public void setPackage_name(String package_name) {
				this.package_name = package_name;
			}

			public String getPosition() {
				return position;
			}

			public void setPosition(String position) {
				this.position = position;
			}

			public boolean isClosable() {
				return closable;
			}

			public void setClosable(boolean closable) {
				this.closable = closable;
			}
		}
	}
}
