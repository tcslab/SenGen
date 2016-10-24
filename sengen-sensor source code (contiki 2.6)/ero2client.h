/**
* \file
* 		ErO2 client which invokes the remote services
* \author
* 		Kasun Samarasinghe <Kasun.Wijesiriwardana@unige.ch>
*/
#ifndef ERO2CLIENT_H_
#define ERO2CLIENT_H_

#include "ero2manager.h"

static struct ero2_service* request_service;
static void* request_payload;

void invoke_service(struct ero2_service* service,void* payload);

#endif
