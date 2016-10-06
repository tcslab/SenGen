package ero2.resource.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ero2.core.*;
import ero2.transport.coap.COAPClient;
import ero2.transport.coap.COAPEndPoint;
import ero2.transport.coap.ErO2Utils;

/**
 * ErO2 auxiliary resource for sensor nodes to register
 *
 * @author Kasun Samarasinghe
 */
public class ServiceRegistryResource extends LocalResource {

  private ErO2Registry ERO2REGISTRY;

  private COAPEndPoint COAPENDPOINT;

  static int counter = 0;

  public ServiceRegistryResource(String custom, String title, String rt) {
    super(custom);
    setTitle(title);
    setResourceType(rt);
    ERO2REGISTRY = ErO2Registry.getInstance();
  }

  public ServiceRegistryResource() {
    this("register", "Register Service", "RegisterServiceDisplayer");
  }

  public void performPOST(POSTRequest request) {
    Response response = new Response(CodeRegistry.RESP_CONTENT);

    //Should check the IP too
    String sqkey=request.sequenceKey();
    String ipaddr=request.sequenceKey().substring(0, sqkey.lastIndexOf(':'));
    if(parseMessage(ipaddr,request.getPayloadString())){
      response.setPayload(ErO2Utils.REGISTRATION_SUCCESSFUL);
      System.out.println("Registration successful for " + ipaddr);
      //Register with global registry

    }else{
      response.setPayload(ErO2Utils.REGISTRATION_FAILED);
    }

    response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
    request.respond(response);
  }

  private boolean parseMessage(String ipaddr,String payloadString){

    String serviceLocator=payloadString.substring(0,payloadString.indexOf(ErO2Service.DELIMITER));
    String serviceDescriptor=payloadString.substring(payloadString.indexOf(ErO2Service.DELIMITER)+1,payloadString.lastIndexOf(ErO2Service.DELIMITER)-1);
    StringTokenizer serviceTokenizer=new StringTokenizer(serviceDescriptor, ErO2Service.DELIMITER);
    System.out.println("payload: "+payloadString);
    //System.out.println("sd "+serviceDescriptor);
    //if(serviceDescriptor==null){
    ////  System.out.println("SD null");
    //}else{
    //  System.out.println("SD good");
    //}

    ErO2Service service;
    ErO2Resource resource=null;
    //while(serviceTokenizer.hasMoreTokens()){
      service=new ErO2Service(ipaddr);
      String name=null,uri=null,method=null,queryString=null;

      //String attributeDescriptor = serviceTokenizer.nextToken();
      StringTokenizer attributeTokenizer=new StringTokenizer(serviceDescriptor, ErO2Service.COMMA);
      System.out.println("ad "+serviceDescriptor);
      int attributeno=0;
      String attribute=null;
      boolean badresource=false;
      while(attributeTokenizer.hasMoreTokens() & !badresource){
        attribute=attributeTokenizer.nextToken();
        switch(attributeno){
          case 0:
            name=attribute;
            System.out.println("name:"+name+".");
            if(name!=" "){
              counter++;
              resource=new ErO2Resource(name, uri, counter, method, queryString,serviceLocator);
              resource.setTimeofRegistration(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
            }else{
              badresource = true;
            }
            break;
          case 1:
            uri=attribute;
            if(uri!=""){
              resource.setURI(uri);
            }else{
              badresource = true;
            }
            break;
          case 2:
            method=attribute;
            if(method != ""){
              resource.setMethod(method);
            }else{
              badresource = true;
            }
            break;
          case 3:
            queryString=attribute;
            resource.setQueryParameters(queryString);
            break;
        }
        attributeno++;
      }

      if(resource!=null){
        service.addResource(resource);
        System.out.println("Registering service "+service.getSerialization());
        ERO2REGISTRY.registerService(serviceLocator, service);

        SensorResource resourceDuplicate=new SensorResource(serviceLocator+"_"+resource.getName(),resource.getName(),"something"); //check
        COAPENDPOINT.addResource(resourceDuplicate);

        COAPClient client=new COAPClient();
        //client.doRequest("coap://89.216.116.166:5684/rd/res", "POST", "<coap://129.194.70.70:5638>;title=Orestis;if=Interface2;rt=sensor;tag=telosb;uid=orestis-office");

      }
    //}

    if(resource!=null){
      return true;
    }else{
      return false;
    }
  }

  public void setCOAPEndPoint(COAPEndPoint coapep){
    this.COAPENDPOINT = coapep;
  }
}
