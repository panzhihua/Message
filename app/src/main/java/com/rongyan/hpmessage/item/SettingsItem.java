package com.rongyan.hpmessage.item;

public class SettingsItem {
	
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

	public static class Data {
		
        private Settings settings;
        
        public Settings getSettings() {
			return settings;
		}

		public void setSettings(Settings settings) {
			this.settings = settings;
		}

		public static class Settings {
            private int collection_duration;
            
            private int report_duration;
            
            private int heartbeat_duration;

			public int getCollection_duration() {
				return collection_duration;
			}

			public void setCollection_duration(int collection_duration) {
				this.collection_duration = collection_duration;
			}

			public int getReport_duration() {
				return report_duration;
			}

			public void setReport_duration(int report_duration) {
				this.report_duration = report_duration;
			}

			public int getHeartbeat_duration() {
				return heartbeat_duration;
			}

			public void setHeartbeat_duration(int heartbeat_duration) {
				this.heartbeat_duration = heartbeat_duration;
			}
                    
        }
    }
}
