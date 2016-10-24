/**
* \file
* 		ErO2 service registry which keep track of services
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#ifndef ERO2REGISTRY_H_
#define ERO2REGISTRY_H_

#include "contiki.h"
#include "contiki-lib.h"
#include "contiki-net.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ero2manager.h"

struct ero2_service;
struct ero2_service{
	struct ero2_service *next;
	uip_ipaddr_t ipaddr;
	const char* name;
	const char* uri;
	const char* request_method;
	const char* query_string;
}ero2_service;

void init_registry();
void add_to_local_registry(struct ero2_service* local_service);
void add_to_external_registry(struct ero2_service* externl_service);
list_t get_local_registry();
list_t get_external_registry();
void get_service_serialization(char* service_string);
#endif
