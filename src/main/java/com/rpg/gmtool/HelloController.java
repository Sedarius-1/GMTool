package com.rpg.gmtool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.gmtool.exceptions.CharacterLoadingException;
import com.rpg.gmtool.models.Characters.Character;
import com.rpg.gmtool.models.Systems.System;
import com.rpg.gmtool.models.Weapons.Weapon;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;


import java.io.*;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    public TextArea PCDescArea;
    public Label PCDataTitle;
    public TextField PCName;
    public TextField PCClass;
    public ListView PCStats;
    public ListView PCEquipment;
    public ListView PCCharacters;
    public Button PCSaveCharacter;
    public TextArea PCEquipmentDescription;
    public Button PCRemoveStat;
    public Button PCSaveStat;
    public ComboBox PCAvailableStatList;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void setupPCData(Character character) {

        PCName.setText(character.getName());
        PCClass.setText(character.getRpgRole());
        PCDescArea.setText(character.getDescription());
        PCStats.setItems(FXCollections.observableList(character.getStats()));
        PCEquipment.setItems(FXCollections.observableList(character.getEquipment()));

    }

    @FXML
    protected void clearPCData() {

        PCName.setText(null);
        PCClass.setText(null);
        PCDescArea.setText(null);
        PCStats.setItems(null);
        PCEquipment.setItems(null);

    }


    @FXML
    protected System getStatListForSelectedSystem(String systemFileName, ObjectMapper mapper) {
        try {
            return mapper.readValue(new File("data/systems/" + systemFileName + ".json"), System.class);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @FXML
    private void refreshFileList() throws CharacterLoadingException {
        File repo = new File("data");
        List<String> fileNameList = new ArrayList<>();
        File[] fileList = repo.listFiles();
        for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
            if (Objects.equals(fileList[i].getName().substring(fileList[i].getName().length() - 5), ".json")) {
                try {
                    fileNameList.add(fileList[i].getName());
                } catch (Exception e) {
                    throw new CharacterLoadingException(e.toString());
                }
            }

        }
        PCCharacters.setItems(FXCollections.observableList(fileNameList));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            refreshFileList();
        } catch (CharacterLoadingException e) {
            throw new RuntimeException(e);
        }
        System selectedSystem = getStatListForSelectedSystem("darkHeresy", mapper);
        PCAvailableStatList.setItems(FXCollections.observableList(selectedSystem.getAvailableStats()));
        PCEquipment.setEditable(true);
        PCEquipment.setCellFactory(TextFieldListCell.forListView());
        PCStats.setEditable(true);
        PCDescArea.setWrapText(true);
        PCStats.setCellFactory(TextFieldListCell.forListView());
        PCCharacters.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {

                        try {
                            clearPCData();
                            PCDescArea.setStyle("-fx-text-fill: black;");
                            Character character = mapper.readValue(new File("data/" + PCCharacters.getSelectionModel().getSelectedItem().toString()), Character.class);
                            setupPCData(character);
                        } catch (Exception e) {
                            PCDescArea.setText(e.toString());

                            PCDescArea.setStyle("-fx-text-fill: red;");
                        }
                    }
                });

        PCEquipment.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {

                        try {
                            Weapon weapon = mapper.readValue(new File("data/equipment/" + PCEquipment.getSelectionModel().getSelectedItem().toString() + ".json"), Weapon.class);
                            PCEquipmentDescription.setText(weapon.toString());
                        } catch (IOException e) {
                            PCEquipmentDescription.setText("No desc for selected item yet :<");
                        }
                    }
                });

        PCSaveCharacter.setOnAction(ActionEvent -> {
            Character character = new Character();
            character.setName(PCName.getText());
            character.setRpgRole(PCClass.getText());
            character.setDescription(PCDescArea.getText());
            character.setStats(PCStats.getItems().stream().toList());
            character.setEquipment(PCEquipment.getItems().stream().toList());
            try {
                mapper.writeValue(new File("data/" + character.getName() + ".json"), character);
                refreshFileList();
            } catch (IOException | CharacterLoadingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}