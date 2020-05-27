package com.vinberts.gifvault.controllers.vault;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.vinberts.gifvault.controllers.ChildController;
import com.vinberts.gifvault.controllers.settings.SystemPreferences;
import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.database.DatabaseHelper;
import com.vinberts.gifvault.events.CustomCloseEvent;
import com.vinberts.gifvault.events.FavoritedEvent;
import com.vinberts.gifvault.utils.AlertMaker;
import com.vinberts.gifvault.utils.AppUtils;
import com.vinberts.gifvault.views.VaultCell;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.vinberts.gifvault.utils.AppConstants.UNCATEGORIZED;
import static de.jensd.fx.glyphs.GlyphsDude.createIcon;

/**
 *
 */
@Slf4j
public class VaultController extends ChildController {

    @FXML
    private JFXButton manageFoldersButton;

    @FXML
    private Label totalLabel;

    @FXML
    private JFXButton nextPageButton;

    @FXML
    private JFXButton prevPageButton;

    @FXML
    private AnchorPane vaultPane;

    @FXML
    private GridView gridView;

    @FXML
    private JFXComboBox<GifFolder> folderSelectorComboBox;

    private NotificationPaneController notificationPaneController;

    private GifFolder[] folderSelectionOptions;

    private final int limit = 25;
    private int offset = 0;
    private int resultSize = 25;

    private int numberOfVaultsSelected = 0;

    SystemPreferences preferences;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        preferences = SystemPreferences.getPreferences();
        List<GifFolder> gifFolders = DatabaseHelper.getListOfFolders(50, 0, true);
        folderSelectionOptions = gifFolders.toArray(new GifFolder[0]);
        folderSelectorComboBox.setItems(FXCollections
                .observableArrayList(folderSelectionOptions));
        folderSelectorComboBox.setValue(folderSelectionOptions[0]);
        Text icon =
                createIcon(MaterialIcon.CREATE_NEW_FOLDER, "14pt");
        manageFoldersButton.setGraphic(icon);

