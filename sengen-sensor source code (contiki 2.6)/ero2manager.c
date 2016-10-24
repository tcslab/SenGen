/**
* \file
* 		ErO2 Manager which implements the service composition logic to certain extent
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#include "contiki-conf.h"
#include "contiki-net.h"
#include <stdio.h>
#include <random.h>

#include "dev/leds.h"

#include "er-coap-12.h"
#include "erbium.h"
#include "er-coap-12-engine.h"
#include "ero2client.h"
#include "ero2registry.h"
#include "er-coap-12-separate.h"
#include "er-coap-12-transactions.h"

#include "dev/light-sensor.h"
#include "dev/temperature-sensor.h"
#include "dev/sht11-sensor.h"
#include "lib/sensors.h"
#include "telosb-sensors.h"


void  serialize_address(uip_ipaddr_t* addr,char* addrstring);
void start_app_services();

static struct etimer et;
static int PROCESS_INTERVAL= DEFAULT_INTERVAL;
process_event_t STATE_CHANGED;

RESOURCE(ero2heartbeat, METHOD_GET, "ero2heartbeat", "title=\"Heartbeat\"");
void ero2heartbeat_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  char* message = "Heartbeat";
	memcpy(buffer, message, 10);

  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response, 10, 1);
  REST.set_response_payload(response, buffer, 10);
}

PROCESS(ero2_process,"ErO2 Process");
PROCESS_THREAD(ero2_process, ev, data)
{
	PROCESS_BEGIN();
	int rand_int, name_int, temp;
	int temp2 = 0;
	//PRINTF("ErO2 Process starts!\n");
	switch(service_pointer[3]){       //HACK for sengen to initialize random generator based on sensor name
		case 'A':
			name_int=10;
			break;
		case 'B':
			name_int=20;
			break;
		case 'C':
			name_int=30;
			break;
	}
	temp = service_pointer[5] - '0';
	if (strlen(service_pointer)==7){
	    temp2 = service_pointer[6] - '0';
	    temp2 += 10;
	}
	name_int = name_int + temp + temp2;
	//printf("name rand is %d\n", name_int);
	random_init(name_int);
	
	/* Define the timer to fire periodically and initially set it to the INIT interval */
	etimer_set(&et, CLOCK_SECOND * INIT_INTERVAL);

	while(1){

		PROCESS_YIELD();

		struct ero2_service remote_service;
		char payload[MAX_PAYLOAD_LENGTH];		

		switch(ero2_state){
			case INIT:
				/* Register services in the centralized profiling registry */
				//PRINTF("Registering services ..\n");
				
				/* Prepare service */
				PROFILING_SERVER(&remote_service.ipaddr);
				remote_service.uri="register";
				remote_service.request_method=COAP_POST;

				/*Prepare payload */			
				sprintf(payload,"%s",service_pointer);
				get_service_serialization(payload);
				sprintf(payload,"%s|",payload);
				PRINTF("Register Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				break;
			case REGISTERED:
				/* Register the node with its parents */
				/* Prepare service */
				/*remote_service.ipaddr=parent.ipaddr;
				remote_service.uri="register";
				remote_service.request_method=COAP_POST;

				//Prepare payload 
				uip_ds6_addr_t *addr;
				addr = uip_ds6_get_global(ADDR_PREFERRED);
				if(addr != NULL) {
  				PRINT6ADDR(&addr->ipaddr);
				}		
				char hostaddrstring[50];
				serialize_address(&addr->ipaddr,hostaddrstring); 	
				sprintf(payload,"%s|%s",service_pointer,hostaddrstring);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);*/
				break;
			case DISCOVERED:
				/* Periodic hearbeat report to the server */
				/* Prepare service */
				PROFILING_SERVER(&remote_service.ipaddr);
				printf("discovered!!!\n");
				remote_service.uri="monitor";
				remote_service.request_method=COAP_POST;

				/*Prepare payload */
				
				SENSORS_ACTIVATE(light_sensor);
				int LightValue = 10* light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC) /7;
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);
				SENSORS_ACTIVATE(sht11_sensor);				
				double FloatTempValue = -39.60 + 0.01*(sht11_sensor.value(SHT11_SENSOR_TEMP));
				int TempValue = FloatTempValue*100;	
				double frac = FloatTempValue-TempValue;
				int fract = frac*100;
				float hum = sht11_sensor.value(SHT11_SENSOR_HUMIDITY);
				double FloatHumidValueLin = -2.0468 + 0.0367*hum + (-1.5955*0.000001)*hum*hum;
				float RealHumidValue = (FloatTempValue-25)*(0.01+0.00008*hum)+FloatHumidValueLin;
				int HumidValue = RealHumidValue*100;

				//sprintf(payload,"%s|%d|%d|%f",service_pointer, LightValue, 22);
				sprintf(payload,"%s|%d|%d|%d",service_pointer, LightValue, TempValue, HumidValue);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				break;
		}	
		
		/* Sets etimer to periodically fire if normal scheduling is failed */
		if(etimer_expired(&et)){
			rand_int = 180+random_rand()%(PROCESS_INTERVAL); //randomly report in interval range 3-8 minutes
			//printf("interval is %d\n", rand_int);
			etimer_set(&et, CLOCK_SECOND * (rand_int));
		}
	}	

  PROCESS_END();
}

/**
*	Starts ero2 service coordination layer
*/
void start_ero2_service(char* sp)
{
	//Initialize rest engine
	rest_init_engine();

	//Activates ero2 services
	service_pointer=sp;
	init_registry();
	rest_activate_resource(&resource_ero2heartbeat);

	//Activates application services
	start_app_services();
	//Starts ero2 process
	process_start(&ero2_process,NULL);
}


/**
* Handles responses from ero2 services
*/

void service_handler(void* response)
{
	PRINTF("Response received\n");

	/* Read the response */
	uint8_t *response_payload;
  int len = coap_get_payload(response, &response_payload);

  PRINTF("Payload received %s %s\n",  (char *)response_payload, REGISTRATION_SUCCESSFUL);	
	
	int status;
	if(response_payload != NULL){
		switch(ero2_state){
			case INIT:	
				if(strcmp(response_payload,REGISTRATION_SUCCESSFUL)==0){
					PRINTF("Registration successful\n");
								
					/* Sets the parent according to the central server */
					//set_parent(response_payload);
					ero2_state=DISCOVERED;//hack
					printf("process changed status\n");
					//process_post(&ero2_process,STATE_CHANGED,NULL);
				}else{
					PRINTF("Error registration..\n");
				}		
				break;

			case REGISTERED:
				status=parse_response(response_payload);				
				if(status){
					PRINTF("Discovered successful\n");
					ero2_state=DISCOVERED;
				}else{
					PRINTF("Error in discovery..\n");
				}	
				break;

			case DISCOVERED:
				if(strcmp(response_payload,REREGISTRATION_REQUIRED)==0){
					ero2_state = INIT;
					break;
				}else{
					break;
				}
		}
	}else{
		PRINTF("NULL response\n");
	}
	//process_start(&ero2_process,NULL);
}

int parse_response(char* response_payload)
{
	return 1;
}

void  serialize_address(uip_ipaddr_t* addr,char* addrstring) 
{
	sprintf(addrstring,"[%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x]", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], 	((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t 	*)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15]);
}


