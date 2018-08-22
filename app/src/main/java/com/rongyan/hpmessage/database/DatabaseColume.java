package com.rongyan.hpmessage.database;

import java.io.Serializable;

public class DatabaseColume {
	
	public static final String DB_NAME = "message.db";//数据库名称
	
	public static final String MESSAGE_AD="1";
	
	public static final String MESSAGE="2";
	
	public static class MessageInfo implements Serializable {

		public static final String TABLENAME = "message_list";//表名
		
		public static final String ID = "message_id";//消息id
		
		public static final String TITLE = "message_title";//消息标题
		
		public static final String SUMMARY = "message_summary";//消息概述
		
		public static final String ISREAD = "isread";//是否已读 0未读，1已读
		
		public static final String TIME="time";//消息时间
		
		public static final String PREVIEW_ICON = "iconurl";
		
		public static final String TYPE="type";//消息类型,1广告，2新消息
	}
	
	public static class AdInfo implements Serializable {

		public static final String TABLENAME = "ad_list";//表名
		
		public static final String ID = "ad_id";//广告id
		
		public static final String TIME = "ad_time";//广告显示时间
		
		public static final String NUMBER = "ad_number";//广告主动弹出次数
	}
	
	static class AppInfo implements Serializable {

		public static final String TABLENAME = "deviceservice_appinfo";
		
		public static final String PKGNAME = "pkgname";
		
		public static final String PKGVERSIONSTRING = "pkgversion";
		
		public static final String PKGVERSIONCODESTRING = "pkgversioncode";
		
		public static final String USEDTIMESTRING = "usedcount";
	}

}
