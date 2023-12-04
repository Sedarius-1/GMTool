package com.rpg.gmtool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.gmtool.models.campaigns.Campaign;
import com.rpg.gmtool.exceptions.CharacterLoadingException;
import com.rpg.gmtool.models.characters.Character;
import com.rpg.gmtool.models.characters.Statistic;
import com.rpg.gmtool.models.characters.Talent;
import com.rpg.gmtool.models.items.Armour;
import com.rpg.gmtool.models.items.Item;
import com.rpg.gmtool.models.items.Weapon;
import com.rpg.gmtool.models.systems.System;
import com.rpg.gmtool.models.systems.SystemStatistic;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


import java.io.*;
import java.net.URL;
import java.util.*;

import static com.rpg.gmtool.config.ConfigUtils.*;

public class PCController implements Initializable {
    //<editor-fold desc="FIELDS">
    public TextArea PCDescArea;
    public TextField PCName;
    public TextField PCClass;
    public TableView<Statistic> PCStats;
    public TableColumn<Statistic, String> PCStatName;
    public TableColumn<Statistic, String> PCStatValue;
    public ListView<String> PCCharacters;
    public Button PCSaveCharacter;
    public Button PCRemoveStat;
    public Button PCSaveStat;
    public ComboBox<SystemStatistic> PCAvailableStatList;
    public TextField PCInputStatValue;
    public Text PCUnsaved;
    public TableView<Talent> PCTalents;
    public TableColumn<Talent, String> PCTalentName;
    public TableColumn<Talent, String> PCTalentStat;
    public TableColumn<Talent, String> PCTalentProficiency;
    public TextField PCTalentNameInput;
    public ComboBox<SystemStatistic> PCTalentStatInput;
    public ComboBox<String> PCTalentProficiencyInput;
    public Button PCSaveTalent;
    public TextArea PCItemDescription;
    public ComboBox<Item> PCItemList;
    public TableView<? extends Item> PCEquipment;
    public TextField PCItemName;
    public TextField PCItemPrice;
    public TextField PCItemAvailability;
    public TextField PCWeaponDamage;
    public TextField PCWeaponRange;
    public TextField PCWeaponType;
    public TextField PCArmourBodyPart;
    public TextField PCArmourProtectionValue;
    public CheckBox PCArmourWorn;
    public Button PCSaveItem;
    private Campaign campaign;
    private Character currentCharacter;
    private final ObjectMapper mapper = getObjectMapper();
    private boolean wasEdited = false;

    private final String UNSAVED_CHANGES = "YOU HAVE UNSAVED CHANGES HERE!";

