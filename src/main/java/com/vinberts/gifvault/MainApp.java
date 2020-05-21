package com.vinberts.gifvault;

import com.vinberts.gifvault.controllers.main.MainAppController;
import com.vinberts.gifvault.data.GifFolder;
import com.vinberts.gifvault.database.DatabaseHelper;
import com.vinberts.gifvault.database.HibernateUtil;
import com.vinberts.gifvault.utils.AppUtils;
import com.vinberts.gifvault.utils.SystemPropertyLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 *
 */
@Slf4j
public class MainApp extends Application {

    private static final Long startTime = System.currentTimeMillis();

    @Override
    public void start(Stage primaryStage) throws IOException {
        SystemPropertyLoader.loadPropValues();

        GifFolder folder = DatabaseHelper.getUncategorizedFolder();
        log.info("Loaded uncategorized folder: " + folder.getName());

        try {
            URL iconURL = MainApp.class.getResource("/gifVaultDock.png");
            Image image = new ImageIcon(iconURL).getImage();
            Taskbar.getTaskbar().setIconImage(image);
        } catch (Exception e) {
            log.error("Could not set APP icon", e);
        }

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/ui/mainScene.fxml"));
        Parent root = loader.load();
        MainAppController appController = loader.getController();
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);

        primaryStage.setTitle("Gif Vault");
        AppUtils.setStageIcon(primaryStage);

        primaryStage.show();

        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

        appController.exitMenuItem.setOnAction(event -> {
            Window window = primaryStage
                    .getScene()
                    .getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });


    }

    private void closeWindowEvent(WindowEvent event) {
        log.info("Window close request ...");

//        if(true) {  // if the dataset has changed, alert the user with a popup
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.getButtonTypes().remove(ButtonType.OK);
//            alert.getButtonTypes().add(ButtonType.CANCEL);
//            alert.getButtonTypes().add(ButtonType.YES);
//            alert.setTitle("Quit application");
//            alert.setContentText(String.format("Close without saving?"));
//            alert.initOwner(mainStage.getOwner());
//            Optional<ButtonType> res = alert.showAndWait();
//
//            if (res.isPresent()) {
//                // if user cancels the alert then do not exit app
//                if(res.get().equals(ButtonType.CANCEL))
//                    event.consume();
//            }
//        }
        HibernateUtil.shutdown();
        Long exitTime = System.currentTimeMillis();
        Duration duration = Duration.of(exitTime - startTime, ChronoUnit.MILLIS);
        log.info("GifVault is closing on {} used for {} h:m:s", AppUtils.formatDateTimeString(exitTime), AppUtils.formatDuration(duration));
        System.exit(0);
    }

    public static void main(String[] args) {
        log.info("Starting main application on {}", AppUtils.formatDateTimeString(startTime));
        launch(args);
    }

}
