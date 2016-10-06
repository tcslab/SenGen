package ero2.transport.http;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import ero2.resource.rest.CrowdRestDataResource;
import ero2.resource.rest.CrowdRestUserResource;
import ero2.resource.rest.ErO2RestMediatorResource;
import ero2.resource.rest.ErO2RestMonitorResource;
import ero2.resource.rest.ErO2RestPullResource;
import ero2.resource.rest.ErO2RestProfileResource;
import ero2.resource.rest.ErO2RestRegistryResource;
import ero2.resource.rest.ErO2RestListRegistryResource;
import ero2.resource.rest.ErO2RestXMLResource;
import ero2.resource.rest.ErO2RestSensValues;

public class HTTPEndPointProxy extends Application {

    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/service", ErO2RestRegistryResource.class);
        router.attach("/info", ErO2RestListRegistryResource.class);
        router.attach("/monitor", ErO2RestMonitorResource.class);
        router.attach("/pull", ErO2RestPullResource.class);       
	router.attach("/mediate", ErO2RestMediatorResource.class);
        router.attach("/user", ErO2RestProfileResource.class);
        router.attach("/service/type/xml_rspec", ErO2RestXMLResource.class);
        // Returns only the values of sensors(luminance, temperature)
        router.attach("/sensorvalues", ErO2RestSensValues.class);
		//Return last values from sensors
        router.attach("/crowddata", CrowdRestDataResource.class);
        router.attach("/crowddata/{type}", CrowdRestDataResource.class);
        //Manage crowd users
        router.attach("/crowdusers", CrowdRestUserResource.class);
        return router;
    }
}
