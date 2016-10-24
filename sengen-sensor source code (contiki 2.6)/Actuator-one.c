#include "ero2manager.h"
#define SERVICE_POINTER(sp) #sp

PROCESS(main_process,"PF Process");
AUTOSTART_PROCESSES(&main_process);

PROCESS_THREAD(main_process, ev, data)
{
	PROCESS_BEGIN();
	start_ero2_service(SERVICE_POINTER(C1S1A1));
	PROCESS_END();
}


