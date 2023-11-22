module com.rpg.gmtool {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires lombok;

    opens com.rpg.gmtool to javafx.fxml;
    exports com.rpg.gmtool;
    exports com.rpg.gmtool.models.Characters;
    exports com.rpg.gmtool.models.Weapons;
    exports com.rpg.gmtool.models.Systems;

}