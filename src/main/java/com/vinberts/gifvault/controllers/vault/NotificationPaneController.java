package com.vinberts.gifvault.controllers.vault;

import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.database.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
public class NotificationPaneController implements Initializable {

    @FXML
    private Label infoTextLabel;

    @FXML
    private ComboBox<GifFolder> folderChooserComboBox;

    private GifFolder[] folderSelectionOptions;

    private VaultController vaultController;
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(NotificationPaneController.class);

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

    public void refreshFolders() {
        GifFolder selectedFolder = folderChooserComboBox.getValue();
        List<GifFolder> gifFolders = DatabaseHelper.getListOfFolders(50, 0, false);
        folderSelectionOptions = gifFolders.toArray(new GifFolder[0]);
        folderChooserComboBox.setItems(FXCollections
                    .observableArrayList(folderSelectionOptions));
        if (folderChooserComboBox.getItems().contains(selectedFolder)) {
                folderChooserComboBox.setValue(selectedFolder);
        } else {
                folderChooserComboBox.setValue(folderSelectionOptions[0]);
        }
    }

    public void setVaultController(final VaultController vaultController) {
        this.vaultController = vaultController;
    }

    public void updateInfoText(String text) {
        infoTextLabel.setText(text);
    }
}
