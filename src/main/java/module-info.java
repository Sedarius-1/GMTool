module com.rpg.gmtool {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.simtechdata.sceneonefx;
    requires lombok;
    requires slf4j.api;

    opens com.rpg.gmtool to javafx.fxml;
    exports com.rpg.gmtool;
    exports com.rpg.gmtool.models.characters;
    exports com.rpg.gmtool.models.items;
    exports com.rpg.gmtool.models.systems;
    exports com.rpg.gmtool.config;
    exports com.rpg.gmtool.controllers;
    exports com.rpg.gmtool.exceptions;
    opens com.rpg.gmtool.controllers to javafx.fxml;
    exports com.rpg.gmtool.models.campaigns;

}