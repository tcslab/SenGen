package ero2.identification;

public enum NodeType {
    bulb,
    light,
    curtain{
    	@Override
        public String getStatus(String status){
            if(status.equals("up")){
                return "up";
            }else{
                return "down";
            }
        }
    	@Override
        public String getToggleStatus(String status){
            if(status.equals("up")){
                return "down";
            }else{
                return "up";
            }
        }
    },
    coffee,
    alarm,
    door,
    heater,
    fan,
    generic;
    
    public static NodeType getType(String device){
        if (device.contains("bulb")) {
            return NodeType.bulb;
        } else if (device.contains("curtain")) {
            return NodeType.curtain;
        } else if (device.contains("light")) {
            return NodeType.light;
        } else if (device.contains("coffee")) {
            return NodeType.coffee;
        } else if (device.contains("alarm")) {
            return NodeType.alarm;
        }else if (device.contains("door")) {
            return NodeType.door;
        }else if (device.contains("fan")) {
            return NodeType.fan;
        }else if (device.contains("heater")) {
            return NodeType.heater;
        }else{
            return NodeType.generic;
        }
    }

    public static String parseResponse(String response){
        if(response.contains("ON")) {
            return "on";
        }else if(response.contains("OFF")){
            return "off";
        }else if(response.contains("UP")){
            return "up";
        }else if(response.contains("DOWN")){
            return "down";
        }else if(response.contains("li")){
            return "on";
        }else{
            return "off";
        }
    }

    //Adapt default on/off status to the required status from telosb
    public String getStatus(String status){
        if(status.equals("on")){
            return "on";
        }else{
            return "off";
        }
    }

    public String getToggleStatus(String status){
        if(status.equals("on")){
            return "off";
        }else{
            return "on";
        }
    }
}
