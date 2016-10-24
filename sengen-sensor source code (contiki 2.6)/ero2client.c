/**
* \file
* 		ErO2 client which invokes the remote services
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#include "contiki.h"
#include "ero2manager.h"
#include "ero2client.h"
#include "ero2registry.h"

PROCESS(ero2_client_process, "ErO2 Client Process");
PROCESS_THREAD(ero2_client_process, ev, data){
	
	PROCESS_BEGIN();
	PRINTF("ERO2 client process starts \n");
	static coap_packet_t request[1];
	coap_receiver_init();
	coap_init_message(request, COAP_TYPE_CON, request_service->request_method, 0 );
  coap_set_header_uri_path(request, request_service->uri);

	char* p=(char*)request_payload;
  coap_set_payload(request, (uint8_t *)request_payload, 100);
	
	//Blocking request
	PRINTF("coap blocking request to resource %s\n",request_service->uri);
	COAP_BLOCKING_REQUEST(&request_service->ipaddr, REMOTE_PORT, request, service_handler);
	PROCESS_END();
}


void invoke_service(struct ero2_service* service,void* payload)
{
	request_service = service;
	request_payload = payload;
	process_start(&ero2_client_process,NULL);
}

