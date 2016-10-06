package ero2.identification;

public class ErO2UserProfile {
	
	private String NFC_ID;
	
	private String sensor_group_id;
	
	private String luminocity;
	
	private String window;
	
	private String tempurature;
	
	private int status;
	
	public void setNFCID(String NFC_ID){
		this.NFC_ID = NFC_ID;
	}
	
	public void setSensorGroupID(String sensor_group_id){
		this.sensor_group_id=sensor_group_id;
	}
	
	public void setLight(String luminocity){
		this.luminocity=luminocity;
	}
	
	public void setWindow(String window){
		this.window = window;
	}
	
	public void setTemp(String tempurature){
		this.tempurature = tempurature;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public String getNFCID(){
		return NFC_ID;
	}
	
	public String getSensorGroupID(){
		return sensor_group_id;
	}
	
	public String getLight(){
		return luminocity;
	}
	
	public String getWindow(){
		return window;
	}
	
	public String getTemp(){
		return tempurature;
	}
	
	public int getStatus(){
		return status;
	}
	
}
