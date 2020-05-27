package com.vinberts.gifvault.controllers.gihpy;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.trievosoftware.giphy4j.Giphy;
import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import com.trievosoftware.giphy4j.entity.search.SearchFeed;
import com.trievosoftware.giphy4j.exception.GiphyException;
import com.vinberts.gifvault.controllers.ChildController;
import com.vinberts.gifvault.controllers.settings.SystemPreferences;
import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.data.GiphyGif;
import com.vinberts.gifvault.database.DatabaseHelper;
import com.vinberts.gifvault.events.FavoritedEvent;
import com.vinberts.gifvault.utils.AlertMaker;
import com.vinberts.gifvault.utils.AppUtils;
import com.vinberts.gifvault.views.GiphyCell;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.vinberts.gifvault.utils.AppConstants.ALL_GIFS;
import static com.vinberts.gifvault.utils.AppConstants.ALL_STICKERS;
import static com.vinberts.gifvault.utils.AppConstants.FAVED_TEXT;
import static com.vinberts.gifvault.utils.AppConstants.GIPHY_API_KEY_PROP;
import static com.vinberts.gifvault.utils.AppConstants.HEARTED_CLASS;
import static com.vinberts.gifvault.utils.AppConstants.UNFAVED_TEXT;
import static com.vinberts.gifvault.utils.AppUtils.setTimeout;
import static de.jensd.fx.glyphs.GlyphsDude.createIcon;

/**
 *
 */
@Slf4j
public class GiphyController extends ChildController {

    @FXML
    private AnchorPane giphyPane;

    @FXML
    private JFXButton nextPageButton;

    @FXML
    private JFXButton prevPageButton;

    @FXML
    private JFXButton searchButton;

    @FXML
    private GridView gridView;

    @FXML
    private JFXComboBox giphyComboBox;

    // When JDK 12+ is used JFX text field has issues
    // https://stackoverflow.com/questions/55889654/illegalaccessexception-for-jfxtextfield-with-java-sdk-12
    @FXML
    private JFXTextField searchGiphyTextField;

    private String giphySearchOptions[] =
            { ALL_GIFS, ALL_STICKERS };

    private final int limit = 25;
    private int offset = 0;

