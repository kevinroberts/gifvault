package com.vinberts.gifvault.utils;

import com.roxstudio.utils.CUrl;
import com.trievosoftware.giphy4j.entity.giphy.GiphyContainer;
import com.trievosoftware.giphy4j.entity.giphy.GiphyData;
import com.trievosoftware.giphy4j.entity.giphy.GiphyImage;
import com.vinberts.gifvault.controllers.settings.SystemPreferences;
import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.data.GifVault;
import com.vinberts.gifvault.events.FavoritedEvent;
import com.vinberts.gifvault.views.GiphyCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@Slf4j
public class AppUtils {
    public static final String ICON_IMAGE_LOC = "lock.png";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");

    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(ICON_IMAGE_LOC));
    }

    public static String formatDateTimeString(Long time) {
        return DATE_TIME_FORMAT.format(new Date(time));
    }

    public static URL loadFXMLResource(String name) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL fxmlUrl = classLoader.getResource(name);
        if (Objects.nonNull(fxmlUrl)) {
            return fxmlUrl;
        } else {
            log.error("Could not load fxml file " + name);
            throw new IOException("Could not find fxml file");
        }
    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                log.error("Timeout error", e);
            }
        }).start();
    }

    public static Object loadWindow(URL loc, String title, Stage parentStage) {
        return loadWindow(loc, title, parentStage, null);
    }

    public static Object loadWindow(URL loc, String title, Stage parentStage, EventHandler customEvent) {
        Object controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(loc);
            Parent parent = loader.load();
            controller = loader.getController();
            Stage stage = null;
            if (parentStage != null) {
                stage = parentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            if (Objects.nonNull(customEvent)) {
                stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, customEvent);
            }
            stage.show();
            setStageIcon(stage);
        } catch (IOException ex) {
            log.error("ioexception occurred trying to load window, ", ex);
        }
        return controller;
    }

    public static void removeAssetsFromFileSystem(GifVault gifVault) {
        SystemPreferences preferences = SystemPreferences.getPreferences();
        Path storagePath = FileSystems.getDefault().getPath(preferences.getGifVaultFolderLocation() +
                        File.separator + gifVault.getStorageLocation());
        if (Files.exists(storagePath)) {
            File storageFolder = storagePath.toFile();
            File parentFolder = storageFolder.getParentFile();
            try {
                FileUtils.deleteDirectory(storageFolder);
                // remove parent folder too if it is empty
                if (Objects.nonNull(parentFolder) && parentFolder.isDirectory()) {
                    String[] entries = parentFolder.list();
                    if (Objects.nonNull(entries) && entries.length == 1) {
                        for (String s: entries) {
                            if (s.equals(".DS_Store")) {
                                FileUtils.deleteDirectory(parentFolder);
                            }
                        }
                    } else if (Objects.nonNull(entries) && entries.length == 0) {
                        FileUtils.deleteDirectory(parentFolder);
                    }
                }
            } catch (IOException e) {
                log.error("Exception occurred trying to delete favorite gifs from filesystem", e);
            }
        }
    }

    public static GifVault prepareNewVaultEntryForGif(Optional<GifFolder> folder, GiphyCell giphyCell) {
        GifVault gifVault = new GifVault();
        gifVault.setId(UUID.randomUUID().toString());
        gifVault.setCreatedAt(new Date());

        // create folder structure
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String month = new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());
        String day = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
        String folderContainer = giphyCell.getGiphyData().getSlug();

        gifVault.setStorageLocation(year + File.separator + month + File.separator + day + File.separator + folderContainer);
        gifVault.setMp4Filename(giphyCell.getGiphyData().getSlug() + ".mp4");
        gifVault.setGifFilename(giphyCell.getGiphyData().getSlug() + ".gif");
        if (StringUtils.isNotEmpty(giphyCell.getGiphyData().getTitle())) {
            gifVault.setTitle(giphyCell.getGiphyData().getTitle());
        } else {
            gifVault.setTitle("Gif By " + giphyCell.getGiphyData().getUsername());
        }
        if (folder.isPresent()) {
            gifVault.setFolder(folder.get());
        }
        return gifVault;
    }

    public static void createFaveFolderAndDownloadAsset(GifVault gifVault, String mp4URL, String gifUrl, Tab tab, FavoritedEvent event) throws IOException {
        SystemPreferences preferences = SystemPreferences.getPreferences();

        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String month = new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());
        String day = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
        String folderContainer = StringUtils.substringAfterLast(gifVault.getStorageLocation(), day + File.separator);

        // check if folders exist / create if not
        Path yearPath = FileSystems.getDefault().getPath(preferences.getGifVaultFolderLocation() + File.separator + year);
        if (Files.notExists(yearPath)) {
            Files.createDirectory(yearPath);
        }
        Path monthPath = FileSystems.getDefault().getPath(preferences.getGifVaultFolderLocation()
                + File.separator + year + File.separator + month);
        if (Files.notExists(monthPath)) {
            Files.createDirectory(monthPath);
        }
        Path dayPath = FileSystems.getDefault().getPath(preferences.getGifVaultFolderLocation()
                + File.separator + year + File.separator + month + File.separator + day);
        if (Files.notExists(dayPath)) {
            Files.createDirectory(dayPath);
        }
        Path containerPath = FileSystems.getDefault().getPath(preferences.getGifVaultFolderLocation()
                + File.separator + year + File.separator + month + File.separator + day + File.separator + folderContainer);
        if (Files.notExists(containerPath)) {
            Files.createDirectory(containerPath);
        }
        new Thread(() -> {
            String mp4FilePath = containerPath.toString() + File.separator + gifVault.getMp4Filename();
            CUrl cUrl = new CUrl(mp4URL).output(mp4FilePath).timeout(10, 130);
            cUrl.exec();
            String gifFilePath = containerPath.toString() + File.separator + gifVault.getGifFilename();
            CUrl cUrl2 = new CUrl(gifUrl).output(gifFilePath).timeout(10, 130);
            cUrl2.exec();
            if (Objects.nonNull(tab.getContent())) {
                Platform.runLater(()-> {
                    tab.getContent().fireEvent(event);
                });
            }
            log.debug("File " + mp4FilePath + " downloaded");
        }).start();
    }

    public static ObservableList<GiphyCell> getDummyListOfGiphyVideos() {
        ObservableList<GiphyCell> list = FXCollections.observableArrayList();
        for (int i = 1; i <= 10; i++) {
            GiphyData giphyData = new GiphyData();
            giphyData.setTitle("Placeholder Gif " + i);
            giphyData.setId("TEST");
            giphyData.setBitlyUrl("https://www.giphy.com");
            GiphyImage image = new GiphyImage();
            image.setMp4(AppUtils.class.getResource("/sampleVideo.mp4").getPath());
            GiphyContainer giphyContainer = new GiphyContainer();
            giphyContainer.setFixedHeight(image);

            giphyData.setImages(giphyContainer);
            list.add(new GiphyCell(giphyData, false));
        }
        return list;
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}
