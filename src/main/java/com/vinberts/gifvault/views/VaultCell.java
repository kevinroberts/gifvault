package com.vinberts.gifvault.views;

import com.jfoenix.controls.JFXButton;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.events.DeleteVaultEvent;
import com.vinberts.gifvault.events.SelectedVaultEvent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import static de.jensd.fx.glyphs.GlyphsDude.createIcon;

/**
 *
 */
@Slf4j
public class VaultCell extends VBox {

    private Pane pane;
    private BorderPane borderPane;
    private Label label;
    private JFXButton removeButton;
    private GifVault gifVault;
    private CheckBox selectionBox;
    private HBox controlsContainer;
    private Media media;
    private MediaPlayer player;
    private MediaView view;
    private final int maxTitleLen = 42;

    public VaultCell(final GifVault gifSource, String mp4Path) {
        super();
        this.gifVault = gifSource;

        URL urlSource;
        try {
            urlSource = Paths.get(mp4Path).toUri().toURL();
            media = new Media(urlSource.toString());
            media.setOnError(() -> {
                // Handle asynchronous error in Media object.
                log.error("Handle asynchronous error in Media object");
            });
            player = new MediaPlayer(media);
            view = new MediaView(player);
            view.setPreserveRatio(true);
            view.setFitWidth(300);
            view.setFitHeight(200);
        } catch (MalformedURLException e) {
            log.error("Invalid url for mp4 path: ", e);
        }

        pane = new Pane();
        pane.getChildren().add(view);
        pane.setMaxWidth(300);
        pane.setMaxHeight(250);
        // set background color of video player to black
        pane.setStyle("-fx-background-color: black;");

        this.label = new Label(String.format("%s",
                StringUtils.abbreviate(gifVault.getTitle(), maxTitleLen)));
        if (Objects.nonNull(gifVault.getGiphyGif())) {
            this.label.setAccessibleText(gifVault.getGiphyGif().getBitlyUrl());
            label.setOnMouseClicked(event -> {
                log.info("Anchor link clicked");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        URI url = URI.create(gifVault.getGiphyGif().getBitlyUrl());
                        Desktop.getDesktop().browse(url);
                    } catch (IOException ex) {
                        log.error("IOException occurred", ex);
                    }
                }
            });
            this.label.getStyleClass().add("anchor");
        }

        this.removeButton = new JFXButton();
        Text trashIcon =
                createIcon(FontAwesomeIcon.TRASH, "10pt");
        removeButton.setGraphic(trashIcon);

        removeButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            log.debug("Remove button clicked");
            DeleteVaultEvent deleteVaultEvent = new DeleteVaultEvent(removeButton, removeButton, DeleteVaultEvent.DELETED, this);
            this.fireEvent(deleteVaultEvent);
        });

        selectionBox = new CheckBox(" ");

        selectionBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                log.debug("Checkbox Selected");
                SelectedVaultEvent selectedVaultEvent = new SelectedVaultEvent(selectionBox, selectionBox, SelectedVaultEvent.SELECTED, this);
                this.fireEvent(selectedVaultEvent);
            } else {
                log.debug("Checkbox unselected");
                SelectedVaultEvent vaultEvent = new SelectedVaultEvent(selectionBox, selectionBox, SelectedVaultEvent.UNSELECTED, this);
                this.fireEvent(vaultEvent);
            }
        });

        borderPane = new BorderPane();
        controlsContainer = new HBox();
        controlsContainer.getChildren().add(selectionBox);
        HBox.setMargin(selectionBox, new Insets(5,0,0,0));
        controlsContainer.getChildren().add(removeButton);

        borderPane.setLeft(label);
        borderPane.setRight(controlsContainer);

        BorderPane.setMargin(label, new Insets(0,5,0,0));

        Platform.runLater(() -> {
            log.info("Video playing.");
            player.play();
        });

        player.setOnEndOfMedia(() -> {
            player.seek(Duration.ZERO);
            player.play();
        });
        this.setSpacing(5);
        this.getChildren().add(pane);
        this.getChildren().add(borderPane);
    }

    public void releasePlayer() {
       this.player.dispose();
    }

    public GifVault getGifVault() {
        return gifVault;
    }

    public String getAnchorLink() {
        return this.label.getAccessibleText();
    }

    public CheckBox getSelectionBox() {
        return selectionBox;
    }
}
