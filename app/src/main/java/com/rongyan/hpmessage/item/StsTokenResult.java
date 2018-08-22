package com.rongyan.hpmessage.item;

public class StsTokenResult extends Result{

	private Data data;
	
	public Data getData() {
		return data;
	}


	public void setData(Data data) {
		this.data = data;
	}

	public static class Data{
		
		private String access_key_id;
		
		private String access_key_secret;
		
		private String security_token;
		
		private Long expiration;
		
		private String log_level;

		public String getAccess_key_id() {
			return access_key_id;
		}

		public void setAccess_key_id(String access_key_id) {
			this.access_key_id = access_key_id;
		}

		public String getAccess_key_secret() {
			return access_key_secret;
		}

		public void setAccess_key_secret(String access_key_secret) {
			this.access_key_secret = access_key_secret;
		}

		public String getSecurity_token() {
			return security_token;
		}

		public void setSecurity_token(String security_token) {
			this.security_token = security_token;
		}

		public Long getExpiration() {
			return expiration;
		}

		public void setExpiration(Long expiration) {
			this.expiration = expiration;
		}

		public String getLog_level() {
			return log_level;
		}

		public void setLog_level(String log_level) {
			this.log_level = log_level;
		}
		
	}
}
