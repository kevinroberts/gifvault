package com.vinberts.gifvault.controllers.vault;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.vinberts.gifvault.controllers.ChildController;
import com.vinberts.gifvault.controllers.settings.SystemPreferences;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.database.DatabaseHelper;
import com.vinberts.gifvault.events.FavoritedEvent;
import com.vinberts.gifvault.views.VaultCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
@Slf4j
public class VaultController extends ChildController {

    @FXML
    private AnchorPane vaultPane;

    @FXML
    private GridView gridView;

    @FXML
    private JFXComboBox folderSelectorComboBox;

    private String folderSelectionOptions[] =
            { "Uncategorized", "Stickers" };

    private final int limit = 25;
    private int offset = 0;

    SystemPreferences preferences;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        preferences = SystemPreferences.getPreferences();

        folderSelectorComboBox.setItems(FXCollections
                .observableArrayList(folderSelectionOptions));
        folderSelectorComboBox.setValue(folderSelectionOptions[0]);

        gridView.setCellFactory(gifGrid -> new GridCell<VaultCell>() {
            @Override
            public void updateItem(VaultCell item, boolean empty) {
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        gridView.setCellHeight(250);
        gridView.setCellWidth(300);
        gridView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        ObservableList<VaultCell> list = FXCollections.observableArrayList();

        vaultPane.addEventHandler(FavoritedEvent.FAVORITED, event -> {
            log.info("Favorite event handled for gif vault item " + event.getGifVault().getTitle());
            this.doFilterAction(null);
        });

        vaultPane.addEventHandler(FavoritedEvent.UNFAVORITED, event -> {
            log.info("Un-favorite event handled for gif vault item");
            ObservableList<VaultCell> gridItems = gridView.getItems();
            gridItems.removeIf(cell -> cell.getGifVault().getId().equals(event.getGifVault().getId()));
            gridView.setItems(gridItems);
        });

        gridView.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleGridActionEvent);

        // load the initial folder uncategorized
        Platform.runLater(() -> {
            List<GifVault> gifVaultList = DatabaseHelper.getGifVaultsByFolder(limit, offset, DatabaseHelper.getUncategorizedFolder());
            for (GifVault gifVault: gifVaultList) {
                String mp4Path = preferences.getGifVaultFolderLocation() + File.separator
                        + gifVault.getStorageLocation() + File.separator + gifVault.getMp4Filename();
                VaultCell vaultCell = new VaultCell(gifVault, mp4Path);
                list.add(vaultCell);
            }
            gridView.setItems(list);
        });

    }

    private void handleGridActionEvent(MouseEvent event) {
        EventTarget comp = event.getTarget();
        Node node = event.getPickResult().getIntersectedNode();
        if (comp instanceof GridCell) {
            log.debug("Grid cell clicked");
            GridCell gridCell = ((GridCell) comp);
            VaultCell cell = (VaultCell) gridCell.getGraphic();
            if (node instanceof Text) {
                String anchorLink = cell.getAnchorLink();
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        URI url = URI.create(anchorLink);
                        Desktop.getDesktop().browse(url);
                    } catch (IOException ex) {
                        log.error("IOException occurred", ex);
                    }
                }
            } else if (node instanceof JFXButton) {
                log.debug("removing gif from favorites");
                FavoritedEvent unFaveEvent = new FavoritedEvent(cell,
                        this.getMainAppController().getGiphyTab().getContent(),
                        FavoritedEvent.UNFAVORITED, cell.getGifVault());
                DatabaseHelper.deleteGiphyGifById(cell.getGifVault().getGiphyGif().getId());
                DatabaseHelper.deleteGifVaultById(cell.getGifVault().getId());
                // remove from UI
                ObservableList<VaultCell> gridItems = gridView.getItems();
                gridItems.removeIf(vaultCell -> vaultCell.getGifVault().getId().equals(cell.getGifVault().getId()));
                gridView.setItems(gridItems);
                this.getMainAppController().getGiphyTab().getContent().fireEvent(unFaveEvent);
            }
        }
    }


    public void doFilterAction(final ActionEvent actionEvent) {
        log.debug("starting do filter action");
        Platform.runLater(() -> {
            releaseAllPlayers();
            ObservableList<VaultCell> list = FXCollections.observableArrayList();
            List<GifVault> gifVaultList = DatabaseHelper.getGifVaultsByFolder(limit, offset, DatabaseHelper.getUncategorizedFolder());
            for (GifVault gifVault: gifVaultList) {
                String mp4Path = preferences.getGifVaultFolderLocation() + File.separator
                        + gifVault.getStorageLocation() + File.separator + gifVault.getMp4Filename();
                VaultCell vaultCell = new VaultCell(gifVault, mp4Path);
                list.add(vaultCell);
            }
            gridView.setItems(list);
        });
    }

    public void getPrevPageAction(final ActionEvent actionEvent) {
        log.debug("starting get prev page action");
    }

    public void getNextPageAction(final ActionEvent actionEvent) {
        log.debug("starting get next page action");
    }

    private void releaseAllPlayers() {
        ObservableList<VaultCell> currentGifs = gridView.getItems();
        currentGifs.forEach(VaultCell::releasePlayer);
    }
}
