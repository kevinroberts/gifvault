package com.vinberts.gifvault.views;

import com.jfoenix.controls.JFXButton;
import com.vinberts.gifvault.data.GifVault;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.HyperlinkLabel;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.Objects;

import static de.jensd.fx.glyphs.GlyphsDude.createIcon;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 *
 */
@Slf4j
public class VaultCell extends VBox {

    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private ImageView videoImageView;
    private Pane pane;
    private BorderPane borderPane;
    private HyperlinkLabel label;
    private JFXButton removeButton;
    private GifVault gifVault;
    private CheckBox selectionBox;
    private HBox controlsContainer;
    private final int maxTitleLen = 42;

    public VaultCell(final GifVault gifSource, String mp4Path) {
        super();
        this.gifVault = gifSource;
        this.mediaPlayerFactory = new MediaPlayerFactory();
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        this.embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(final MediaPlayer mediaPlayer) {
                super.playing(mediaPlayer);
            }

            @Override
            public void error(final MediaPlayer mediaPlayer) {
                super.error(mediaPlayer);
                log.error("Video playback error occurred");
            }
        });
        this.embeddedMediaPlayer.controls().setRepeat(true);
        this.videoImageView = new ImageView();
        this.videoImageView.setPreserveRatio(true);
        embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(this.videoImageView));
        videoImageView.setFitWidth(300);
        videoImageView.setFitHeight(200);

        pane = new Pane();
        pane.getChildren().add(videoImageView);
        pane.setMaxWidth(300);
        pane.setMaxHeight(250);
        // set background color of video player to black
        pane.setStyle("-fx-background-color: black;");

        this.label = new HyperlinkLabel(String.format("[%s]",
                StringUtils.abbreviate(gifVault.getTitle(), maxTitleLen)));
        if (Objects.nonNull(gifVault.getGiphyGif())) {
            this.label.setAccessibleText(gifVault.getGiphyGif().getBitlyUrl());
        }

        this.removeButton = new JFXButton();
        Text trashIcon =
                createIcon(FontAwesomeIcon.TRASH, "10pt");
        removeButton.setGraphic(trashIcon);

        selectionBox = new CheckBox(" ");
        borderPane = new BorderPane();
        controlsContainer = new HBox();
        controlsContainer.getChildren().add(selectionBox);
        HBox.setMargin(selectionBox, new Insets(5,0,0,0));
        controlsContainer.getChildren().add(removeButton);

        borderPane.setLeft(label);
        borderPane.setRight(controlsContainer);

        BorderPane.setMargin(label, new Insets(0,5,0,0));

        Platform.runLater(() -> {
            log.debug("Video playing.");
            embeddedMediaPlayer.media().play(mp4Path);
        });
        this.setSpacing(5);
        this.getChildren().add(pane);
        this.getChildren().add(borderPane);
    }

    public void releasePlayer() {
        this.mediaPlayerFactory.release();
        this.embeddedMediaPlayer.release();
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
