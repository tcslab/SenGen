/**
* \file
* 		ErO2 Manager which implements the service composition logic to certain extent
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#ifndef ERO2MANAGER_H_
#define ERO2MANAGER_H_

#include "contiki.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "erbium.h"
#include "er-coap-12-engine.h"

#define DEBUG 0
#if DEBUG
#define PRINTF(...) printf(__VA_ARGS__)
#define PRINT6ADDR(addr) PRINTF("[%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x]", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], ((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t *)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15])
#define PRINTLLADDR(lladdr) PRINTF("[%02x:%02x:%02x:%02x:%02x:%02x]",(lladdr)->addr[0], (lladdr)->addr[1], (lladdr)->addr[2], (lladdr)->addr[3],(lladdr)->addr[4], (lladdr)->addr[5])
#else
#define PRINTF(...)
#define PRINT6ADDR(addr)
#define PRINTLLADDR(addr)
#endif

#define ACTUATOR_NODE 1

#define PROFILING_SERVER(ipaddr)   uip_ip6addr(ipaddr, 0xaaaa, 0, 0, 0, 0, 0, 0, 0x0001) 
#define REMOTE_PORT    UIP_HTONS(COAP_DEFAULT_PORT)

#define INIT 0
#define REGISTERED 1
#define DISCOVERED 2

#define INIT_INTERVAL 10
#define REGISTERED_INTERVAL 50
#define DISCOVERED_INTERVAL 50
#define DEFAULT_INTERVAL 300 //5 min

#define MAX_PAYLOAD_LENGTH 100

#define REGISTRATION_SUCCESSFUL "100"
#define REGISTRATION_FAILED "101"
#define REREGISTRATION_REQUIRED "102"

int actuator1_state; //defining the actuator1 state status

struct parent{
	uip_ipaddr_t ipaddr;
	char* service_pointer;
}parent;

struct parent* current_parent;
char* service_pointer;
int ero2_state;

void start_ero2_service(char* sp);
//void register_instance(struct service_instance);
//void start_manager();	
void service_handler(void* response);

#endif
