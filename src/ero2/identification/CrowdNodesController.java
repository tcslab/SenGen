package ero2.identification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CrowdNodesController extends TimerTask{
	private static CrowdNodesController instance;
	private static CrowdNodesRegulator nodesRegulator;
	private ArrayList<CrowdNode> nodesList;
	private String url = "http://129.194.70.52:8111/ero2proxy";
	
	public CrowdNodesController(){
		this.nodesRegulator = CrowdNodesRegulator.getInstance(this);
		this.nodesList = new ArrayList<>();
		//Update the nodes list every 30 minutes
		new Timer().schedule(this, 0, 1800000);
	}
	
	public static synchronized CrowdNodesController getInstance(){
		if(instance == null){
			instance = new CrowdNodesController();
		}
		return instance;
	}
	
	/**
	 * The task to execute, populate the nodes list with nodes reporting data from the server
	 */
	@Override
	public void run() {
		this.nodesList.clear();
		this.fetchNodes();
		this.fetchValues();
	}
	
	/**
	 * Send a mediate request to turn a node on or off
	 * @param node
	 * @param status
	 */
	public void sendMediateRequest(CrowdNode node, String status){
		String request_url = url+"/mediate?service="+node.getmNID()+"&resource="+node.getmType()+"&status="+status;
		System.out.println(request_url);
		
		try {
			URL urlObj = new URL(request_url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = inputStream.readLine()) != null) {
				response.append(inputLine);
			}
			inputStream.close();
			System.out.println("RESPONSE: "+response.toString());
			//If node is not found, request new nodes list
			if(response.equals("ERROR")){
				this.nodesList.clear();
				this.fetchNodes();
			}else{
				node.setLastChangeTS(System.currentTimeMillis());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch nodes from the server url
	 */
	public void fetchNodes(){
		String request_url = url+"/service/type/xml_rspec";
		StringBuffer response = null;
		
		try {
			URL urlObj = new URL(request_url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			response = new StringBuffer();
	 
			while ((inputLine = inputStream.readLine()) != null) {
				response.append(inputLine);
			}
			inputStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		this.parseNodes(response.toString());
	}
	
	/**
	 * Create a new list of nodes from the xml response of the server
	 * @param response
	 */
	public void parseNodes(String response){
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(response)));

            NodeList nl = document.getElementsByTagName("gpio");
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                Element e = (Element) n.getFirstChild();
                String device = e.getAttribute("name");
                NodeType nodeType = NodeType.getType(device);
                this.addNode(new CrowdNode(device.substring(device.indexOf("NID: ")+5, device.length()), nodeType, nodeType.getStatus("default")));
                System.out.println(nodeType.toString()+": "+device);
            }
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch all the values reported by the fixes nodes from the server url
	 */
	public void fetchValues(){
		String request_url = url+"/sensorvalues";
		StringBuffer response = null;
		
		try {
			URL urlObj = new URL(request_url);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			response = new StringBuffer();
	 
			while ((inputLine = inputStream.readLine()) != null) {
				response.append(inputLine);
			}
			inputStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		this.parseValues(response.toString());
	}
	
	/**
	 * Parse the nodes values from the JSON response of the server
	 * @param response
	 */
	public void parseValues(String response){
		JSONObject valuesJSON = (JSONObject) JSONValue.parse(response);
		System.out.println(valuesJSON.toString());
		JSONArray nodesListJSON = (JSONArray) valuesJSON.get("services");
		for(Object node: nodesListJSON){
			JSONObject nodeJSON = (JSONObject)node;
			CrowdNode crowdNode = getNode((String)nodeJSON.get("node"));
			crowdNode.setLuminance(Float.valueOf((String)nodeJSON.get("luminance")));
			crowdNode.setTemperature(Float.valueOf((String)nodeJSON.get("temperature")));
		}
	}
	
	/**
	 * Get the room average value for the data type passsed in parameter in the room of the data passed in parameter
	 * @param data
	 * @return
	 */
	public float getRoomAverageValue(CrowdData data){
		float averageValue = 0;
		int nbNodes = 0;
		for(CrowdNode node : nodesList){
			if(node.getmNID().substring(0, 2).equals(CrowdController.getInstance().getUser(data.getAccountId()).getOffice())){
				if(data.getDataType().equals("LIGHT")){
					averageValue += node.getLuminance();
				}else if(data.getDataType().equals("TEMPERATURE")){
					averageValue += node.getTemperature();
				}
				nbNodes++;
			}
		}
		return averageValue/nbNodes;
	}
	
	/**
	 * Get the JSON formatted list of nodes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getNodesJSON(){
		JSONObject representation = new JSONObject();
		JSONObject listData = new JSONObject();
		JSONArray listDataArray = new JSONArray();
		for(CrowdNode node : nodesList){
			listDataArray.add(node.getJSON());
		}
		representation.put("Physical&Virtual", listDataArray);
		return representation;
	}

	/**
	 * Add a node to the list of nodes in the manager
	 * @param node
	 */
	public void addNode(CrowdNode node){
		Boolean nodeExist = false;
        for(CrowdNode currentNode : nodesList){
            if(currentNode.getmNID().equals(node.getmNID())) {
                currentNode.setmStatus(node.getmStatus());
                nodeExist = true;
            }
        }
        if(!nodeExist){
        	nodesList.add(node);
        }
	}
	
	/**
	 * Get the node with the NID passed in parameter
	 * @param NID
	 * @return
	 */
	public CrowdNode getNode(String NID){
		for(CrowdNode node: nodesList){
			if(node.getmNID().equals(NID)){
				return node;
			}
		}
		return null;
	}
	
	public ArrayList<CrowdNode> getNodesList(){
		return nodesList;
	}
	
	public static CrowdNodesRegulator getNodesRegulator() {
		return nodesRegulator;
	}

	public static void setNodesRegulator(CrowdNodesRegulator nodesRegulator) {
		CrowdNodesController.nodesRegulator = nodesRegulator;
	}

}
