package com.rongyan.hpmessage.item;

public class ApksResponseItem {
	
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
		Apk apk;

		public Apk getApk() {
			return apk;
		}

		public void setApk(Apk apk) {
			this.apk = apk;
		}
	}
	
	public static class Apk{
		
		private int id;
		
		private String version;
		
		private String apk_file_url;
		
		private String apk_type;
		
		private String apk_type_text;
		
		public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getVersion() {
	        return version;
	    }

	    public void setVersion(String version) {
	        this.version = version;
	    }

	    public String getApk_file_url() {
	        return apk_file_url;
	    }

	    public void setApk_file_url(String apk_file_url) {
	        this.apk_file_url = apk_file_url;
	    }

	    public String getApk_type() {
	        return apk_type;
	    }

	    public void setApk_type(String apk_type) {
	        this.apk_type = apk_type;
	    }

	    public String getApk_type_text() {
	        return apk_type_text;
	    }

	    public void setApk_type_text(String apk_type_text) {
	        this.apk_type_text = apk_type_text;
	    }
		
	}
}
