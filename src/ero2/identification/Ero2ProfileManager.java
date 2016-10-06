package ero2.identification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.transport.coap.COAPClient;
import ero2.util.Ero2Common;

public class Ero2ProfileManager {

	ErO2Registry ERO2REGISTRY;
	Connection conn;

	public Ero2ProfileManager() {
		ERO2REGISTRY = ErO2Registry.getInstance();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/Syndesi?user=root&password=");

		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			System.out.println("class not found");
		}
	}

	public void activateProfile(String profileID) {

		// System.out.println("******** USER PROFILE ID:" + userProfile);
		try{
			long profileIDInt = Long.parseLong(profileID);
			if (profileIDInt != 0) {
				System.out.println("[ProfileManager] Activating profile: "
						+ profileID);
				// Connect with the db and get sensor_group_id and profile things
				ErO2UserProfile userProfile = getProfile(profileID);
				
				if (userProfile.getStatus() == 0) {
					String group = userProfile.getSensorGroupID();
					// currently activate basic. i.e all
					Vector<ErO2Service> services = ERO2REGISTRY
							.searchServiceGroup(group);
					if (activateBasic(services, group)) {
						updateProfileStatus(profileID, 1);
					}else{
						System.out.println("[ProfileManager] Services are not registered for "+profileID + ",group:" +group);
					}

				} else {
					String group = userProfile.getSensorGroupID();
					// currently activate basic. i.e all
					Vector<ErO2Service> services = ERO2REGISTRY
							.searchServiceGroup(group);
					if (deactivateBasic(services, group)) {
						updateProfileStatus(profileID, 0);
					}else{
						System.out.println("[ProfileManager] Services are not registered for "+profileID + ",group:" +group);
					}
				}
			} else {
				System.out.println("[ProfileManager] ProfileID not found");
			}
		}catch(NumberFormatException e){
			System.out.println("[ProfileManager] ProfileID Error");
		}
		
	}

	private boolean activateBasic(Vector<ErO2Service> services, String group) {
		// unlock the door
		String localRoot = group.substring(0, 2);
		ErO2Service rootService = ERO2REGISTRY.searchService(localRoot);
		if (rootService != null) {
			COAPClient root = new COAPClient();
			String rootURI = Ero2Common.getURI(rootService, rootService
					.getResourceByName("door").getName(), "status=open");
			String response = root.doRequest(rootURI, rootService
					.getResourceByName("door").getMethod(), null);
			System.out.println("DEBUGGING: " + response + rootURI);

			ErO2Service service = null;
			ErO2Resource resource = null;
			while (!services.isEmpty()) {
				System.out
						.println("[ProfileManager] Activating Basic resources : Trying for Bulb!");

				service = services.remove(0);
				resource = service.getResourceByName("bulb");
				if (resource == null) {
					continue;
				} else {
					break;
				}
			}

			if (resource != null) {
				System.out.println("[ProfileManager] calling resource: "
						+ resource.getName());
				COAPClient client = new COAPClient();

				String uri = Ero2Common.getURI(service, resource.getName(),
						"status=on");
				System.out.println("[ProfileManager]: activating actuators"
						+ uri);
				String responsePayload = client.doRequest(uri,
						resource.getMethod(), "");
				System.out.println("[ProfileManager]:" + responsePayload);
			}

			System.out
					.println("[ProfileManager] Profile activated succesfully");
			return true;
		} else {
			System.out.println("[ProfileManager] Unable to unlock the door");
			return false;
		}

	}

	private boolean deactivateBasic(Vector<ErO2Service> services, String group) {

		ErO2Service service = null;
		ErO2Resource resource = null;
		while (!services.isEmpty()) {
			System.out
					.println("Deactivating Basic resources : Trying for Bulb!");

			service = services.remove(0);
			resource = service.getResourceByName("bulb");
			if (resource == null) {
				continue;
			} else {
				break;
			}
		}

		if (resource != null) {
			System.out.println("calling resource: " + resource.getName());
			COAPClient client = new COAPClient();

			String uri = Ero2Common.getURI(service, resource.getName(),
					"status=off");
			System.out.println("[Deactivating] DEBUGGING2: " + uri);
			String responsePayload = client.doRequest(uri,
					resource.getMethod(), "");
			System.out.println(responsePayload);
			System.out.println("[Deactivating] Deactivated sucessfully");
			return true;
		} else {
			return false;
		}

	}

	private void activateAdvanced(Vector<ErO2Service> services) {

	}

	private ErO2UserProfile getProfile(String profileID) {
		try {
			if (profileID != null) {
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement
						.executeQuery("SELECT sensor_group_id,light_level,temp_level,window_state,status FROM profile WHERE NFC_id="
								+ profileID);

				if (resultSet.next()) {
					ErO2UserProfile profile = new ErO2UserProfile();
					String sgid = resultSet.getString("sensor_group_id");
					profile.setSensorGroupID(sgid);

					String ll = resultSet.getString("light_level");
					profile.setLight(ll);

					String tl = resultSet.getString("temp_level");
					profile.setTemp(tl);

					String ws = resultSet.getString("window_state");
					profile.setWindow(ws);

					int status = resultSet.getInt("status");
					profile.setStatus(status);

					return profile;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void updateProfileStatus(String profileID, int status) {
		Statement statement;
		try {
			statement = conn.createStatement();
			int updateStatus = statement
					.executeUpdate("UPDATE profile SET status='" + status
							+ "' WHERE NFC_id='" + profileID + "';");
			System.out.println("[ProfileManager:] profile update status "
					+ updateStatus);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
