package com.rongyan.hpmessage.item;
/**
 * 消息回执
 * @author liumeng
 *
 */
public class MessageReceiptItem {

	private String sn_no;
	private String uuid;
	private String device_token;
	private String fire_shop_no;
	private String actived_at;
	public String getSn_no() {
		return sn_no;
	}
	public void setSn_no(String sn_no) {
		this.sn_no = sn_no;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getDevice_token() {
		return device_token;
	}
	public void setDevice_token(String device_token) {
		this.device_token = device_token;
	}
	public String getFire_shop_no() {
		return fire_shop_no;
	}
	public void setFire_shop_no(String fire_shop_no) {
		this.fire_shop_no = fire_shop_no;
	}
	public String getActived_at() {
		return actived_at;
	}
	public void setActived_at(String actived_at) {
		this.actived_at = actived_at;
	}
	
}
