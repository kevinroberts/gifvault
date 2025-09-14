package com.vinberts.gifvault.views;

import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.HyperlinkLabel;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import static com.vinberts.gifvault.utils.AppConstants.*;

/**
 *
 */
public class GiphyCell extends VBox {

    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer embeddedMediaPlayer;
    private ImageView videoImageView;
    private Pane pane;
    private BorderPane borderPane;
    private HyperlinkLabel label;
    private Button faveButton;
    private GiphyData giphyData;
    private final int maxTitleLen = 45;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GiphyCell.class);

    public GiphyCell(final GiphyData gifSource, boolean isFaved) {
        super();
        this.giphyData = gifSource;
        this.mediaPlayerFactory = new MediaPlayerFactory();
        this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        this.embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(final uk.co.caprica.vlcj.player.base.MediaPlayer mediaPlayer) {
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
        embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.videoImageView));
        videoImageView.setFitWidth(300);
        videoImageView.setFitHeight(200);

        pane = new Pane();
        pane.getChildren().add(videoImageView);
        pane.setMaxWidth(300);
        pane.setMaxHeight(250);
        // set background color of video player to black
        pane.setStyle("-fx-background-color: black;");

        this.label = new HyperlinkLabel(String.format("[%s]", getTitleFromGif(gifSource)));
        this.label.setPickOnBounds(false);
        this.label.setAccessibleText(gifSource.getBitlyUrl());

        this.faveButton = new Button();
        if (isFaved) {
            Text heartIcon =
                    FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.HEART, "10pt");
            faveButton.setGraphic(heartIcon);
            faveButton.getStyleClass().add(HEARTED_CLASS);
            faveButton.setAccessibleText(FAVED_TEXT);
        } else {
            Text icon =
                    FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.HEART_ALT, "10pt");
            faveButton.setGraphic(icon);
            faveButton.setAccessibleText(UNFAVED_TEXT);
        }
        if (giphyData.getId().equals("TEST")) {
            faveButton.setDisable(true);
        }

        borderPane = new BorderPane();
        borderPane.setLeft(label);
        borderPane.setRight(faveButton);
        BorderPane.setMargin(label, new Insets(0,5,0,0));

        Platform.runLater(() -> {
            log.info("Video playing.");
            embeddedMediaPlayer.media().play(gifSource.getImages().getFixedHeight().getMp4());
        });
        this.setSpacing(5);
        this.getChildren().add(pane);
        this.getChildren().add(borderPane);
    }

    private String getTitleFromGif(GiphyData giphyData) {
        String title = giphyData.getTitle().replaceAll("\\b(\\w*GIF\\w*)\\b", "");
        if (!title.isEmpty()) {
            return StringUtils.abbreviate(title, maxTitleLen);
        } else {
            if (StringUtils.isNotEmpty(giphyData.getUsername())) {
                return StringUtils.abbreviate("Gif By " + giphyData.getUsername(), maxTitleLen);
            } else {
                return "Gif Link";
            }
        }
    }

    public void releasePlayer() {
        this.mediaPlayerFactory.release();
        this.embeddedMediaPlayer.release();
    }

    public String getAnchorLink() {
        return this.label.getAccessibleText();
    }

    public GiphyData getGiphyData() {
        return giphyData;
    }
}
