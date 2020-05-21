package com.vinberts.gifvault.controllers.about;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class AboutController implements Initializable {

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    private void loadWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
            handleWebpageLoadException(url);
        }
    }

    private void handleWebpageLoadException(String url) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load(url);
        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(browser));
        stage.setScene(scene);
        stage.setTitle("GifVault Explorer");
        stage.show();
    }

    public void loadPersonalSite(final ActionEvent actionEvent) {
        loadWebpage("https://kevinroberts.us");
    }

    public void loadGithubPage(final ActionEvent actionEvent) {
        loadWebpage("https://github.com/kevinroberts");
    }
}
