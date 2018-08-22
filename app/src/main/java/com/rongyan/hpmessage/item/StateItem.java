package com.rongyan.hpmessage.item;

import java.util.List;

import android.R.integer;
public class StateItem {
	
	public Device_State device_state;
	
	public List<Package_States> package_states;

	public Device_State getDevice_state() {
		return device_state;
	}

	public void setDevice_state(Device_State device_state) {
		this.device_state = device_state;
	}

	public List<Package_States> getPackage_states() {
		return package_states;
	}

	public void setPackage_states(List<Package_States> package_states) {
		this.package_states = package_states;
	}

	public static class Device_State{
		
		 public int cpu_usage;// cpu 使用率（百分比）
		 
		 public int memory_usage;// 内存使用率（百分比）
		 
		 public int disk_usage; // 磁盘使用率（百分比）
		 
         public int net_out;// 网络出流量（单位 kb）
         
         public int net_in; // 网络入流量（单位 kb）
         
         public int net_stat; // 网口状态（0 或 1）
         
         public int printer_stat; // 打印机状态（0 或 1）
         
         public int cashbox_stat; // 钱箱状态（0 或 1）
         
         public int scan_stat; // 扫描枪状态（0 或 1）
         
         public int usb1_stat; // usb1 接口状态（0 或 1）
         
         public int usb2_stat;
         
         public int usb3_stat;
         
         public int usb4_stat;
         
         public String collected_at; // 状态收集时间

		public int getCpu_usage() {
			return cpu_usage;
		}

		public void setCpu_usage(int cpu_usage) {
			this.cpu_usage = cpu_usage;
		}

		public int getMemory_usage() {
			return memory_usage;
		}

		public void setMemory_usage(int memory_usage) {
			this.memory_usage = memory_usage;
		}

		public int getDisk_usage() {
			return disk_usage;
		}

		public void setDisk_usage(int disk_usage) {
			this.disk_usage = disk_usage;
		}

		public int getNet_out() {
			return net_out;
		}

		public void setNet_out(int net_out) {
			this.net_out = net_out;
		}

		public int getNet_in() {
			return net_in;
		}

		public void setNet_in(int net_in) {
			this.net_in = net_in;
		}

		public int getNet_stat() {
			return net_stat;
		}

		public void setNet_stat(int net_stat) {
			this.net_stat = net_stat;
		}

		public int getPrinter_stat() {
			return printer_stat;
		}

		public void setPrinter_stat(int printer_stat) {
			this.printer_stat = printer_stat;
		}

		public int getCashbox_stat() {
			return cashbox_stat;
		}

		public void setCashbox_stat(int cashbox_stat) {
			this.cashbox_stat = cashbox_stat;
		}

		public int getScan_stat() {
			return scan_stat;
		}

		public void setScan_stat(int scan_stat) {
			this.scan_stat = scan_stat;
		}

		public int getUsb1_stat() {
			return usb1_stat;
		}

		public void setUsb1_stat(int usb1_stat) {
			this.usb1_stat = usb1_stat;
		}

		public int getUsb2_stat() {
			return usb2_stat;
		}

		public void setUsb2_stat(int usb2_stat) {
			this.usb2_stat = usb2_stat;
		}

		public int getUsb3_stat() {
			return usb3_stat;
		}

		public void setUsb3_stat(int usb3_stat) {
			this.usb3_stat = usb3_stat;
		}

		public int getUsb4_stat() {
			return usb4_stat;
		}

		public void setUsb4_stat(int usb4_stat) {
			this.usb4_stat = usb4_stat;
		}

		public String getCollected_at() {
			return collected_at;
		}

		public void setCollected_at(String collected_at) {
			this.collected_at = collected_at;
		}
        
	}
	
	public static class Package_States{
		
		public String package_name;
		
		public int version_code;
		
		public String version_name;
		
		public int open_times;
		
		public int net_flow;
				
		public int cpu_usage;
				
		public int memory_usage;
				
		public int disk_usage;
				
		public String collected_at; // 状态收集时间

		public String getPackage_name() {
			return package_name;
		}

		public void setPackage_name(String package_name) {
			this.package_name = package_name;
		}

		public int getVersion_code() {
			return version_code;
		}

		public void setVersion_code(int version_code) {
			this.version_code = version_code;
		}

		public String getVersion_name() {
			return version_name;
		}

		public void setVersion_name(String version_name) {
			this.version_name = version_name;
		}

		public int getCpu_usage() {
			return cpu_usage;
		}

		public void setCpu_usage(int cpu_usage) {
			this.cpu_usage = cpu_usage;
		}

		public int getMemory_usage() {
			return memory_usage;
		}

		public void setMemory_usage(int memory_usage) {
			this.memory_usage = memory_usage;
		}

		public int getDisk_usage() {
			return disk_usage;
		}

		public void setDisk_usage(int disk_usage) {
			this.disk_usage = disk_usage;
		}

		public String getCollected_at() {
			return collected_at;
		}

		public void setCollected_at(String collected_at) {
			this.collected_at = collected_at;
		}

		public int getOpen_times() {
			return open_times;
		}

		public void setOpen_times(int open_times) {
			this.open_times = open_times;
		}

		public int getNet_flow() {
			return net_flow;
		}

		public void setNet_flow(int net_flow) {
			this.net_flow = net_flow;
		}

		
	}

}
