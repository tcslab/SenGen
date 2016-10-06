package ero2.resource.rest;

import java.util.Set;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.identification.Ero2ProfileManager;
import ero2.transport.coap.COAPClient;

public class ErO2RestMediatorResource extends ServerResource {

  ErO2Registry ERO2REGISTRY;

  public ErO2RestMediatorResource() {
    ERO2REGISTRY = ErO2Registry.getInstance();
  }

  @SuppressWarnings("unused")
  @Get()
  public String callService() {
    String serviceLocator = null;
    String resourceName = null;
    String profileID = null;
    ErO2Service service = null;
    String parameterString = null;

    Set<String> parameters = getQuery().getNames();
    for (String parameter : parameters) {
      if (parameter.equals("service")) {
        serviceLocator = getQuery().getValues(parameter);
        service = ERO2REGISTRY.searchService(serviceLocator);
      }

      if (parameter.equals("resource")) {
        resourceName = getQuery().getValues(parameter);
      }

      if (parameter.equals("profileid")) {
        profileID = getQuery().getValues(parameter);
      }
    }

    if (service != null) {
      String qString = getQuery().getQueryString();
      parameterString = qString.substring(qString.indexOf("&") + 1);

      COAPClient client = new COAPClient();
      String uri = getURI(service, resourceName, parameterString);
      System.out.println(uri);
      String responsePayload = client.doRequest(uri, service
          .getResourceByName(resourceName).getMethod(),null);
      System.out.println(responsePayload);
      
      //here  goes my response
      //return "http://response :" + responsePayload + " ";
      return "http://tcslab.unige.ch/"+ responsePayload ;
    } else if (profileID != null) {
      Ero2ProfileManager profileManager = new Ero2ProfileManager();
      profileManager.activateProfile(profileID);
      return "{response:profile activated}";
    }
    return "ERROR";
    // return serializeUpdate(serviceLocator);
  }

  private String getURI(ErO2Service service, String resourceName,
      String paramterString) {
    System.out.println(resourceName);
    ErO2Resource resource = service.getResourceByName(resourceName);
    String uri = "coap://" + service.getIPAddress() + ":"
        + COAPClient.COAPPORT + "/" + resource.getName() + "?"
        + paramterString;
    
    
    return uri;
  }
  
}
