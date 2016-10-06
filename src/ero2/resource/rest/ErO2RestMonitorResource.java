package ero2.resource.rest;



import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2ServiceStatus;
import ero2.util.ErO2JSON;

public class ErO2RestMonitorResource extends ServerResource {
	
	ErO2Registry ERO2REGISTRY;
	
	public ErO2RestMonitorResource(){
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
		ErO2ServiceStatus status=ERO2REGISTRY.getUpdate(serviceLocator);
		if(status!=null){
		String servicesJSON=ErO2JSON.getUpdatesJSONString(serviceLocator,status);
		return servicesJSON;
		}else{
			return "{error:no updates from service "+serviceLocator+"}";
		}
	}
}