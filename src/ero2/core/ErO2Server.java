package ero2.core;

import java.net.SocketException;

import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;

import ero2.transport.coap.COAPEndPoint;
import ero2.transport.http.HTTPEndPoint;
import ero2.util.NFCListner;

public class ErO2Server {
	
	private LocalEndpoint COAPENDPOINT;
	private HTTPEndPoint HTTPENDPOINT;
	
	private boolean initListners(){
		//Init NFC listner
		//NFCListner nfclistener = new NFCListner();
		//nfclistener.initialize();
		//TEST FOR GIT
		//TEST AGAIN FOR GIT
		
		//COAP end point
		try{
			COAPENDPOINT= new COAPEndPoint();
			COAPENDPOINT.start();
            
            System.out.printf("COAPEndPoint listening on port %d.\n", COAPENDPOINT.getPort());

			//Rest end point
			HTTPENDPOINT = new HTTPEndPoint();
			if(HTTPENDPOINT.startHTTPListner()){
				return true;
			}else{
				return false;
			}
		}catch(SocketException e){
			System.err.println("Error starting coap listner");
			return false;
		}
	}
	
	
	public static void main(String args[]){
		ErO2Server server=new ErO2Server();
		server.initListners();
				
		System.out.println("ErO2 Server Started successfully");
	}
}
