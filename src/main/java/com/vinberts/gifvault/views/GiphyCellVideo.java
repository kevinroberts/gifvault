package com.vinberts.gifvault.views;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 *
 */
public class GiphyCellVideo extends VBox {

    Media media;
    MediaPlayer player;
    MediaView view;
    Pane pane;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GiphyCellVideo.class);

    public GiphyCellVideo(final String gifSource) {
        super();
        media = new Media(gifSource);
        media.setOnError(() -> {
            // Handle asynchronous error in Media object.
            log.error("Handle asynchronous error in Media object");
        });
        player = new MediaPlayer(media);
        view = new MediaView(player);
        pane = new Pane();
        pane.getChildren().add(view);
        view.setFitHeight(200);
        view.setFitWidth(300);
        pane.setMaxWidth(300);
        pane.setMaxHeight(200);
        // set background color of video player to black
        pane.setStyle("-fx-background-color: black;");
        Platform.runLater(() -> {
            log.info("Video playing.");
            player.play();
        });
        player.setOnEndOfMedia(() -> {
            player.seek(Duration.ZERO);
            player.play();
        });

        this.getChildren().add(pane);
    }

}
