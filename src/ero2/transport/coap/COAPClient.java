package ero2.transport.coap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import ch.ethz.inf.vs.californium.coap.*;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.util.Log;

	
	public class COAPClient  {

		// resource URI path used for discovery
		private static final String DISCOVERY_RESOURCE = "/.well-known/core";

		// exit codes for runtime errors
		private static final int ERR_MISSING_METHOD  = 1;
		private static final int ERR_UNKNOWN_METHOD  = 2;
		private static final int ERR_MISSING_URI     = 3;
		private static final int ERR_BAD_URI         = 4;
		private static final int ERR_REQUEST_FAILED  = 5;
		private static final int ERR_RESPONSE_FAILED = 6;
		private static final int ERR_BAD_LINK_FORMAT = 7;
		public static final String COAPPORT = "5683";
		
		
		public String doRequest(String requestURI,String requestMethod, String payload) {

			// initialize parameters
			String method = requestMethod;
			URI uri=null;
			try {
				uri = new URI(requestURI);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//String payload = null;
			boolean loop = false;

			Log.setLevel(Level.ALL);
			Log.init();

			if (requestMethod == null) {
				System.err.println("Method not specified");
				System.exit(ERR_MISSING_METHOD);
			}
			if (requestURI == null) {
				System.err.println("URI not specified");
				System.exit(ERR_MISSING_URI);
			}
			
			// create request according to specified method
			Request request = newRequest(method);
			if (request == null) {
				System.err.println("Unknown method: " + method);
				System.exit(ERR_UNKNOWN_METHOD);
			}

			if (method.equals("OBSERVE")) {
				request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));
				loop = true;
			}

			// set request URI
			if (method.equals("DISCOVER") && (uri.getPath() == null || uri.getPath().isEmpty() || uri.getPath().equals("/"))) {
				// add discovery resource path to URI
				try {
					uri = new URI(uri.getScheme(), uri.getAuthority(), DISCOVERY_RESOURCE, uri.getQuery());
					
				} catch (URISyntaxException e) {
					System.err.println("Failed to parse URI: " + e.getMessage());
					System.exit(ERR_BAD_URI);
				}
			}
			
			request.setURI(uri);
			request.setPayload(payload);
			request.setToken( TokenManager.getInstance().acquireToken() );
			
			// enable response queue in order to use blocking I/O
			request.enableResponseQueue(true);
			
			request.prettyPrint();
			
			// execute request
			try {
				request.execute();

				// loop for receiving multiple responses
				do {
		
					// receive response
		
					System.out.println("Receiving response...");
					Response response = null;
					try {
						response = request.receiveResponse();
					} catch (InterruptedException e) {
						System.err.println("Failed to receive response: " + e.getMessage());
						System.exit(ERR_RESPONSE_FAILED);
					}
		
					// output response
		
					if (response != null) {
		
						response.prettyPrint();
						System.out.println("Time elapsed (ms): " + response.getRTT());
		
						return response.getPayloadString();
		
					} else {
		
						// no response received	
						System.err.println("Request timed out");
						return null;
					}
		
				} while (loop);
			} catch (UnknownHostException e) {
				System.err.println("Unknown host: " + e.getMessage());
				return null;
			} catch (IOException e) {
				System.err.println("Failed to execute request: " + e.getMessage());
				return null;
			}
		}

		
		private static Request newRequest(String method) {
			if (method.equals("GET")) {
				return new GETRequest();
			} else if (method.equals("POST")) {
				return new POSTRequest();
			} else if (method.equals("PUT")) {
				return new PUTRequest();
			} else if (method.equals("DELETE")) {
				return new DELETERequest();
			} else if (method.equals("DISCOVER")) {
				return new GETRequest();
			} else if (method.equals("OBSERVE")) {
				return new GETRequest();
			} else {
				return null;
			}
		}

	
}
