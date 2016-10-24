/*
 * Copyright (c) 2012, University of Geneva, TCS&Sensors lab
 * All rights reserved.
 *
 *
 *
 *
 *	this is the Header file for the TelosB sensors
 *	Get Light, Temperature, Humidity
 *
 *    To add support for the Mathlibrary, add in the end of the makefile:
 * CUSTOM_RULE_LINK=1
%$(TARGET):%.co$(PROJECT_OBJECTFILES) $(PROJECT_LIBRARIES)contiki-$(TARGET).a$(LD)$(LDFLAGS) $(TARGET_STARTFILES)${filter-out%.a,$^}${filter%.a,$^}$(TARGET_LIBFILES)-o$@-lm
 *
 *
 *
 *
 *
 * author: orestis.evangelatos@unige.ch
 * Created: 23.11.2012
 *
 */


#ifndef __SENSORREADINGS_H__
#define __SENSORREADINGS_H__

#include "contiki.h"
#include <string.h>
#include <stdlib.h>
#include "dev/light-sensor.h"
#include "dev/sht11-sensor.h"
#include "dev/temperature-sensor.h"
#include "dev/battery-sensor.h"
#include "dev/button-sensor.h"
#include "lib/sensors.h"
#include "contiki-lib.h"


int GetSensorLight();
int GetSensorLightSolar();
int GetSensorTemp();
int GetSensorHumid();
float GetSensorBattery();
float floorfunct(float);

/*-----------------------------GET Light----------------------------------------------*/
int GetSensorLight() {
SENSORS_ACTIVATE(light_sensor);
int LightValue = 10* light_sensor.value(LIGHT_SENSOR_PHOTOSYNTHETIC) /7;
//SENSORS_DEACTIVATE(light_sensor);
return LightValue; //return Light Photsynthetic Value
//printf("\nLight Valueeee: %d \t",LightValue);
}


/*-----------------------------GET Light Solar--------------------------------------------*/
int GetSensorLightSolar() {
SENSORS_ACTIVATE(light_sensor);
int LightSolarValue =  light_sensor.value(LIGHT_SENSOR_TOTAL_SOLAR);
//SENSORS_DEACTIVATE(light_sensor);
return LightSolarValue; //return Light Solar Value
//printf("Temp Value: %d \n",LightSolarValue);
}

/*-----------------------------GET Temperature----------------------------------------------*/
int GetSensorTemp() {
SENSORS_ACTIVATE(sht11_sensor);
int TempValue = sht11_sensor.value(SHT11_SENSOR_TEMP);
TempValue = -39.60 + 0.01*(TempValue);
//SENSORS_DEACTIVATE(sht11_sensor);
return TempValue; //return Temperature Value
//printf("Temp Value: %d \n",TempValue);
}

/*-----------------------------GET Humidity----------------------------------------------*/
int GetSensorHumid(){
SENSORS_ACTIVATE(sht11_sensor);
int HumidValue = sht11_sensor.value(SHT11_SENSOR_HUMIDITY);
HumidValue = -4 + 0.0405*(HumidValue) -((HumidValue)*(HumidValue)*2.8*0.000001);
//SENSORS_DEACTIVATE(sht11_sensor);
return HumidValue; //return Relative Humidity Value
//printf("Relative Humidity Value: %d \n",HumidValue);
}

/*-----------------------------GET Battery Voltage Level---------------------------------------*/
float GetSensorBattery(){
SENSORS_ACTIVATE(battery_sensor);
int tBatteryValue = battery_sensor.value(0);
float BatteryValue = ((tBatteryValue*2500*2)/4096);
float BatteryValuef = (BatteryValue - floorfunct(BatteryValue))*1000;
//SENSORS_DEACTIVATE(battery_sensor);
BatteryValue = BatteryValue + BatteryValuef;
return BatteryValue; //return decimal point of voltage
//printf("Battery Value: %ld \n",(long) BatteryValue);
//printf("Battery Value: %03d \n",(unsigned) BatteryValuef);
}


/*-----------------------------function for get floor(number) STILL HAS A BUG!!!----------------------*/
float floorfunct(float x){
if(x>=0.0f)
	return (float)((int)x);
else
	return(float)((int)x-1);
}

#endif /* SENSORREADINGS */