    //</editor-fold>
    //<editor-fold desc="INITIALIZATION">
    @FXML
    protected System getStatListForSelectedSystem(String systemFileName, ObjectMapper mapper) {
        try {
            return mapper.readValue(new File(systemLocations + systemFileName + ".json"), System.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initializeCampaign() {
        try {
            campaign = mapper.readValue(new File(currentConfigFile), Campaign.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchCharactersList() {
        try {
            refreshCharacterList();
        } catch (CharacterLoadingException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected List<Item> getItemsList() throws CharacterLoadingException {
        File repo = new File(equipmentLocations + campaign.getSystemCodeName() + "/");
        List<Item> itemList = new ArrayList<>();
        File[] fileList = repo.listFiles();
        if (!Objects.isNull(fileList)) {
            for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                if (Objects.equals(fileList[i].getName().substring(fileList[i].getName().length() - 5), ".json")) {
                    try {
                        switch(fileList[i].getName().charAt(0)){
                            case 'i':
                                itemList.add(mapper.readValue(new File(equipmentLocations + campaign.getSystemCodeName() + "/" + fileList[i].getName()), Item.class));
                                break;
                            case 'w':
                                itemList.add(mapper.readValue(new File(equipmentLocations + campaign.getSystemCodeName() + "/" + fileList[i].getName()), Weapon.class));
                                break;
                            case 'a':
                                itemList.add(mapper.readValue(new File(equipmentLocations + campaign.getSystemCodeName() + "/" + fileList[i].getName()), Armour.class));
                                break;
                        }

                    } catch (Exception e) {
                        throw new CharacterLoadingException(e.toString());
                    }
                }
            }
        }
        return itemList;
    }

    private void initailizePCScreenObjects() {
        PCStats.setEditable(true);
        PCStatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        PCStatValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        PCStatValue.setCellFactory(TextFieldTableCell.forTableColumn());
        System selectedSystem = getStatListForSelectedSystem(campaign.getSystemCodeName(), mapper);
        PCAvailableStatList.setItems(FXCollections.observableList(selectedSystem.getAvailableStats()));
        PCTalentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        PCTalentStat.setCellValueFactory(new PropertyValueFactory<>("stat"));
        PCTalentProficiency.setCellValueFactory(new PropertyValueFactory<>("proficiency"));
        PCTalentProficiencyInput.setItems(FXCollections.observableList(selectedSystem.getAvailableProficiencies()));
        PCTalentStatInput.setItems(FXCollections.observableList(selectedSystem.getAvailableStats()));
        PCDescArea.setWrapText(true);
        try {
            PCItemList.setItems(FXCollections.observableList(getItemsList()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


    }

    //</editor-fold>
    //<editor-fold desc="DATA REFRESHING">

    @FXML
    private void setPCUnsaved() {
        wasEdited = true;
        PCUnsaved.setFill(Color.RED);
        PCUnsaved.setText(UNSAVED_CHANGES);
    }

    @FXML
    private void unsetPCUnsaved() {
        wasEdited = false;
        PCUnsaved.setText(" ");

    }

    @FXML
    private void refreshCharacterList() throws CharacterLoadingException {
        File repo = new File(campaignLocations + campaign.getCampaignName() + "/characters");
        List<String> fileNameList = new ArrayList<>();
        File[] fileList = repo.listFiles();
        if (!Objects.isNull(fileList)) {
            for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                if (Objects.equals(fileList[i].getName().substring(fileList[i].getName().length() - 5), ".json")) {
                    try {
                        fileNameList.add(fileList[i].getName());
                    } catch (Exception e) {
                        throw new CharacterLoadingException(e.toString());
                    }
                }
            }
        }
        PCCharacters.setItems(FXCollections.observableList(fileNameList));
    }

    @FXML
    private void refreshStatList() {
        PCStats.setItems(FXCollections.observableList(currentCharacter.getStats()));
    }

    @FXML
    private void refreshTalentList() {
        PCTalents.setItems(FXCollections.observableList(currentCharacter.getTalents()));
    }

    //</editor-fold>
    //<editor-fold desc="PC DATA MANIPULATION">
    @FXML

    protected void setupPCData(Character character) {

        PCName.setText(character.getName());
        PCClass.setText(character.getRpgRole());
        PCDescArea.setText(character.getDescription());
        if (Objects.isNull(character.getStats())) {
            character.setStats(new ArrayList<>());
        }
        PCStats.setItems(FXCollections.observableList(character.getStats()));
        if (Objects.isNull(character.getTalents())) {
            character.setTalents(new ArrayList<>());
        }
        PCTalents.setItems(FXCollections.observableList(character.getTalents()));
        if (Objects.isNull(character.getEquipment())) {
            character.setEquipment(new ArrayList<>());
        }

    }

    @FXML
    protected void clearPCData() {

        PCName.setText(null);
        PCClass.setText(null);
        PCDescArea.setText(null);
        PCStats.setItems(null);

    }

    @FXML
    protected void clearItemData() {

        PCItemName.setText(null);
        PCItemPrice.setText(null);
        PCItemAvailability.setText(null);
        PCItemDescription.setText(null);
        PCWeaponDamage.setText(null);
        PCWeaponRange.setText(null);
        PCWeaponType.setText(null);
        PCArmourBodyPart.setText(null);
        PCArmourProtectionValue.setText(null);
        PCArmourWorn.setSelected(false);

    }

    private void fetchSelectedCharacter(MultipleSelectionModel<String> multipleSelectionModel) {
        try {
            clearPCData();
            PCDescArea.setStyle("-fx-text-fill: black;");
            Character character = mapper.readValue(new File(campaignLocations + campaign.getCampaignName() + "/characters/" + multipleSelectionModel.getSelectedItem()), Character.class);
            setupPCData(character);
            currentCharacter = character;
            unsetPCUnsaved();
        } catch (Exception e) {
            PCDescArea.setText(e.toString());
            PCDescArea.setStyle("-fx-text-fill: red;");
        }
    }

    private void fetchSelectedItem(SingleSelectionModel<? extends Item> singleSelectionModel) {
        clearItemData();
        PCItemName.setText(singleSelectionModel.getSelectedItem().getName());
        PCItemPrice.setText(singleSelectionModel.getSelectedItem().getPrice());
        PCItemAvailability.setText(singleSelectionModel.getSelectedItem().getAvailability());
        PCItemDescription.setText(singleSelectionModel.getSelectedItem().getDescription());
        switch(singleSelectionModel.getSelectedItem().getType()){
            case "weapon":
                Weapon weapon = (Weapon) singleSelectionModel.getSelectedItem();
                PCWeaponDamage.setText(weapon.getDamage());
                PCWeaponRange.setText(weapon.getRange());
                PCWeaponType.setText(weapon.getWeaponType());
                break;
            case "armour":
                Armour armour = (Armour) singleSelectionModel.getSelectedItem();
                PCArmourBodyPart.setText(armour.getBodypart());
                PCArmourProtectionValue.setText(armour.getProtection());
                break;
            case "item":
                break;
        }

    }
    // </editor-fold>

    //<editor-fold desc="BUTTON HANDLING">

    @FXML
    private void saveStat() {
        try {
            Long statValue = Long.valueOf(PCInputStatValue.getText());
            Statistic statistic = new Statistic(PCAvailableStatList.getValue().toString(), String.valueOf(statValue));
            if (currentCharacter.getStats().stream().filter(stat -> Objects.equals(stat.getName(), PCAvailableStatList.getValue().toString())).findFirst().isEmpty()) {
                currentCharacter.getStats().add(statistic);
                refreshStatList();
                setPCUnsaved();
            } else {
                PCDescArea.setText("THIS STAT IS ALREADY ADDED");
                unsetPCUnsaved();
            }

        } catch (Exception ex) {
            PCDescArea.setText("STAT VALUE MUST BE NUMERIC");
            unsetPCUnsaved();
        }
    }

    @FXML
    private void saveTalent() {
        Talent talent = new Talent(PCTalentNameInput.getText(), PCTalentStatInput.getValue().getShorthand(), PCTalentProficiencyInput.getValue());
        if (Objects.nonNull(currentCharacter.getTalents())) {
            Optional<Talent> potentialTalentToBeReplaced = currentCharacter.getTalents().stream().filter(talentIter -> Objects.equals(talentIter.getName(), PCTalentNameInput.getText())).findFirst();
            potentialTalentToBeReplaced.ifPresent(value -> currentCharacter.getTalents().remove(value));
        }
        currentCharacter.getTalents().add(talent);
        refreshTalentList();
        setPCUnsaved();


    }

    @FXML
    private void saveItem() {
        Talent talent = new Talent(PCTalentNameInput.getText(), PCTalentStatInput.getValue().getShorthand(), PCTalentProficiencyInput.getValue());
        if (Objects.nonNull(currentCharacter.getTalents())) {
            Optional<Talent> potentialTalentToBeReplaced = currentCharacter.getTalents().stream().filter(talentIter -> Objects.equals(talentIter.getName(), PCTalentNameInput.getText())).findFirst();
            potentialTalentToBeReplaced.ifPresent(value -> currentCharacter.getTalents().remove(value));
        }
        currentCharacter.getTalents().add(talent);
        refreshTalentList();
        setPCUnsaved();


    }

    @FXML
    private void savePC() {
        Character character = new Character();
        character.setName(PCName.getText());
        character.setRpgRole(PCClass.getText());
        character.setDescription(PCDescArea.getText());
        character.setStats(PCStats.getItems().stream().toList());
        character.setTalents(PCTalents.getItems().stream().toList());
        try {
            mapper.writeValue(new File(campaignLocations + campaign.getCampaignName() + "/characters/" + character.getName() + ".json"), character);
            refreshCharacterList();
            unsetPCUnsaved();
        } catch (IOException | CharacterLoadingException e) {
            throw new RuntimeException(e);
        }
    }

    // </editor-fold>
    @Override


    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCampaign();
        fetchCharactersList();
        initailizePCScreenObjects();
        PCCharacters.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> fetchSelectedCharacter(PCCharacters.getSelectionModel()));

//        PCEquipment.getSelectionModel().selectedItemProperty().addListener(
//                (ov, old_val, new_val) -> fetchSelectedItem(PCEquipment.getSelectionModel()));
        PCName.textProperty().addListener((ov, old_val, new_val) -> setPCUnsaved());
        PCClass.textProperty().addListener((ov, old_val, new_val) -> setPCUnsaved());
        PCDescArea.textProperty().addListener((ov, old_val, new_val) -> setPCUnsaved());
        PCStatValue.setOnEditCommit(event -> setPCUnsaved());
        PCSaveCharacter.setOnAction(ActionEvent -> savePC());
        PCSaveStat.setOnAction(ActionEvent -> saveStat());
        PCSaveTalent.setOnAction(ActionEvent -> saveTalent());
        PCItemList.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> fetchSelectedItem(PCItemList.getSelectionModel())
        );
        PCSaveItem.setOnAction(ActionEvent -> saveItem());
    }
}