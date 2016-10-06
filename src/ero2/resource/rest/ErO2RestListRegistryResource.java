package ero2.resource.rest;


import java.util.Hashtable;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Service;
import ero2.util.ErO2JSON;

public class ErO2RestListRegistryResource extends ServerResource {



  public ErO2RestListRegistryResource(){}

  @Get()
  public String getTestbedInfo() {
    return retreiveTestbedInfo();
  }

  private String retreiveTestbedInfo(){
    String testbedinfoJSON=ErO2JSON.getTestbedInfoJSONString();
    return testbedinfoJSON;
  }
}
