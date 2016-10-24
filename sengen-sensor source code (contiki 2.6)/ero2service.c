/*
 * Copyright (c) 2012, Sensor Nets Laboratory, University of Geneva.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

/**
 * \file
 *      Common service implementation
 * \authors
 *      Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
 *			Orestis Evangelatos <Orestis.Evangelatos@unige.ch>
 *
 *
 *		Services supported by the sengen sensor: eg: coap://[IPV6 address]:PORT/sengen?value
 *		sengen?status=on			> switch on sengen
 *		sengen?status=off			> switch off sengen
 *		on					> switch on sengen
 *		off					> switch off sengen
 *
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "erbium.h"

#include "dev/leds.h"

#if WITH_COAP == 12
#include "er-coap-12.h"
#elif WITH_COAP == 7
#include "er-coap-07.h"
#else
#endif 

#include "ero2manager.h"
#include "ero2service.h"
#include "ero2registry.h"

#include "telosb-actuator.h"
#include "dev/light-sensor.h"

//int flag=0;
struct ero2_service sengen_service;
struct ero2_service turnon_service;
struct ero2_service turnoff_service;
//struct ero2_service query_lum_service;
struct ero2_service remote_service;


RESOURCE(sengen, METHOD_GET, "sengen", "title=\"sengen?status=on/off/value..\";rt=\"Text\"");
void sengen_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{

  char message[8];
	const char *status = NULL;
	const char *len = NULL;

	printf("I am printing and toggling\n");
	//leds_toggle(LEDS_BLUE);
	SetActuator(1);
	SetActuator(2);

	//Light value
	//SENSORS_ACTIVATE(light_sensor);
	//int LightValue = 10* light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC) /7;
	if ((len=REST.get_query_variable(request, "status", &status))) {
		  PRINTF("status %.*s\n", len, status);


		  if (strncmp(status, "on", len)==0){	
				ActuatorON(1);
				//ActuatorON(2);
				//flag=1;
				//sprintf(message,"sengen sengen= OPEN");
				printf("message is %s\n",message);
				printf("the actuator1 value is %d\n", actuator1_state);
				memcpy(buffer, "SG=ON", 6);
				leds_on(LEDS_GREEN);
				leds_off(LEDS_RED);
				//process_start(&timer_process,NULL);
			}else if (strncmp(status, "off", len)==0){
				ActuatorOFF(1);
				ActuatorOFF(2);		
				//flag=0;
				//sprintf(message,"sengen lock= CLOSED");
				printf("message is %s\n",message);
				memcpy(buffer, "SG=OFF", 7);
				leds_off(LEDS_GREEN);
				leds_on(LEDS_RED);
			}else{
				char payload[MAX_PAYLOAD_LENGTH];
				PROFILING_SERVER(&remote_service.ipaddr);
				remote_service.uri="monitor";
				remote_service.request_method=COAP_POST;

				/*Prepare payload */

				SENSORS_ACTIVATE(light_sensor);
				int LightValue = 10* light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC) /7;
				
				//SENSORS_ACTIVATE(sht11_sensor);				
				//int TempValue = sht11_sensor.value(SHT11_SENSOR_TEMP);
				//TempValue = -39.60 + 0.01*(TempValue);

				sprintf(payload,"%s|%d|%d|",service_pointer,LightValue ,22);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);

				memcpy(buffer, "value_reported", 15);
			}
}
  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response,10, 1);
  REST.set_response_payload(response, buffer, 15);
}

/*
RESOURCE(query_lum, METHOD_GET, "query_lum", "title=\"sengen?value..\";rt=\"Text\"");
void query_lum_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
char payload[MAX_PAYLOAD_LENGTH];
PROFILING_SERVER(&remote_service.ipaddr);
  remote_service.uri="monitor";
				remote_service.request_method=COAP_POST;

				
				SENSORS_ACTIVATE(light_sensor);
				int LightValue = 10* light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC) /7;
				
				//SENSORS_ACTIVATE(sht11_sensor);				
				//int TempValue = sht11_sensor.value(SHT11_SENSOR_TEMP);
				//TempValue = -39.60 + 0.01*(TempValue);

				sprintf(payload,"%s|%d|%d|",service_pointer,LightValue ,22);
				PRINTF("Payload %s\n",payload);
				
				invoke_service(&remote_service,payload);
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);
				leds_toggle(LEDS_GREEN);
				leds_toggle(LEDS_RED);

				memcpy(buffer, "value_sent", 11);

  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response,10, 1);
  REST.set_response_payload(response, buffer, 11);
}
*/

RESOURCE(turnon, METHOD_GET, "turnon", "title=\"turnon.\";rt=\"Text\"");
void turnon_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  const char *len = NULL;
  char message[8];
	const char *status = NULL;


	printf("Printing the TURNON\n");
	
	SetActuator(1);
	SetActuator(2);

				ActuatorON(1);
				ActuatorON(2);
				leds_on(LEDS_GREEN);
				leds_off(LEDS_RED);
				memcpy(buffer, "on", 3);

  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response,10, 1);
  REST.set_response_payload(response, buffer, 3);
}

RESOURCE(turnoff, METHOD_GET, "turnoff", "title=\"turnoff\";rt=\"Text\"");
void turnoff_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
  const char *len = NULL;
  char message[8];
	const char *status = NULL;


	//printf("Printing the TURNOFF\n");
	
	SetActuator(1);
	SetActuator(2);

				ActuatorOFF(1);
				ActuatorOFF(2);
				leds_off(LEDS_GREEN);
				leds_on(LEDS_RED);
				memcpy(buffer, "off", 4);

  REST.set_header_content_type(response, REST.type.TEXT_PLAIN); 
  REST.set_header_etag(response,10, 1);
  REST.set_response_payload(response, buffer, 4);
}





void start_app_services()
{
	
	rest_init_engine();
/* sengen service */
	rest_activate_resource(&resource_sengen);
	sengen_service.name="sengen";	
	sengen_service.uri="sengen";
	sengen_service.request_method="COAP_GET";	
	add_to_local_registry(&sengen_service);

/* query luminance service 
	rest_activate_resource(&resource_query_lum);
	query_lum_service.name="query_lum";	
	query_lum_service.uri="query_lum";
	query_lum_service.request_method="COAP_GET";	
	add_to_local_registry(&query_lum_service);
*/
/* turnon service used for triggering the service from a sensor automatically (distributed way) */
	rest_activate_resource(&resource_turnon);
	turnon_service.name="turnon";	
	turnon_service.uri="turnon";
	turnon_service.request_method="COAP_GET";	
	add_to_local_registry(&turnon_service);
	//printf("turnon service printing");

/* turnoff service used for triggering the service from a sensor automatically (distributed way) */

	rest_activate_resource(&resource_turnoff);
	turnoff_service.name="turnoff";	
	turnoff_service.uri="turnoff";
	turnoff_service.request_method="COAP_GET";	
	add_to_local_registry(&turnoff_service);
	//printf("turnoff service printing");




}
