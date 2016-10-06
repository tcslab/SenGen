package ero2.util;

import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.transport.coap.COAPClient;

public class Ero2Common {
	
	public static String getURI(ErO2Service service, String resourceName,
			String paramterString) {
		System.out.println(resourceName);
		ErO2Resource resource = service.getResourceByName(resourceName);
		String uri = "coap://" + service.getIPAddress() + ":"
				+ COAPClient.COAPPORT + "/" + resource.getName() + "?"
				+ paramterString;
		
		
		return uri;
	}
}
