package com.example.telecom.util;

import java.util.HashMap;
import java.util.Map;

public class CustomerUsageObject {
	
	private Map<String,Integer> monUsage = new HashMap<String, Integer>();
	
	public Map<String, Integer> getMonUsage() {
		return monUsage;
	}


	public void addUsage(String monYear, Integer used) {
		if (monUsage.containsKey(monYear)) {
			Integer totalUsed = monUsage.get(monYear);
			totalUsed = totalUsed + used;
			monUsage.put(monYear, totalUsed);
		} else {
			System.out.println("Adding usage " +used+ " for month -"+monYear);
			monUsage.put(monYear, used);
		}
	}
}
