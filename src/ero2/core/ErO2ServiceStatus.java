package ero2.core;

import java.util.Hashtable;

public class ErO2ServiceStatus {

	String ipaddr;
	Hashtable<String, String> readings = new Hashtable<String, String>();
	
	public void setIPAddr(String ipaddr){
		this.ipaddr = ipaddr;
	}
	
	public void setReading(String key, String value){
		readings.put(key, value);
	}

	public Hashtable<String, String> getReadings(){
		return readings;
	}
	
	public String getIPAddr(){
		return ipaddr;
	}
}
