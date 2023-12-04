package com.rpg.gmtool.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigUtils {

    //<editor-fold desc="CONSTANTS">
    public static final String systemLocations = "data/systems/";
    public static final String campaignLocations = "data/campaigns/";
    public static final String equipmentLocations = "data/equipment/";
    public static final String suffix = ".json";
    public static final String configFile = "config.json";

    public static final String currentConfigFile = "data/config.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    //</editor-fold>
    //<editor-fold desc="ACCESS METHODS">

    public static ObjectMapper getObjectMapper(){
        return mapper;
    }
    //</editor-fold>

}
