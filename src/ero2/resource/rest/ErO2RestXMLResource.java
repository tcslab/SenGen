package ero2.resource.rest;

import java.util.Hashtable;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import ero2.core.ErO2Registry;
import ero2.core.ErO2Service;
import ero2.util.ErO2JSON;
import ero2.util.ErO2XML;

public class ErO2RestXMLResource extends ServerResource {

	ErO2Registry ERO2REGISTRY;

	public ErO2RestXMLResource() {
		ERO2REGISTRY = ErO2Registry.getInstance();
	}

	@Get()
	public String getServices() {
		return serializeRegistry();
	}

	private String serializeRegistry() {
		Hashtable<String, ErO2Service> serviceRegistry = ERO2REGISTRY
				.allServices();
		if (!serviceRegistry.isEmpty()) {
			ErO2XML xmlwriter = new ErO2XML();
			String servicesXML = xmlwriter
					.getServicesXMLString(serviceRegistry);
			return servicesXML;
		} else {
			System.out.println(" Server is Running but No resource available for XML parsing!");
			return("No resource available for XML parsing!");
		}

	}
}
