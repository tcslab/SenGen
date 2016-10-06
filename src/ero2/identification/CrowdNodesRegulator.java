package ero2.identification;

public class CrowdNodesRegulator {
	private static CrowdNodesRegulator instance;
	private static CrowdNodesController nodesManager;
	
	public CrowdNodesRegulator(CrowdNodesController nodesManager){
		this.nodesManager = nodesManager;
	}
	
	public static synchronized CrowdNodesRegulator getInstance(CrowdNodesController nodesManager){
		if(instance == null){
			instance = new CrowdNodesRegulator(nodesManager);
		}
		return instance;
	}
	
	public void regulateTemp(CrowdData newTempData){
		long lastChangeTs;
		if((newTempData.getValue()+nodesManager.getRoomAverageValue(newTempData))/2 < CrowdController.getInstance().getWeightedRoomAveragePref(newTempData)){
			for(CrowdNode node : nodesManager.getNodesList()){
				if(node.getmNID().substring(0, 2).equals(CrowdController.getInstance().getUser(newTempData.getAccountId()).getOffice())){
					if(node.getmType() == NodeType.fan){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node,"off");
						}
					}
					if(node.getmType() == NodeType.heater){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node, "on");
						}
					}
				}
			}
		}else{
			for(CrowdNode node : nodesManager.getNodesList()){
				if(node.getmNID().substring(0, 2).equals(CrowdController.getInstance().getUser(newTempData.getAccountId()).getOffice())){
					if(node.getmType() == NodeType.fan){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node, "on");
						}
					}
					if(node.getmType() == NodeType.heater){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node, "off");
						}
					}
				}
			}
		}
	}

	public void regulateLight(CrowdData newLightData){
		long lastChangeTs;
		if((newLightData.getValue()+nodesManager.getRoomAverageValue(newLightData))/2 < CrowdController.getInstance().getWeightedRoomAveragePref(newLightData)){
			for(CrowdNode node : nodesManager.getNodesList()){
				if(node.getmNID().substring(0, 2).equals(CrowdController.getInstance().getUser(newLightData.getAccountId()).getOffice())){
					if(node.getmType() == NodeType.bulb){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node, "on");
						}
					}
					if(node.getmType() == NodeType.curtain){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node, "up");
						}
					}
				}
			}
		}else{
			for(CrowdNode node : nodesManager.getNodesList()){
				if(node.getmNID().substring(0, 2).equals(CrowdController.getInstance().getUser(newLightData.getAccountId()).getOffice())){
					if(node.getmType() == NodeType.bulb){
						//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
						if(node.getLastChangeTS() == null){
							lastChangeTs = 0;
						}else{
							lastChangeTs = node.getLastChangeTS();
						}
						if(System.currentTimeMillis()-lastChangeTs > 300000){
							node.setLastChangeTS(System.currentTimeMillis());
							nodesManager.sendMediateRequest(node, "off");
						}
					}
					//If direct sunlight -> close curtains
					if(newLightData.getValue() > 10000){
						if(node.getmType() == NodeType.curtain){
							//Change the node status only if it haven't been changed in the last 5 minutes to avoid annoying the users
							if(node.getLastChangeTS() == null){
								lastChangeTs = 0;
							}else{
								lastChangeTs = node.getLastChangeTS();
							}
							if(System.currentTimeMillis()-lastChangeTs > 300000){
								node.setLastChangeTS(System.currentTimeMillis());
								nodesManager.sendMediateRequest(node, "down");
							}
						}
					}
				}
			}
		}
	}
	
}
