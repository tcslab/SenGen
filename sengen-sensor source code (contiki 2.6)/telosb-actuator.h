/*
 * Copyright (c) 2012, University of Geneva, TCS&Sensors lab
 * All rights reserved.
 *
 *
 *
 *
 *	this is the Header file for the TelosB actuators
 *
 *
 *
 * author: orestis.evangelatos@unige.ch
 * Created: 01.11.2012
 *
 */

#ifndef __ACTUATORS_H__
#define __ACTUATORS_H__


/*Include libraries for identifying the pins */
#include "msp430def.h"
#include "dev/cc2420.h"
#include <io.h>
#include <string.h>
#include <stdlib.h>



/* Define the P23 as output. P23 corresponds to G/IO2 which is the pin No3 in the 6-pin module */
#define P23_OUT() (P2DIR |= BV(3))
#define P23_IN() (P2DIR &= ~BV(3))
#define P23_SEL() (P2SEL &= ~BV(3))
#define P23_IS_1  (P2OUT & BV(3))
#define P23_WAIT_FOR_1() do{}while (!P23_IS_1)
#define P23_IS_0  (P2OUT & ~BV(3))
#define P23_WAIT_FOR_0() do{}while (!P23_IS_0)
#define P23_1() (P2OUT |= BV(3))
#define P23_0() (P2OUT &= ~BV(3))

/* Define the P26 as output. P26 corresponds to G/IO3 which is the pin No4 in the 6-pin module */
#define P26_OUT() (P2DIR |= BV(6))
#define P26_IN() (P2DIR &= ~BV(6))
#define P26_SEL() (P2SEL &= ~BV(6))
#define P26_IS_1  (P2OUT & BV(6))
#define P26_WAIT_FOR_1() do{}while (!P26_IS_1)
#define P26_IS_0  (P2OUT & ~BV(3))
#define P26_WAIT_FOR_0() do{}while (!P26_IS_0)
#define P26_1() (P2OUT |= BV(6))
#define P26_0() (P2OUT &= ~BV(6))




void SetActuator(int stype); /* function prototype*/
void SetActuator(int stype) { /* function definition*/
if (stype == 1){
	P23_OUT();
	P23_SEL();
}
else if (stype == 2){
	P26_OUT();
	P26_OUT();
}
else
printf(" The actuator you set: ***%d*** does not belong to the system! Please check again the name of the device you try to connect!\n", stype);
}

void ActuatorON(int stype){
if (stype == 1){
P23_1();
printf("sdfsfsdfsdf%d \n", stype);
}
else if (stype == 2)
P26_1();
}

void ActuatorOFF(int stype){
if (stype == 1)
P23_0();
else if (stype == 2)
P26_0();
}


#endif /* ACTUATORS */