        // setup notifications bar
        FXMLLoader loader = new FXMLLoader(VaultController.class.getResource("/ui/notificationPane.fxml"));
        try {
            Parent notificationNode = loader.load();
            notificationPaneController = loader.getController();
            notificationPaneController.setVaultController(this);
            this.getMainAppController().getNotificationsPane().setGraphic(notificationNode);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
            log.debug("Favorite event handled for gif vault item " + event.getGifVault().getTitle());
            String selectedFolder = folderSelectorComboBox.getValue().getName();
            if (selectedFolder.equals(UNCATEGORIZED)) {
                GifVault newVault = event.getGifVault();
                ObservableList<VaultCell> gridItems = gridView.getItems();
                String mp4Path = preferences.getGifVaultFolderLocation() + File.separator
                        + newVault.getStorageLocation() + File.separator + newVault.getMp4Filename();
                VaultCell vaultCell = new VaultCell(newVault, mp4Path);
                gridItems.add(0, vaultCell);
                if (gridItems.size() > limit) {
                    gridItems.get(gridItems.size()-1).releasePlayer();
                    gridItems.remove(gridItems.size()-1);
                }
                gridView.setItems(gridItems);
                resultSize+=1;
                updatePageCount();
            }
        });

        vaultPane.addEventHandler(FavoritedEvent.UNFAVORITED, event -> {
            log.debug("Un-favorite event handled for gif vault item");
            ObservableList<VaultCell> gridItems = gridView.getItems();
            gridItems.removeIf(cell -> cell.getGifVault().getId().equals(event.getGifVault().getId()));
            gridView.setItems(gridItems);
        });

        gridView.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleGridActionEvent);

        // load the initial folder uncategorized
        Platform.runLater(() -> {
            GifFolder uncatFolder = DatabaseHelper.getUncategorizedFolder();
            List<GifVault> gifVaultList = DatabaseHelper.getGifVaultsByFolder(limit, offset, uncatFolder);
            resultSize = DatabaseHelper.getNumberOfGifVaultsByFolder(uncatFolder);
            updatePageCount();
            if (resultSize > limit) {
                nextPageButton.setDisable(false);
            }
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
                if (Objects.nonNull(cell.getGifVault().getGiphyGif())) {
                    DatabaseHelper.deleteGiphyGifById(cell.getGifVault().getGiphyGif().getId(), false);
                }
                // remove associated files from gif vault / filesystem
                AppUtils.removeAssetsFromFileSystem(cell.getGifVault());
                DatabaseHelper.deleteGifVaultById(cell.getGifVault().getId());
                // remove item from Grid View UI
                ObservableList<VaultCell> gridItems = gridView.getItems();
                gridItems.removeIf(vaultCell -> vaultCell.getGifVault().getId().equals(cell.getGifVault().getId()));
                gridView.setItems(gridItems);
                this.getMainAppController().getGiphyTab().getContent().fireEvent(unFaveEvent);
            } else if (node instanceof StackPane) {
                log.debug("handling checkbox action from grid list");
                if (cell.getSelectionBox().isSelected()) {
                    cell.getSelectionBox().setSelected(false);
                    if (numberOfVaultsSelected > 0) {
                        numberOfVaultsSelected--;
                    }
                    if (numberOfVaultsSelected == 0) {
                        this.getMainAppController().getNotificationsPane().hide();
                    }
                } else {
                    cell.getSelectionBox().setSelected(true);
                    numberOfVaultsSelected++;
                    notificationPaneController.updateInfoText(String.format("Add %d Selected Gifs to Folder:", numberOfVaultsSelected));
                    this.getMainAppController().getNotificationsPane().show();
                }

            }
        }
    }


    public void doFilterAction(final ActionEvent actionEvent) {
        log.debug("starting do filter action");
        String selectedFolder = folderSelectorComboBox.getValue().getName();
        GifFolder folder = DatabaseHelper.getGifFolderByName(selectedFolder);
        resultSize = DatabaseHelper.getNumberOfGifVaultsByFolder(folder);
        Platform.runLater(() -> {
            updatePageCount();
            if (Objects.isNull(actionEvent)) {
                offset = 0;
                prevPageButton.setDisable(true);
            }
            if ((offset + limit) >= resultSize) {
                nextPageButton.setDisable(true);
            } else if (resultSize > limit) {
                nextPageButton.setDisable(false);
            }

            releaseAllPlayers();
            ObservableList<VaultCell> list = FXCollections.observableArrayList();
            List<GifVault> gifVaultList = DatabaseHelper.getGifVaultsByFolder(limit, offset, folder);
            for (GifVault gifVault: gifVaultList) {
                String mp4Path = preferences.getGifVaultFolderLocation() + File.separator
                        + gifVault.getStorageLocation() + File.separator + gifVault.getMp4Filename();
                VaultCell vaultCell = new VaultCell(gifVault, mp4Path);
                list.add(vaultCell);
            }
            gridView.setItems(list);
        });
    }

    public void getNextPageAction(final ActionEvent actionEvent) {
        log.debug("starting get next page action");
        if (offset == 0) {
            offset += limit;
            prevPageButton.setDisable(false);
            this.doFilterAction(actionEvent);
        } else if (offset > 0) {
            offset += limit;
            this.doFilterAction(actionEvent);
        }
    }

    public void getPrevPageAction(final ActionEvent actionEvent) {
        log.debug("starting get prev page action");
        if (offset >= limit) {
            offset -= limit;
            if (offset == 0) {
                prevPageButton.setDisable(true);
            }
            this.doFilterAction(actionEvent);
        } else {
            log.info("Offset already less than limit");
        }
    }

    private void updatePageCount() {
        double currentPage = Math.ceil(((double)offset - 1) / (double)limit) + 1;
        double totalPages = Math.ceil((double)resultSize / (double)limit);
        totalLabel.setText(String.format("Page %02d of %02d", Math.round(currentPage), Math.round(totalPages)));
    }

    private void releaseAllPlayers() {
        ObservableList<VaultCell> currentGifs = gridView.getItems();
        currentGifs.forEach(VaultCell::releasePlayer);
    }

    public void openFolderManager(final ActionEvent actionEvent) {
        CustomCloseEvent closeEvent = new CustomCloseEvent();
        closeEvent.setVaultController(this);
        AppUtils.loadWindow(VaultController.class.getResource("/ui/manageFolders.fxml"),
                "Manage Gif Vault Folders",
                null,
                closeEvent);
    }

    public void refreshFolders() {
        GifFolder selectedFolder = folderSelectorComboBox.getValue();
        List<GifFolder> gifFolders = DatabaseHelper.getListOfFolders(50, 0, true);
        if (selectedFolder.getName().equals(UNCATEGORIZED)) {
            folderSelectionOptions = gifFolders.toArray(new GifFolder[0]);
            folderSelectorComboBox.setItems(FXCollections
                    .observableArrayList(folderSelectionOptions));
            folderSelectorComboBox.setValue(folderSelectionOptions[0]);
        } else {
            folderSelectionOptions = gifFolders.toArray(new GifFolder[0]);
            folderSelectorComboBox.setItems(FXCollections
                    .observableArrayList(folderSelectionOptions));
            if (folderSelectorComboBox.getItems().contains(selectedFolder)) {
                folderSelectorComboBox.setValue(selectedFolder);
            } else {
                folderSelectorComboBox.setValue(folderSelectionOptions[0]);
            }
        }
    }

    public void addSelectedGifsToFolder(GifFolder folder) {
        // check if selected folder and add to folder are the same
        // GifFolder selectedFolder = folderSelectorComboBox.getValue();
        ObservableList<VaultCell> gridItems = gridView.getItems();
        Collection<GifVault> selectedVaults = new ArrayList<>();
        for (VaultCell cell: gridItems) {
            if (cell.getSelectionBox().isSelected()) {
                selectedVaults.add(cell.getGifVault());
            }
        }
        boolean updated = DatabaseHelper.addGifVaultsToFolder(folder, selectedVaults);
        if (updated) {
            this.getMainAppController().getNotificationsPane().hide();
            numberOfVaultsSelected = 0;
            List<VaultCell> cellsToRemove = new ArrayList<>();
            for (VaultCell cell: gridItems) {
                if (cell.getSelectionBox().isSelected()) {
                    cell.getSelectionBox().setSelected(false);
                    cell.releasePlayer();
                    cellsToRemove.add(cell);
                }
            }
            Platform.runLater(() ->{
                gridView.getItems().removeAll(cellsToRemove);
            });
        } else {
            AlertMaker.showErrorMessage("Uhoh", "Something went wrong trying to save gifs to your folder.");
        }
    }
}
