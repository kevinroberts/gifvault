package com.vinberts.gifvault.controllers.settings;

import com.jfoenix.controls.JFXTextField;
import com.vinberts.gifvault.utils.AlertMaker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class SettingsController implements Initializable {

    @FXML
    private JFXTextField giphyAPIKeyField;

    @FXML
    private JFXTextField gifVaultFolderField;

    SystemPreferences preferences;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        preferences = SystemPreferences.getPreferences();
        giphyAPIKeyField.setText(preferences.getGiphyAPIKey());
        gifVaultFolderField.setText(preferences.getGifVaultFolderLocation());
    }

    private Stage getStage() {
        return ((Stage) giphyAPIKeyField.getScene().getWindow());
    }

    public void handleSavePreferencesAction(final ActionEvent actionEvent) {
        SystemPreferences systemPreferences = SystemPreferences.getPreferences();
        systemPreferences.setGiphyAPIKey(giphyAPIKeyField.getText());
        systemPreferences.setGifVaultFolderLocation(gifVaultFolderField.getText());

        SystemPreferences.writePreferenceToFile(systemPreferences);

    }

    public void handleDatabaseExportAction(final ActionEvent actionEvent) {
        AlertMaker.showSimpleAlert("Not impl", "feature coming soon");
    }

    public void handleSetVaultFolderAction(final ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location for Gif Vault's Storage");
        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory == null) {
            AlertMaker.showErrorMessage("Selection cancelled", "No Valid Directory Found");
        } else {
            gifVaultFolderField.setText(selectedDirectory.getPath());
        }
    }
}
