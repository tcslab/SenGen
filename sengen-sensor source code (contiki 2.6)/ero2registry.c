/**
* \file
* 		ErO2 service registry which keep track of services
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#include "ero2registry.h"

LIST(local_registry);
LIST(external_registry);
int counter=0;

void init_registry()
{	
	list_init(local_registry);
	list_init(external_registry);
}

void add_to_local_registry(struct ero2_service* local_service)
{
	list_add(local_registry,local_service);
}

void add_to_external_registry(struct ero2_service* external_service)
{
	list_add(external_registry,external_service);
}

list_t get_local_registry()
{
	return local_registry;
}

list_t get_external_registry()
{
	return external_registry;
}

void get_service_serialization(char* service_string)
{
	struct ero2_service* service=NULL;
	
	
		for (service = (struct ero2_service*)list_head(local_registry); service; service = service->next)
			{
				sprintf(service_string,"%s|%s,%s,%s,%s**%d",service_string,service->name,service->uri,service->request_method,service->query_string,counter);
				break;
		}
	
}
