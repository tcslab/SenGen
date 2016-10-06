package ero2.resource.rest;


import java.util.Hashtable;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Service;
import ero2.util.ErO2JSON;

public class ErO2RestRegistryResource extends ServerResource {

  ErO2Registry ERO2REGISTRY;

  public ErO2RestRegistryResource(){
    ERO2REGISTRY = ErO2Registry.getInstance();
  }

  @Get()
  public String getServices() {
    return serializeRegistry();
  }

  private String serializeRegistry(){
    Hashtable<String, ErO2Service> serviceRegistry=ERO2REGISTRY.allServices();
    String servicesJSON=ErO2JSON.getServicesJSONString(serviceRegistry);
    return servicesJSON;
  }
}
