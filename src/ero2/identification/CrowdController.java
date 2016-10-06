package ero2.identification;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Singleton controller to manage crowd users
 * @author Blaise
 *
 */
public class CrowdController {
	private static CrowdController instance;
	private static CrowdNodesController nodesManager;
	private ArrayList<CrowdUser> users;
	
	public CrowdController(){
		this.users = new ArrayList<CrowdUser>();
		this.nodesManager = CrowdNodesController.getInstance();
	}
	
	public static synchronized CrowdController getInstance(){
		if(instance == null){
			instance = new CrowdController();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSON(){
		JSONObject representation = new JSONObject();
		JSONArray listUsers = new JSONArray();
		for(CrowdUser user : users){
			listUsers.add(user.getJSON());
		}
		representation.put("users", listUsers);
		return representation;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getDataJSON(){
		JSONObject representation = new JSONObject();
		JSONObject listData = new JSONObject();
		JSONArray listDataArray = new JSONArray();
		for(CrowdUser user : users){
			JSONArray listDataUser = new JSONArray();
			for(CrowdData data : user.getLastDatas()){
				if((System.currentTimeMillis()-data.getTimestamp()) < 300000 ){
					listDataUser.add(data.getJSON());
				}
			}
			listData.put(user.getId(),listDataUser);
			listDataArray.add(listData);
		}
		representation.put("mobile", listDataArray);
		return representation;
	}
	
	public void setLastData(CrowdData lastData){
		CrowdUser user = this.getUser(lastData.getAccountId());
		user.incrementCrowPoints();
		Boolean dataExist = false;
		for(CrowdData data : user.getLastDatas()){
			if(data.getDataType().equals(lastData.getDataType())){
				data.setValue(lastData.getValue());
				data.setTimestamp(System.currentTimeMillis());
				dataExist = true;
				break;
			}
		}
		if(!dataExist){
			user.getLastDatas().add(lastData);
		}
		//Light and temperature automation
		if(lastData.getDataType().equals("LIGHT")){
			nodesManager.getNodesRegulator().regulateLight(lastData);
		}else if(lastData.getDataType().equals("TEMPERATURE")){
			nodesManager.getNodesRegulator().regulateTemp(lastData);
		}
	}
	
	public float getRoomAveragePref(CrowdData data){
		float averagePref = 0;
		int nbUsers = 0;
		for(CrowdUser user : users){
			if(user.getOffice().equals(getUser(data.getAccountId()).getOffice())){
				if(data.getDataType().equals("LIGHT")){
					averagePref += user.getTargetLight();
				}else if(data.getDataType().equals("TEMPERATURE")){
					averagePref += user.getTargetTemp();
				}
				nbUsers++;
			}
		}
		return averagePref/nbUsers;
	}
	
	public float getWeightedRoomAveragePref(CrowdData data){
		float averagePref = 0;
		int nbCrowdPoints = 0;
		for(CrowdUser user : users){
			if(user.getOffice().equals(getUser(data.getAccountId()).getOffice())){
				if(data.getDataType().equals("LIGHT")){
					averagePref += user.getTargetLight() * user.getCrowdPoints();
				}else if(data.getDataType().equals("TEMPERATURE")){
					averagePref += user.getTargetTemp() * user.getCrowdPoints();
				}
				nbCrowdPoints += user.getCrowdPoints();
			}
		}
		return averagePref/nbCrowdPoints;
	}
	
	public CrowdUser getUser(String userId){
		for(CrowdUser user : this.users){
			if(user.getId().equals(userId)){
				return user;
			}
		}
		return null;
	}
	
	public ArrayList<CrowdUser> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<CrowdUser> users) {
		this.users = users;
	}
}
