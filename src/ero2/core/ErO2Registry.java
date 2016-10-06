package ero2.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ErO2Registry {

  private static final ErO2Registry instance= new ErO2Registry();
  private Hashtable<String, ErO2Service> registry;
  private Hashtable<String, ErO2ServiceStatus> monitor;

  private ErO2Registry(){
    registry = new Hashtable<String, ErO2Service>();
    monitor = new Hashtable<String, ErO2ServiceStatus>();
  }

  public static ErO2Registry getInstance(){
    return instance;
  }

  public void  registerService(String servicePointer,ErO2Service service){
    registry.put(servicePointer, service);
    System.out.println("ErO2: Service registerd: "+service.getSerialization());
  }

  public ErO2Service searchService(String serviceLocator){
    return registry.get(serviceLocator);
  }

  public void updateService(String serviceLocator,String luminance, String temperature, String humidity, String timestamp){
    ErO2Service ero2ser = registry.get(serviceLocator);
    ero2ser.setLuminance(luminance);
    ero2ser.setTemperature(temperature);
    ero2ser.setTimestamp(timestamp);
    ero2ser.setHumidity_value(humidity.replaceAll("[^a-zA-Z0-9\\s+]", ""));
    registry.put(serviceLocator,ero2ser);
    // Logger logger = Logger.getLogger("AliLogger");
    // logger.log(Level.SEVERE, "ALI|ErO2Registry|registry.get("+serviceLocator+").getTemperatureValue: "+registry.get(serviceLocator).getTemperatureValue());
  }

  public Vector<ErO2Service> searchServiceGroup(String groupID){
    Enumeration<String> keys=registry.keys();
    Vector<ErO2Service> services=new Vector<ErO2Service>();
    while(keys.hasMoreElements()){
      String key = keys.nextElement();
      if(key.startsWith(groupID)){
        services.add(registry.get(key));
      }
    }

    return services;
  }

  public Hashtable<String, ErO2Service> allServices(){
    return registry;
  }

  public void updateStatus(String serviceLocator, ErO2ServiceStatus status){
    monitor.put(serviceLocator, status);
  }

  public ErO2ServiceStatus getUpdate(String serviceLocator){
    return monitor.get(serviceLocator);
  }

}


