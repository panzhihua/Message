package com.rongyan.hpmessage.item;

import java.util.List;

public class States {

	private List<StateItem> states;
	
	private String device_sn;
	
    public void setStates(List<StateItem> states) {
         this.states = states;
     }
     public List<StateItem> getStates() {
         return states;
     }
	public String getDevice_sn() {
		return device_sn;
	}
	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}
     
}