    SystemPreferences preferences;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        preferences = SystemPreferences.getPreferences();
        // Initialize search options
        giphyComboBox.setItems(FXCollections
                .observableArrayList(giphySearchOptions));
        giphyComboBox.setValue(giphySearchOptions[0]);
        Text icon =
                createIcon(FontAwesomeIcon.SEARCH, "14pt");
        searchButton.setGraphic(icon);
        // set search action to perform when user hit Enter key
        searchGiphyTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.doGiphySearchAction(null);
            }
        });
        // integrate autocomplete to search example
        //TextFields.bindAutoCompletion(searchGiphyTextField, "fire", "banana", "fart");

        gridView.setCellFactory(giphyGrid -> new GridCell<GiphyCell>() {
            @Override
            public void updateItem(GiphyCell item, boolean empty) {
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
        ObservableList<GiphyCell> list = FXCollections.observableArrayList();

        Platform.runLater(() -> {
            // set focus to search field by default
            searchGiphyTextField.requestFocus();
            try {
                Giphy giphy = new Giphy(System.getProperty(GIPHY_API_KEY_PROP));
                SearchFeed feed = giphy.trend();
                for (GiphyData giphyData: feed.getDataList()) {
                    Optional<GiphyGif> gifOptional = DatabaseHelper.getGiphyGifById(giphyData.getId());
                    boolean isFavorite = gifOptional.isPresent();
                    GiphyCell cell = new GiphyCell(giphyData, isFavorite);
                    list.add(cell);
                }
                gridView.setItems(list);
            } catch (GiphyException e) {
                log.error("Giphy exception occurred", e);
                gridView.setItems(AppUtils.getDummyListOfGiphyVideos());
                AlertMaker.showErrorMessage(e, "Giphy exception occurred", "Could not retrieve trending gifs.");
            }
        });

        // initialize grid view events
        gridView.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleGridActionEvent);

        giphyPane.addEventHandler(FavoritedEvent.UNFAVORITED, event -> {
            log.info("Un-favorite event handled for gif vault item");
            ObservableList<GiphyCell> gridItems = gridView.getItems();
            gridItems.removeIf(cell -> cell.getGiphyData().getId().equals(event.getGifVault().getGiphyGif().getId()));
            gridView.setItems(gridItems);
        });
    }

    private void handleGridActionEvent(MouseEvent event) {
        EventTarget comp = event.getTarget();
        Node node = event.getPickResult().getIntersectedNode();
        // check if this is a grid cell
        if (comp instanceof GridCell) {
            log.debug("Grid cell clicked");
            GridCell gridCell = ((GridCell) comp);
            GiphyCell cell = (GiphyCell) gridCell.getGraphic();
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
                if (!node.getStyleClass().contains(HEARTED_CLASS)) {
                    log.debug("adding gif as favorite");
                    GifFolder folder = DatabaseHelper.getUncategorizedFolder();
                    GifVault gifVault = AppUtils.prepareNewVaultEntryForGif(Optional.of(folder), cell);
                    DatabaseHelper.insertNewGifVaultEntry(gifVault);

                    boolean success = DatabaseHelper.insertNewGiphyEntry(cell, gifVault);
                    if (!success) {
                        AlertMaker.showErrorMessage("Oops", "Something went wrong trying to add this giphy to your favorites.");
                    } else {
                        FavoritedEvent favoritedEvent = new FavoritedEvent(cell, this.getMainAppController().getVaultTab().getContent(), FavoritedEvent.FAVORITED, gifVault);
                        Text heartIcon =
                                createIcon(FontAwesomeIcon.HEART, "10pt");
                        ((JFXButton) node).setGraphic(heartIcon);
                        node.setAccessibleText(FAVED_TEXT);
                        node.getStyleClass().add(HEARTED_CLASS);
                        try {
                            AppUtils.createFaveFolderAndDownloadAsset(gifVault,
                                    cell.getGiphyData().getImages().getOriginalMp4().getMp4(),
                                    cell.getGiphyData().getImages().getOriginal().getUrl(),
                                    this.getMainAppController().getVaultTab(),
                                    favoritedEvent);
                        } catch (IOException e) {
                            AlertMaker.showErrorMessage("Oops", "Something went wrong trying to add this giphy to your favorites.");
                        }
                    }
                } else if (node.getStyleClass().contains(HEARTED_CLASS)) {
                    log.debug("removing gif as favorite");
                    String vaultId = DatabaseHelper.deleteGiphyGifById(cell.getGiphyData().getId(), true);
                    if (StringUtils.isNotEmpty(vaultId)) {
                        GifVault vaultToBeRemoved = new GifVault();
                        vaultToBeRemoved.setId(vaultId);
                        FavoritedEvent unFaveEvent = new FavoritedEvent(cell,
                                this.getMainAppController().getVaultTab().getContent(),
                                FavoritedEvent.UNFAVORITED, vaultToBeRemoved);
                        this.getMainAppController().getVaultTab().getContent().fireEvent(unFaveEvent);
                        DatabaseHelper.deleteGifVaultById(vaultId);
                        Text heartIcon =
                                createIcon(FontAwesomeIcon.HEART_ALT, "10pt");
                        ((JFXButton) node).setGraphic(heartIcon);
                        node.setAccessibleText(UNFAVED_TEXT);
                        node.getStyleClass().remove(HEARTED_CLASS);
                    } else {
                        AlertMaker.showErrorMessage("Oops", "Something went wrong trying to remove this giphy to your favorites.");
                    }
                }
            }
        }
    }

    public void getTrendingAction(final ActionEvent actionEvent) {
        this.getMainAppController().showSpinner();
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    // reset any offsets / paging actions (trending does not have pages)
                    offset = 0;
                    nextPageButton.setDisable(true);
                    prevPageButton.setDisable(true);

                    releaseAllPlayers();
                    ObservableList<GiphyCell> list = FXCollections.observableArrayList();
                    Giphy giphy = new Giphy(System.getProperty(GIPHY_API_KEY_PROP));
                    String type = (String) giphyComboBox.getValue();
                    SearchFeed feed;
                    if (type.equals(ALL_GIFS)) {
                        feed = giphy.trend();
                    } else {
                        feed = giphy.trendSticker();
                    }
                    for (GiphyData giphyData: feed.getDataList()) {
                        Optional<GiphyGif> gifOptional = DatabaseHelper.getGiphyGifById(giphyData.getId());
                        boolean isFavorite = gifOptional.isPresent();
                        GiphyCell cell = new GiphyCell(giphyData, isFavorite);
                        list.add(cell);
                    }
                    gridView.setItems(list);
                } catch (GiphyException e) {
                    log.error("Giphy exception occurred", e);
                    AlertMaker.showErrorMessage(e, "Giphy exception occurred", "Could not retrieve trending gifs.");
                }
                setTimeout(() -> this.getMainAppController().hideSpinner(), 500);
            });
        });
        thread.start();
    }

    public void doGiphySearchAction(final ActionEvent actionEvent) {
        String searchText = searchGiphyTextField.getText();
        if (Objects.nonNull(searchText) && !searchText.equals("")) {
            // if this is a new page search set offset back to 0
            if (Objects.isNull(actionEvent)) {
                offset = 0;
                prevPageButton.setDisable(true);
            }
            this.getMainAppController().showSpinner();
            Thread thread = new Thread(() -> {
                Platform.runLater(() -> {
                    releaseAllPlayers();
                    try {
                        ObservableList<GiphyCell> list = FXCollections.observableArrayList();
                        Giphy giphy = new Giphy(System.getProperty(GIPHY_API_KEY_PROP));

                        String type = (String) giphyComboBox.getValue();
                        SearchFeed feed;
                        if (offset == 0 && nextPageButton.isDisable()) {
                            nextPageButton.setDisable(false);
                        }
                        if (type.equals(ALL_GIFS)) {
                            feed = giphy.search(searchText, limit, offset);
                        } else {
                            feed = giphy.searchSticker(searchText, limit, offset);
                        }
                        for (GiphyData giphyData: feed.getDataList()) {
                            Optional<GiphyGif> gifOptional = DatabaseHelper.getGiphyGifById(giphyData.getId());
                            boolean isFavorite = gifOptional.isPresent();
                            GiphyCell cell = new GiphyCell(giphyData, isFavorite);
                            list.add(cell);
                        }
                        gridView.setItems(list);
                    } catch (GiphyException e) {
                        log.error("Giphy exception occurred", e);
                        AlertMaker.showErrorMessage(e, "Giphy exception occurred", "Could not retrieve trending gifs.");
                    }
                    setTimeout(() -> this.getMainAppController().hideSpinner(), 500);
                });
            });
            thread.start();
        } else {
            AlertMaker.showErrorMessage("Search term required.", "Please enter a search term first.");
        }
    }

    public void getNextPageAction(final ActionEvent actionEvent) {
        log.debug("Next page action called");
        if (offset == 0) {
            offset += limit;
            prevPageButton.setDisable(false);
            this.doGiphySearchAction(actionEvent);
        } else if (offset > 0) {
            offset += limit;
            this.doGiphySearchAction(actionEvent);
        }
    }

    public void getPrevPageAction(final ActionEvent actionEvent) {
        log.debug("Prev page action called");
        if (offset >= limit) {
            offset -= limit;
            if (offset == 0) {
                prevPageButton.setDisable(true);
            }
            this.doGiphySearchAction(actionEvent);
        } else {
            log.info("Offset already less than limit");
        }
    }

    public void releaseAllPlayers() {
        ObservableList<GiphyCell> currentGifs = gridView.getItems();
        currentGifs.forEach(GiphyCell::releasePlayer);
    }

}
