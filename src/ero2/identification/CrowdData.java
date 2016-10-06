package ero2.identification;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import org.json.simple.JSONObject;

/**
 * Represents sensor data sent by crowd users
 * @author Blaise
 *
 */
public class CrowdData {
    private String accountId;
    private float value;
    private String dataType;
    private Long timestamp;
    
    public CrowdData(String accountId, float data, String dataType){
    	this.accountId = accountId;
    	this.value = data;
    	this.dataType = dataType;
    	this.timestamp = System.currentTimeMillis();
    }
    
	@SuppressWarnings("unchecked")
	public JSONObject getJSON(){
		JSONObject representation = new JSONObject();
		representation.put("accountId", accountId);
		representation.put("value", value);
		representation.put("type", dataType);
		representation.put("timestamp", new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date(timestamp)));
		return representation;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
