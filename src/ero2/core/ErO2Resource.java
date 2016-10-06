package ero2.core;

public class ErO2Resource {

  private String name;
  private String uri;
  private String serviceLocator;
  private int number;
  private String requestMethod;
  private String queryString;
  private String timeofRegistration;

  public ErO2Resource(String name, String uri,int number,String requestMethod, String queryString, String serviceLocator){
    this.serviceLocator = serviceLocator;
    this.name = name;
    this.uri = uri;
    this.number = number;
    this.requestMethod = requestMethod;
    if(queryString!=ErO2Service.NULL){
      this.queryString = queryString;
    }
  }

  public void setName(String name){
    this.name = name;
  }

  public void setURI(String uri){
    this.uri = uri;
  }

  public void setMethod(String method){
    if("COAP_GET".equals(method)){
      method = "GET";
    }

    if("COAP_POST".equals(method)){
      method="POST";
    }
    this.requestMethod = method;
  }

  public void setQueryParameters(String queryParameters){
    this.queryString = queryParameters;
  }

  public String getName(){
    return name;
  }

  public String getServiceDesc(){
    return serviceLocator;
  }
  public String getURI(){
    return uri;
  }

  public int getNumber(){
    return number;
  }

  public String getMethod(){
    return requestMethod;
  }

  public String getQueryParameters(){
    return queryString;
  }
  
  public String getServiceLocator() {
	return serviceLocator;
}
public void setTimeofRegistration(String time) {
	this.timeofRegistration = time;
	}
public String getTimeofRegistration() {
	return timeofRegistration;
}

}
