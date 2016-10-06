package ero2.transport.http;

import org.restlet.Component;
import org.restlet.data.Protocol;


public class HTTPEndPoint {

	public boolean startHTTPListner(){
		try {
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8111);
			component.getDefaultHost().attach("/ero2proxy",
					new HTTPEndPointProxy());
			component.start();
			System.out.println("HTTP Listner started successfully");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
