package ero2.transport.coap;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;
import ero2.core.ErO2Registry;
import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.resource.rest.ServiceMonitorResource;
import ero2.resource.rest.ServiceRegistryResource;


public class COAPEndPoint extends ServerEndpoint {
	
	//define here time of first check and check interval in milliseconds, when checking for expired resources
	public static int FIRST_CHECK_OFFSET = 1200000; //20min
	public static int CHECK_INTERVAL = 600000; //10min
	public static int ALIVE_THRESHOLD = 1800000;///30min

	public COAPEndPoint() throws SocketException {
		// Initializes ErO2 services
		ServiceRegistryResource serviceRegistry = new ServiceRegistryResource();
		addResource(serviceRegistry);
		serviceRegistry.setCOAPEndPoint(this);
		
		// Add monitor service
	
		ServiceMonitorResource monitorRegistry = new ServiceMonitorResource();
		System.out.println("SERVICE_MONITOR_RESOURCE CREATED");
		addResource(monitorRegistry);
		
		// Launch task periodically to check for sensors no longer reporting and remove them from registry
		Timer t = new Timer();
		t.scheduleAtFixedRate(
		    new TimerTask()
		    {
		        public void run()
		        {
		        	System.out.println("Checking for removed sensors...");
		        	ErO2Registry ERO2REGISTRY = ErO2Registry.getInstance();
		        	Hashtable<String, ErO2Service> registry=ERO2REGISTRY.allServices();
		        	Enumeration<String> serviceKeys = registry.keys();
		        	String serviceLocator = null;
		        	ErO2Service ero2ser = null;
		        	
		        	while (serviceKeys.hasMoreElements()) {
		        	      try {
		        	    	  serviceLocator = serviceKeys.nextElement();
			        	      ero2ser = registry.get(serviceLocator);

			        	      String now = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			        	      //check if the date strings are equal until the desired accuracy
			        	      if (millisOfDate(now)-millisOfDate(ero2ser.getTimestamp()) > ALIVE_THRESHOLD) { 
			        	    	  registry.remove(serviceLocator);
			        	    	  System.out.println("Sensor " + serviceLocator + " was no longer reporting and has been removed from registry");
			        	      	  } 
			        	      } catch (Exception e) {
			        	    	  if (e instanceof NullPointerException){
				        	    	  String now = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
				        	    	  ErO2Resource ero2res = ero2ser.getResourceByServicelocator(serviceLocator);
			        	    		  if (millisOfDate(now)-millisOfDate(ero2res.getTimeofRegistration()) > ALIVE_THRESHOLD) {
			        	    			  registry.remove(serviceLocator);
			        	    		  }
			        	    		  System.out.println("Sensor " + serviceLocator + " never reported any values and has been removed from registry");
			        	    	  }
			        	      }   	
		    	    }
		        }
		    },
		    FIRST_CHECK_OFFSET, 
		    CHECK_INTERVAL); 
	}
	
	public static long millisOfDate(String date) {
		int seconds = Integer.valueOf(date.substring(17,19));
		int minutes = Integer.valueOf(date.substring(14,16));
		int hours = Integer.valueOf(date.substring(11,13));
		int days = Integer.valueOf(date.substring(8,10));
		int months = Integer.valueOf(date.substring(5,7));
		int years = Integer.valueOf(date.substring(0,4));
		long totalMilliseconds = (seconds + minutes*60 + hours*3600 + days*86400 + months*2592000 + years*31104000) * 1000;
		return totalMilliseconds;
	}
	
	public void handleRequest(Request request) {
		request.prettyPrint();
		super.handleRequest(request);
	}

}
