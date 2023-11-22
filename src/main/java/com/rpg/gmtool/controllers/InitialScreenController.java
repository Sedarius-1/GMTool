package com.rpg.gmtool.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpg.gmtool.GMToolApplication;
import com.rpg.gmtool.exceptions.ConfigurationLoadingException;
import com.rpg.gmtool.models.campaigns.Campaign;
import com.rpg.gmtool.models.systems.System;
import com.simtechdata.sceneonefx.SceneOne;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.rpg.gmtool.config.ConfigUtils.*;


@Slf4j
public class InitialScreenController implements Initializable {

    //<editor-fold desc="FIELDS">
    public ComboBox<System> ISSystemsList;
    public ListView<String> ISCampaignsList;
    public Button ISStart;
    public TextField ISCampaignName;
    public TextField ISPartyName;
    public Text ISErrorLabel;
    private System selectedSystem;
    private final ObjectMapper mapper = getObjectMapper();
    //</editor-fold>
    //<editor-fold desc="LISTS POPULATION">
    @FXML
    private void loadConfigsListFromFiles() throws ConfigurationLoadingException {
        File repo = new File(campaignLocations);
        List<String> fileNameList = new ArrayList<>();
        File[] fileList = repo.listFiles();
        for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
            if (new File(fileList[i], configFile).exists()) {
                try {
                    Campaign campaign = getObjectMapper().readValue(new File(campaignLocations + fileList[i].getName() + "/" + configFile), Campaign.class);
                    fileNameList.add(campaign.getCampaignName());
                } catch (Exception e) {
                    throw new ConfigurationLoadingException(e.toString());
                }
            }
            ISCampaignsList.setItems(FXCollections.observableList(fileNameList));
        }
    }

    @FXML
    private void loadSystemsListFromFiles() throws ConfigurationLoadingException {
        File repo = new File(systemLocations);
        List<System> fileNameList = new ArrayList<>();
        File[] fileList = repo.listFiles();
        for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
            if (Objects.equals(fileList[i].getName().substring(fileList[i].getName().length() - 5), ".json")) {
                try {
                    System system = getObjectMapper().readValue(new File(systemLocations + fileList[i].getName()), System.class);
                    fileNameList.add(system);
                } catch (Exception e) {
                    throw new ConfigurationLoadingException(e.toString());
                }
            }
        }
        ISSystemsList.setItems(FXCollections.observableList(fileNameList));
    }


    @FXML
    private void setupLists(Campaign campaign) throws IOException {
        ISCampaignName.setText(campaign.getCampaignName());
        ISSystemsList.setValue(selectedSystem);
        selectedSystem = mapper.readValue(new File(systemLocations + campaign.getSystemCodeName() + suffix), System.class);
        ISPartyName.setText(campaign.getPartyName());
    }
    //</editor-fold>
    //<editor-fold desc="LIST FETCHING">
    @FXML
    private void fetchSelectedCampaign(MultipleSelectionModel<String> selectionModel) {
        try {
            log.error(selectionModel.getSelectedItem());
            Campaign campaign = mapper.readValue(new File(campaignLocations + selectionModel.getSelectedItem() + "/config" + suffix), Campaign.class);
            selectedSystem = mapper.readValue(new File(systemLocations + campaign.getSystemCodeName() + suffix), System.class);
            setupLists(campaign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //</editor-fold>
    //<editor-fold desc="BUTTON HANDLING">
    @FXML

    private boolean createDirectoryStructure(Campaign campaign) {
        File file = new File(campaignLocations + campaign.getCampaignName());
        File characters = new File(campaignLocations + campaign.getCampaignName() + "/characters");
        File events = new File(campaignLocations + campaign.getCampaignName() + "/events");
        File logs = new File(campaignLocations + campaign.getCampaignName() + "/logs");
        File npcs = new File(campaignLocations + campaign.getCampaignName() + "/npcs");
        File sessions = new File(campaignLocations + campaign.getCampaignName() + "/sessions");
        if (file.mkdirs()
                || characters.mkdirs()
                || events.mkdirs()
                || logs.mkdirs()
                || npcs.mkdirs()
                || sessions.mkdirs()) {

            log.info("Created directory for new campaign!");
            return true;
        } else {
            log.error("Error during directory creation!");
            ISErrorLabel.setText("Error during directory creation!");
            return false;
        }
    }

    @FXML
    private void proceedWithCurrentInputs() {
        ISErrorLabel.setText("");
        ISErrorLabel.setStyle("-fx-text-fill: RED;");
        selectedSystem = ISSystemsList.getValue();
        if (Objects.equals(ISCampaignName.getText(), "") || Objects.equals(ISPartyName.getText(), "")) {
            ISErrorLabel.setText("FILL ALL FIELDS");
        } else {
            Campaign campaign = new Campaign(ISCampaignName.getText(), selectedSystem.getCodeName(), selectedSystem.getName(), ISPartyName.getText());
            if (new File(campaignLocations + campaign.getCampaignName()).exists()) {
                try {
                    mapper.writeValue(new File(currentConfigFile), campaign);
                } catch (IOException e) {
                    ISErrorLabel.setText("ERROR DURING CONFIGURATION SAVING");
                    throw new RuntimeException(e);
                }
            } else {
                if (!createDirectoryStructure(campaign)) {
                    throw new RuntimeException("Error during directory creation!");
                }
                try {
                    mapper.writeValue(new File(currentConfigFile), campaign);
                    mapper.writeValue(new File(campaignLocations + campaign.getCampaignName() + "/" + configFile), campaign);
                } catch (IOException e) {
                    ISErrorLabel.setText("Error during configuration files creation!");
                    throw new RuntimeException(e);
                }
            }
            Parent fxmlLoader;
            try {
                fxmlLoader = FXMLLoader.load(Objects.requireNonNull(GMToolApplication.class.getResource("hello-view.fxml")));
                SceneOne.set("next", fxmlLoader).size(1920, 1080).build();
                SceneOne.show("next");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //</editor-fold>
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadConfigsListFromFiles();
            loadSystemsListFromFiles();
        } catch (ConfigurationLoadingException ex) {
            throw new RuntimeException(ex);
        }
        ISCampaignsList.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> fetchSelectedCampaign(ISCampaignsList.getSelectionModel()));

        ISStart.setOnAction(ActionEvent -> proceedWithCurrentInputs());
    }

}
