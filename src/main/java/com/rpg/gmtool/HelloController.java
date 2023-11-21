package com.rpg.gmtool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.gmtool.models.Characters.Character;
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
    public Button ChangeFileButton;
    public TextField PCName;
    public TextField PCClass;
    public ListView PCStats;
    public ListView PCEquipment;
    public ListView PCCharacters;
    public Button PCSaveCharacter;
    public TextArea PCEquipmentDescription;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void setupPCData(Character character){

        PCName.setText(character.getName());
        PCClass.setText(character.getRpgRole());
        PCDescArea.setText(character.getDescription());
        PCStats.setItems(FXCollections.observableList(character.getStats()));
        PCEquipment.setItems(FXCollections.observableList(character.getEquipment()));

    }

    private static List<String> getParsedStatsValues(String[] rawData) {
        List<String> stats = new ArrayList<>(List.of("Weapon Skill (WS)", "Ballistic Skill (WS)",
                "Strength (Str)",
                "Toughness (T)",
                "Agility (Ag)",
                "Intelligence (Int)",
                "Perception (Per)",
                "Will Power (WP)",
                "Fellowship (Fel)"));
        String[] statsValues =String.valueOf(rawData[3]).split("`");
        for(int i=0;i<statsValues.length;i++){
            if(!Objects.isNull(statsValues[i])){
                stats.set(i, stats.get(i) +" "+ statsValues[i]);
            }
            else{
                stats.set(i, stats.get(i) + " NO VALUE");
            }

        }
        if(statsValues.length!=stats.size()){
            for(int i=statsValues.length;i<stats.size();i++){
                    stats.set(i, stats.get(i) + " NO VALUE");
            }
        }

        return stats;
    }

    @FXML
    private void refreshFileList(){
        File repo = new File ("data");
        List<String> fileNameList = new ArrayList<>();
        File[] fileList = repo.listFiles();
        for (int i = 0; i< Objects.requireNonNull(fileList).length; i++) {
            if(Objects.equals(fileList[i].getName().substring(fileList[i].getName().length()-5), ".json")){
                fileNameList.add(fileList[i].getName());
            }

        }
        PCCharacters.setItems(FXCollections.observableList(fileNameList));
    }
    

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        ObjectMapper mapper = new ObjectMapper();

        refreshFileList();
        PCEquipment.setEditable(true);
        PCEquipment.setCellFactory(TextFieldListCell.forListView());
        PCStats.setEditable(true);
        PCStats.setCellFactory(TextFieldListCell.forListView());
        PCCharacters.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {

                        try {
                            Character character = mapper.readValue(new File("data/"+PCCharacters.getSelectionModel().getSelectedItem().toString()), Character.class);
                            setupPCData(character);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        PCEquipment.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {

                        try {
                            Weapon weapon = mapper.readValue(new File("data/equipment/"+PCEquipment.getSelectionModel().getSelectedItem().toString()+".json"), Weapon.class);
                            PCEquipmentDescription.setText(weapon.toString());
                        } catch (IOException e) {
                            PCEquipmentDescription.setText("No desc for selected item yet :<");
                        }
                    }
                });
        PCDescArea.setEditable(false);
        ChangeFileButton.setOnAction(ActionEvent -> {
//            StringBuilder characterData = new StringBuilder();
//            File file = new File("data/test.gmpc");
//            try (Scanner myReader = new Scanner(file)){
//                while(myReader.hasNextLine()){
//                    String line = myReader.nextLine();
//                    characterData.append(line);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            String[] characterDataRead = characterData.toString().split("&");
//
//            setupPCData(characterDataRead);

            try {
                mapper.writeValue(new File("character.json"), new Character("a", "a", "b", Arrays.asList("uwu", "uwuwu"), Arrays.asList("uwu", "uwuwu","wuwu")));
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                mapper.writeValue(new File("data/"+character.getName()+".json"), character);
                refreshFileList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}