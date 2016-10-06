package ero2.resource.rest;

import java.util.Hashtable;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Service;
import ero2.util.ErO2JSON;

public class ErO2RestSensValues extends ServerResource {

  ErO2Registry ERO2REGISTRY;

  public ErO2RestSensValues() {
    ERO2REGISTRY = ErO2Registry.getInstance();
  }

  @Get()
  public String getTestbedSensorValues() {
    return retreiveTestbedSensorValues();
  }

  private String retreiveTestbedSensorValues(){
    Hashtable<String, ErO2Service> serviceRegistry=ERO2REGISTRY.allServices();
    String sensorValueJSON=ErO2JSON.getTestbedSensorValuesJSONString(serviceRegistry);
    return sensorValueJSON;
  }

}
