package ero2.identification;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.simple.JSONObject;

public class CrowdNode {
    private String mNID;
    private NodeType mType;
    private String mStatus;
    private float luminance;
    private float temperature;
    private Long LastChangeTS;

    public CrowdNode(String NID, NodeType type, String status){
        this.mNID = NID;
        this.mType = type;
        this.mStatus = status;
    }
    
	@SuppressWarnings("unchecked")
	public JSONObject getJSON(){
		JSONObject representation = new JSONObject();
		representation.put("NID", mNID);
		representation.put("type", mType.toString());
		representation.put("status", mStatus);
		representation.put("luminance", luminance);
		representation.put("temperature", temperature);
		if(LastChangeTS != null){
			representation.put("timestamp", new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date(LastChangeTS)));
		}
		else{
			representation.put("timestamp", new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date(System.currentTimeMillis())));
		}
		return representation;
	}

    public String getmNID() {
        return mNID;
    }

    public void setmNID(String mNID) {
        this.mNID = mNID;
    }

    public NodeType getmType() {
        return mType;
    }

    public void setmType(NodeType mType) {
        this.mType = mType;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }
    
    public float getLuminance() {
        return luminance;
    }

    public void setLuminance(float luminance) {
        this.luminance = luminance;
    }
    
    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

	public Long getLastChangeTS() {
		return LastChangeTS;
	}

	public void setLastChangeTS(Long lastChangeTS) {
		LastChangeTS = lastChangeTS;
	}
}
