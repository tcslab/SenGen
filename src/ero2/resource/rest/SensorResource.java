package ero2.resource.rest;


import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ero2.core.*;
import ero2.transport.coap.COAPClient;

/**
 * ErO2 auxiliary resource for sensor nodes to register 
 * 
 * @author Kasun Samarasinghe
 */
public class SensorResource extends LocalResource {

	ErO2Registry ERO2REGISTRY = ErO2Registry.getInstance();

	
	public SensorResource(String custom, String title, String rt) {
		super(custom);
		setTitle(title);
		setResourceType(rt);
	}
	
	public SensorResource() {
		this("sensorresource", "Sensor Service", "SensorServiceDisplayer");
	}

	public void performGET(GETRequest request) {
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		String uriPath = request.getUriPath();
		String[] uriParts=uriPath.split("_");
		String service = uriParts[0].substring(1, uriParts[0].length());
		String resourceName = uriParts[1];
		ErO2Service localservice = ERO2REGISTRY.searchService(service);
		
		COAPClient client = new COAPClient();
		String uri = getURI(localservice, resourceName, request.getUriQuery());
		System.out.println(uri);
		String responsePayload = client.doRequest(uri, localservice.getResourceByName(resourceName).getMethod(),null);
		System.out.println("response " +responsePayload);
		
		response.setPayload(responsePayload);
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
		request.respond(response);
	}

	private String getURI(ErO2Service service, String resourceName,
			String paramterString) {
		//System.out.println(resourceName);
		ErO2Resource resource = service.getResourceByName(resourceName);
		String queryString=null;
		if(paramterString!=null){
			queryString= resource.getName()+paramterString; 
		}else{
			queryString = resource.getName();
		}
		String uri = "coap://" + service.getIPAddress() + ":"
				+ COAPClient.COAPPORT + "/" + queryString;
		return uri;
	}
}
