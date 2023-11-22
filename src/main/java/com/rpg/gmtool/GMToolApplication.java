package com.rpg.gmtool;

import com.simtechdata.sceneonefx.SceneOne;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class GMToolApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GMToolApplication.class.getResource("initial-screen.fxml"));
        SceneOne.set("initial", (Parent) fxmlLoader.load()).size(960,720).show();


    }

    public static void main(String[] args) {
        launch();
    }
}