package com.rongyan.hpmessage.item;

public class Devices {
	
	public Device device=new Device();
	
	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public static class Device{
	
		private String sn;
		
		private String fire_entity_id;
		
		private String mac;
		
		private String net_flow;
	
		public String getSn() {
			return sn;
		}
	
		public void setSn(String sn) {
			this.sn = sn;
		}
	
		public String getFire_entity_id() {
			return fire_entity_id;
		}
	
		public void setFire_entity_id(String fire_entity_id) {
			this.fire_entity_id = fire_entity_id;
		}
	
		public String getMac() {
			return mac;
		}
	
		public void setMac(String mac) {
			this.mac = mac;
		}
	
		public String getNet_flow() {
			return net_flow;
		}
	
		public void setNet_flow(String net_flow) {
			this.net_flow = net_flow;
		}
	}

}
