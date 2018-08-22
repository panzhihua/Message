package com.rongyan.hpmessage.item;
/**
 * 消息回执服务器返回对象
 * @author liumeng
 *
 */
public class MessageReceiptResponseItem {
    private String success;
    private String codeString;
    private String message;
    private String dataString;
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getCodeString() {
		return codeString;
	}
	public void setCodeString(String codeString) {
		this.codeString = codeString;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDataString() {
		return dataString;
	}
	public void setDataString(String dataString) {
		this.dataString = dataString;
	}
    
	
}
