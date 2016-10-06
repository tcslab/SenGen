package ero2.resource.rest;

import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import ero2.identification.CrowdController;
import ero2.identification.CrowdUser;

/**
 * Rest resource to view / add users
 * @author Blaise
 *
 */
public class CrowdRestUserResource extends ServerResource {
	private CrowdController crowdController;

	public CrowdRestUserResource(){
		this.crowdController = CrowdController.getInstance();
	}

	@SuppressWarnings("unchecked")
	@Get("json")
	public String getUsers() {
		JSONObject listUsers = crowdController.getJSON();
		return listUsers.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	@Post()
	public String addUser(String request) throws IOException {
		JSONObject userJSON = (JSONObject) JSONValue.parse(request);
		JSONArray availableSensorsArray = (JSONArray) userJSON.get("mAvailableSensors");
		ArrayList<String> availableSensors = new ArrayList<>();
		for(int i = 0; i < availableSensorsArray.size(); i++){
			availableSensors.add((String)availableSensorsArray.get(i));
		}
		CrowdUser newUser = new CrowdUser((String)userJSON.get("mId"), getRequest().getClientInfo().getAddress(), getRequest().getClientInfo().getAgent(), (String)userJSON.get("mName"), (String)userJSON.get("mSurname"), (String)userJSON.get("mOffice"), ((Number)userJSON.get("mTargetLight")).intValue(), ((Number)userJSON.get("mTargetTemp")).intValue(), availableSensors);
		crowdController.getUsers().add(newUser);
		JSONObject response = new JSONObject();
		response.put("user update", "success");
		return response.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	@Put()
	public String updateUser(String request) throws IOException {
		JSONObject userJSON = (JSONObject) JSONValue.parse(request);
		JSONArray availableSensorsArray = (JSONArray) userJSON.get("mAvailableSensors");
		ArrayList<String> availableSensors = new ArrayList<>();
		for(int i = 0; i < availableSensorsArray.size(); i++){
			availableSensors.add((String)availableSensorsArray.get(i));
		}
		CrowdUser updatedUser = new CrowdUser((String)userJSON.get("mId"), getRequest().getClientInfo().getAddress(), getRequest().getClientInfo().getAgent(), (String)userJSON.get("mName"), (String)userJSON.get("mSurname"), (String)userJSON.get("mOffice"), ((Number)userJSON.get("mTargetLight")).intValue(), ((Number)userJSON.get("mTargetTemp")).intValue(), availableSensors);
		ArrayList<CrowdUser> users = crowdController.getUsers();
		if(users.contains(updatedUser)){
			CrowdUser oldUser = users.get(users.indexOf(updatedUser));
			oldUser.setName(updatedUser.getName());
			oldUser.setSurname(updatedUser.getSurname());
			oldUser.setOffice(updatedUser.getOffice());
			oldUser.setTargetLight(updatedUser.getTargetLight());
			oldUser.setTargetTemp(updatedUser.getTargetTemp());
			oldUser.setAvailableSensors(updatedUser.getAvailableSensors());
		}else{
			users.add(updatedUser);
		}
		JSONObject response = new JSONObject();
		response.put("user upload", "success");
		return response.toJSONString();
	}
	
}
