package ero2.resource.rest;




import org.restlet.Response;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Service;
import ero2.core.ErO2ServiceStatus;
import ero2.transport.coap.COAPClient;
import ero2.util.ErO2JSON;

public class ErO2RestPullResource extends ServerResource {
	
	ErO2Registry ERO2REGISTRY;
	
	public ErO2RestPullResource(){
		ERO2REGISTRY = ErO2Registry.getInstance();
	}
	
	@Get()
	public String getUpdate() {
		String serviceLocator=getQuery().getValues("service");
		if(serviceLocator!=null){
			return serializeUpdate(serviceLocator);
		}else{
			return "Service Locator should not be null";
		}
	}
	
	private String serializeUpdate(String serviceLocator){
		ErO2Service service = ERO2REGISTRY.searchService(serviceLocator);
		COAPClient client = new COAPClient();
	    String uri = getURI(service);
	    
	    String responsePayload = client.doRequest(uri, service
	          .getResourceByName("sengen").getMethod(),null);
	    System.out.println(responsePayload);
	    if (responsePayload!=null) {
			ErO2ServiceStatus status=ERO2REGISTRY.getUpdate(serviceLocator);
			System.out.println("status is " +status);
			if(status!=null){
			String servicesJSON=ErO2JSON.getUpdatesJSONString(serviceLocator,status);
			return servicesJSON;
			}else{
				return "{error:no updates from service "+serviceLocator+"}";
			}
	    }else{
	    	return null;
	    }

	}
	
	  private String getURI(ErO2Service service){
		    String uri = "coap://" + service.getIPAddress() + ":"
		    	        + COAPClient.COAPPORT + "/sengen?status=value";   
		    return uri;
		  }
}
