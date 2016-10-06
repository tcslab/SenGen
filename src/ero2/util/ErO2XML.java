package ero2.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ero2.core.ErO2Resource;
import ero2.core.ErO2Service;
import ero2.core.ErO2ServiceStatus;

public class ErO2XML {

	@SuppressWarnings("unchecked")
	public String getServicesXMLString(
			Hashtable<String, ErO2Service> serviceRegistry) {

		Enumeration<String> serviceKeys = serviceRegistry.keys();

		StringWriter stringWriter = new StringWriter();

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter;
		try {
			xmlStreamWriter = xmlOutputFactory
					.createXMLStreamWriter(stringWriter);

			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement("rspec");
			xmlStreamWriter.writeAttribute("type", "advertisement");
			xmlStreamWriter.writeAttribute("xsi:schemaLocation",
					"http://www.iotlab.eu");
			xmlStreamWriter.writeAttribute("xmlns:xsi",
					"http://www.w3.org/2001/XMLSchema-instance");
			xmlStreamWriter.writeAttribute("xmlns", "http://www.iotlab.eu");

			while (serviceKeys.hasMoreElements()) {
				String serviceLocator = serviceKeys.nextElement();
				System.out.println(serviceLocator);
				ErO2Service service = serviceRegistry.get(serviceLocator);

				Vector<ErO2Resource> resources = service.getResources();
				xmlStreamWriter.writeStartElement("node");
				xmlStreamWriter.writeAttribute("component_manager_id",
						"urn:publicid:iot-lab.eu+cm");
				xmlStreamWriter.writeAttribute("component_name", "telosb");
				xmlStreamWriter.writeAttribute("exclusive", "true");
				xmlStreamWriter.writeAttribute("component_id",
						"urn:publicid:unige.ch+node+telosb+" + serviceLocator);

				xmlStreamWriter.writeStartElement("interface");
				xmlStreamWriter.writeAttribute("component_id", "sdfa");

				xmlStreamWriter.writeStartElement("location");
				xmlStreamWriter.writeAttribute("latitude", "46.1765964");
				xmlStreamWriter.writeAttribute("longitude", "6.1399820");
				xmlStreamWriter.writeEndElement();
				
				xmlStreamWriter.writeStartElement("ip");
				xmlStreamWriter.writeAttribute("address", "129.194.70.52");
				xmlStreamWriter.writeAttribute("netmask", "255.255.255.0");
				xmlStreamWriter.writeAttribute("protocol", "http");
				xmlStreamWriter.writeAttribute("type", "");
				xmlStreamWriter.writeAttribute("port", "8111");

				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndElement();

				for (ErO2Resource ero2Resource : resources) {
					if (ero2Resource.getName() != null
							&& ero2Resource.getMethod() != null) {
						
					//actuation on
					xmlStreamWriter.writeStartElement("resource");
					xmlStreamWriter.writeAttribute("max_reservations","1");
					xmlStreamWriter.writeAttribute("component_manager_id",
							"urn:publicid:iot-lab.eu+cm");
					xmlStreamWriter.writeAttribute("exclusive", "true");
					xmlStreamWriter.writeAttribute("component_id",
							"urn:publicid:unige.ch+resource+"
									+ ero2Resource.getName());
					xmlStreamWriter.writeAttribute("NID", serviceLocator); //unique NodeID used to map with other services. It comes hardcoded on the node.
					
					// create an element with the IPSO resource description
					xmlStreamWriter.writeStartElement(getIPSOResourceName(ero2Resource.getName()));
					xmlStreamWriter.writeStartElement("on");
					xmlStreamWriter.writeAttribute("data_type", "true");
					xmlStreamWriter.writeAttribute("interface_def", "a");
					xmlStreamWriter.writeAttribute("interface_type",
							getIPSOResourceType(ero2Resource.getName()));
					xmlStreamWriter.writeAttribute("path",
							"/ero2proxy/mediate?service=" + serviceLocator + "&resource=" + ero2Resource.getName()+ "&status=on");
					//xmlStreamWriter.writeAttribute("name",getIPSOResourceType(ero2Resource.getName()) + "at UNIGE.ch" + serviceLocator);
					xmlStreamWriter.writeAttribute("name", ero2Resource.getName() + " at UNIGE with NID: " + serviceLocator);
					xmlStreamWriter.writeEndElement(); // closing /on
					xmlStreamWriter.writeEndElement(); // closing /IPSO name
														// description
					xmlStreamWriter.writeEndElement(); // closing /resource
														// description
					
					//actuation off
					xmlStreamWriter.writeStartElement("resource");
					xmlStreamWriter.writeAttribute("max_reservations","1");
					xmlStreamWriter.writeAttribute("component_manager_id",
							"urn:publicid:iot-lab.eu+cm");
					xmlStreamWriter.writeAttribute("exclusive", "true");
					xmlStreamWriter.writeAttribute("component_id",
							"urn:publicid:unige.ch+resource+"
									+ ero2Resource.getName());
					xmlStreamWriter.writeAttribute("NID", serviceLocator); //unique NodeID used to map with other services. It comes hardcoded on the node.
					
					// create an element with the IPSO resource description
					xmlStreamWriter
							.writeStartElement(getIPSOResourceName(ero2Resource
									.getName())); //HARDCODED gpio??
					xmlStreamWriter.writeStartElement("off");
					xmlStreamWriter.writeAttribute("data_type", "true");
					xmlStreamWriter.writeAttribute("interface_def", "a");
					xmlStreamWriter.writeAttribute("interface_type",
							getIPSOResourceType(ero2Resource.getName()));
					xmlStreamWriter.writeAttribute("path",
							"/ero2proxy/mediate?service=" + serviceLocator + "&resource=" + ero2Resource.getName()+ "&status=off");
					//xmlStreamWriter.writeAttribute("name",getIPSOResourceType(ero2Resource.getName()) + "at UNIGE.ch" + serviceLocator);
					xmlStreamWriter.writeAttribute("name", ero2Resource.getName() + " at UNIGE with NID: " + serviceLocator);
					xmlStreamWriter.writeEndElement(); // closing /on
					xmlStreamWriter.writeEndElement(); // closing /IPSO name
														// description
					xmlStreamWriter.writeEndElement(); // closing /resource
														// description
					
					//sensor value(luminance)
					xmlStreamWriter.writeStartElement("resource");
					xmlStreamWriter.writeAttribute("max_reservations","1");
					xmlStreamWriter.writeAttribute("component_manager_id",
							"urn:publicid:iot-lab.eu+cm");
					xmlStreamWriter.writeAttribute("exclusive", "true");
					xmlStreamWriter.writeAttribute("component_id",
							"urn:publicid:unige.ch+resource+"
									+ ero2Resource.getName());
					xmlStreamWriter.writeAttribute("NID", serviceLocator); //unique NodeID used to map with other services. It comes hardcoded on the node.
					
					// create an element with the IPSO resource description
					xmlStreamWriter
							.writeStartElement("sen");
					xmlStreamWriter.writeStartElement("lum");
					xmlStreamWriter.writeAttribute("data_type", "true");
					xmlStreamWriter.writeAttribute("interface_def", "a");
					xmlStreamWriter.writeAttribute("interface_type","ipso.sen.lum");
					xmlStreamWriter.writeAttribute("path",
							"/ero2proxy/monitor?service=" + serviceLocator);
					//xmlStreamWriter.writeAttribute("name",getIPSOResourceType(ero2Resource.getName()) + "at UNIGE.ch" + serviceLocator);
					xmlStreamWriter.writeAttribute("name", "luminance at UNIGE with NID: " + serviceLocator);
					xmlStreamWriter.writeEndElement(); // closing /on
					xmlStreamWriter.writeEndElement(); // closing /IPSO name
														// description
					xmlStreamWriter.writeEndElement(); // closing /resource
														// description
					}
				}
				xmlStreamWriter.writeEndElement(); // closing /node
				
			}
			xmlStreamWriter.writeEndElement(); // closing /rspec
			xmlStreamWriter.writeEndDocument(); // closing /document

			xmlStreamWriter.flush();
			xmlStreamWriter.close();

			String xmlString = stringWriter.getBuffer().toString();

			stringWriter.close();
			return xmlString;
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	private String getIPSOResourceName(String localResourceName) {

		if (localResourceName == "bulb")
			return "lightcontrol";
		else if (localResourceName == "temp" || localResourceName == "hum"
				|| localResourceName == "light")
			return "sen";
		else
			return "gpio";

	}

	private String getIPSOResourceType(String localResourceName) {

		if (localResourceName == "bulb")
			return "ipso.lt.on";
		else if (localResourceName == "temp" || localResourceName == "hum"
				|| localResourceName == "light")
			return "ipso.sen";
		else
			return "ipso.gpio.dout";

	}

	private String getIPSOResourcePath(String localResourceName) {

		if (localResourceName == "bulb")
			return "/lt/1/on";
		else if (localResourceName == "temp" || localResourceName == "hum"
				|| localResourceName == "light")
			return "/sen";
		else
			return "/gpio/dout";

	}
}
