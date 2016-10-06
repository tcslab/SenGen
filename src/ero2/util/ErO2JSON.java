package ero2.util;

import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.http.client.utils.URIBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.core.ErO2ServiceStatus;

public class ErO2JSON {

  @SuppressWarnings("unchecked")
  public static String getServicesJSONString(
      Hashtable<String, ErO2Service> serviceRegistry) {

    Enumeration<String> serviceKeys = serviceRegistry.keys();
    String ip = "129.194.69.178";
    URIBuilder builder = null;
    URIBuilder uri_b = null;
    //uri_b.addParameter("t", "search");
    //builder.scheme("http")
	//.autority(ip+":8011")
        //.appendPath("ero2proxy")
        //.appendPath("monitor")
        //.appendQueryParam("name", "pune");
    URI urla = null;
    //uri_b.setScheme("http").setHost(ip).setPort(8011).setPath("/ero2proxy/monitor");
    URL myURL=null;

    JSONObject finalJSON = new JSONObject();
    JSONArray servicesJSON = new JSONArray();
    while (serviceKeys.hasMoreElements()) {
      String serviceLocator = serviceKeys.nextElement();
    try {
//	builder = new URIBuilder();
//	builder.setScheme("http").setHost("www.google.com").setPath("/search")
//            .setParameter("q", "httpclient")
//            .setParameter("btnG", "Google Search")
//            .setParameter("aq", "f")
//            .setParameter("oq", "");
    myURL = new URL("http://129.194.70.52:8011/");
    //uri_b = new URIBuilder("http://example.com");
    //urla = uri_b.build();
    //urla = builder.build();
    } 
    catch (MalformedURLException e) {
        // exception handler code here
        // ...
    }
      
//      try
//      {
        //uri_b.addParameter("service",serviceLocator);
//	uri_b.addParameter("service","puree");

//	uri = uri_b.build();
//      }
      //catch (URISyntaxException e)
//      catch (Exception e)
//      {
//      	System.out.println("Exception building URI [" + uri.toString() + "]");
	//out = respon
      	//e.printStackTrace(out);
      	// No point in continuing...
//      	return null;
//      }
      //System.out.println(serviceLocator);
      ErO2Service service = serviceRegistry.get(serviceLocator);
      String luminance      = service.getLuminanceValue();
      String temperature    = service.getTemperatureValue();
	  String humidity       = service.getHumidity_value();
      String timestamp		= service.getTimestamp();
	  
	  //HACK TO GET THE FLOATING POINT FOR HUMIDITY AND TEMPERATURE
      if (temperature!=null) {
    	 int temper = Integer.valueOf(temperature.replaceAll("\\D+",""));
    	 float temper2 = (float) temper / 100;
    	 temperature = Float.toString(temper2);
      }
      if (humidity!=null) {
    	  int humid = Integer.valueOf(humidity.replaceAll("\\D+","")); 
    	  if (humid>9999) {
    		  String t = Integer.toString(humid);
    		  t = t.substring(0,4);
    		  humid = Integer.valueOf(t);
    		  System.out.println("fixed hum = " + humid);
    	  }
          float humid2 = (float) humid / 100;
          humidity = Float.toString(humid2) + " %" ;
      }
	  
      // []
      Vector<ErO2Resource> resources = service.getResources();
      JSONArray resourcesJSON = new JSONArray();

      JSONObject nodeJSON;
      for (ErO2Resource ero2Resource : resources) {
        if (ero2Resource.getName() != null
            && ero2Resource.getMethod() != null) {
  	      System.out.println("RESOURCE NAME IS : " + ero2Resource.getName() );
          nodeJSON = new JSONObject();
          String hostname = "node_"+ero2Resource.getNumber()+".unige";
       	  String node_id = serviceLocator;
          
    	  nodeJSON = new JSONObject();
		  nodeJSON.put("hardware", "telosb");
		  nodeJSON.put("node_id", node_id);
		  nodeJSON.put("protocol", "httpUnige");
		  nodeJSON.put("ip", ip);
		  nodeJSON.put("uri", ip); //put ip instead of myURL.tostring because getNodes() in sfawrap takes the ip from here..  
		  nodeJSON.put("hostname", hostname);
		  nodeJSON.put("type", "sensor-actuator");
		  nodeJSON.put("port", "8111");
		//luminance sensor
		  JSONObject nodeResourceJSONlumSen = new JSONObject();
		  nodeResourceJSONlumSen.put("timestamp", timestamp);
		  nodeResourceJSONlumSen.put("timeofRegistration", ero2Resource.getTimeofRegistration());
		  nodeResourceJSONlumSen.put("data_type", "true");
		  nodeResourceJSONlumSen.put("path", "/ero2proxy/monitor?service=" + serviceLocator);
		  nodeResourceJSONlumSen.put("type", "ipso.sen.ill");
		  nodeResourceJSONlumSen.put("illuminance", luminance);
		  nodeResourceJSONlumSen.put("temperature", temperature);
		  nodeResourceJSONlumSen.put("humidity", humidity);
		  nodeResourceJSONlumSen.put("name", ero2Resource.getName() + " at UNIGE with NID: " + node_id);
		  nodeResourceJSONlumSen.put("unit", "lux");
		  nodeJSON.put("resourcesnode",nodeResourceJSONlumSen);
		  resourcesJSON.add(nodeJSON);
          
          
        }
      }
      JSONObject serviceJSON = new JSONObject();
      /*  serviceJSON.put("serviceID", serviceLocator); */
       serviceJSON.put("resources", resourcesJSON);

       servicesJSON.add(serviceJSON);
      // {service
      
    }
    finalJSON.put("services", servicesJSON);

    StringWriter out = new StringWriter();
    try {
      finalJSON.writeJSONString(out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String jsonText = out.toString();
    return jsonText;
  }

  @SuppressWarnings("unchecked")
  public static String getTestbedInfoJSONString(){
    String name      = "unigetestbed";
    double longitude = 46.1767058;
    double latitude  = 6.1397209;
    String domain    = "iot.unige.ch:8111";
    JSONObject finalJSON = new JSONObject();
    finalJSON.put("name", name);
    finalJSON.put("longitude", longitude);
    finalJSON.put("latitude", latitude);
    finalJSON.put("domain", domain);
    StringWriter out = new StringWriter();
    try {
      finalJSON.writeJSONString(out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String jsonText = out.toString();
    System.out.println(jsonText);
    return jsonText;
  }

  @SuppressWarnings("unchecked")
  public static String getUpdatesJSONString(String serviceLocator,
      ErO2ServiceStatus status) {
    JSONObject readings = new JSONObject();
    readings.put("service", serviceLocator);
    Enumeration<String> readingsKeys = status.getReadings().keys();
     while (readingsKeys.hasMoreElements()) {
      String key = readingsKeys.nextElement();
      System.out.println(key + " and  " + status.getReadings().get(key));
      float humid2 = 0;
      if (key.equals("humidity")) {
    	  int humid = Integer.valueOf(status.getReadings().get(key).replaceAll("\\D+",""));
    	  if (humid>9999) {
     		  String t = Integer.toString(humid);
     		  t = t.substring(0,4);
     		  humid = Integer.valueOf(t);
     		  System.out.println("fixed hum = " + humid);
     	  }
    	  humid2 = (float) humid / 100;  
    	  readings.put(key, humid2 );
      }
      else if (key.equals("temperature")){
    	 int temper = Integer.valueOf(status.getReadings().get(key).replaceAll("\\D+",""));
      	 float temper2 = (float) temper / 100;
      	 readings.put(key, temper2 );
      }
      else
    	  readings.put(key, status.getReadings().get(key));
    }

    StringWriter out = new StringWriter();
    try {
      readings.writeJSONString(out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String jsonText = out.toString();
    return jsonText;
  }

  @SuppressWarnings("unchecked")
  public static String getTestbedSensorValuesJSONString(Hashtable<String, ErO2Service> serviceRegistry) {

    Enumeration<String> serviceKeys = serviceRegistry.keys();

    JSONObject finalJSON   = new JSONObject();
    JSONArray servicesJSON = new JSONArray();
    while (serviceKeys.hasMoreElements()) {
      String serviceLocator = serviceKeys.nextElement();
      ErO2Service ero2ser   = serviceRegistry.get(serviceLocator);
      String luminance      = ero2ser.getLuminanceValue();
      String temperature    = ero2ser.getTemperatureValue();


      JSONObject nodeJSON   = new JSONObject();
      nodeJSON.put("node", serviceLocator);
      nodeJSON.put("illuminance", luminance);
      nodeJSON.put("temperature", temperature);
      servicesJSON.add(nodeJSON);
    }
    finalJSON.put("services", servicesJSON);

    StringWriter out = new StringWriter();
    try {
      finalJSON.writeJSONString(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String jsonText = out.toString();
    System.out.println(jsonText);
    return jsonText;
  }

}
