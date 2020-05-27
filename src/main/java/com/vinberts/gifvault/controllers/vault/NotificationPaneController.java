package com.vinberts.gifvault.controllers.vault;

import com.jfoenix.controls.JFXComboBox;
import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.database.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
@Slf4j
public class NotificationPaneController implements Initializable {

    @FXML
    private Label infoTextLabel;

    @FXML
    private JFXComboBox<GifFolder> folderChooserComboBox;

    private GifFolder[] folderSelectionOptions;

    private VaultController vaultController;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        List<GifFolder> gifFolders = DatabaseHelper.getListOfFolders(50, 0, false);
        folderSelectionOptions = gifFolders.toArray(new GifFolder[0]);
        folderChooserComboBox.setItems(FXCollections
                .observableArrayList(folderSelectionOptions));
        folderChooserComboBox.setValue(folderSelectionOptions[0]);
    }

    public void addCheckedToFolderAction(final ActionEvent actionEvent) {
        log.info("Adding checked items to folder");
        GifFolder selectedFolder = folderChooserComboBox.getValue();
        this.vaultController.addSelectedGifsToFolder(selectedFolder);
    }

    public void setVaultController(final VaultController vaultController) {
        this.vaultController = vaultController;
    }

    public void updateInfoText(String text) {
        infoTextLabel.setText(text);
    }
}
