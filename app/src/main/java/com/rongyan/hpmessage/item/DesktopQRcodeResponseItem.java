package com.rongyan.hpmessage.item;

/**
 * 获取桌面二维码对象
 * 
 * @author liumeng
 * 
 */

public class DesktopQRcodeResponseItem {
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
		private Qr_code qr_code;

		public void setQr_code(Qr_code qr_code) {
			this.qr_code = qr_code;
		}

		public Qr_code getQr_code() {
			return this.qr_code;
		}

		public static class Qr_code {
			private int id;

			private String image_path;

			public void setId(int id) {
				this.id = id;
			}

			public int getId() {
				return this.id;
			}

			public void setImage_path(String image_path) {
				this.image_path = image_path;
			}

			public String getImage_path() {
				return this.image_path;
			}

		}
	}

}
