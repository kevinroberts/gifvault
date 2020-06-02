package com.vinberts.gifvault.controllers.main;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTabPane;
import com.vinberts.gifvault.controllers.gihpy.GiphyController;
import com.vinberts.gifvault.controllers.vault.VaultController;
import com.vinberts.gifvault.utils.AppUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.NotificationPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
@Slf4j
public class MainAppController {

    private NotificationPane notificationsPane;

    @FXML
    private JFXSpinner spinner;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    public MenuItem exitMenuItem;

    @FXML
    private MenuItem userGuideMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuBar mainMenuBar;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    private Tab giphyTab;

    @FXML
    private Tab vaultTab;

    private final double tabWidth = 90.0;
    public static int lastSelectedTabIndex = 0;
    private Map<String, Object> tabControllerMap = new HashMap<String, Object>();

    public void initialize() {
        //init menu
        initMenu();
        tabPane.setTabMinWidth(tabWidth);
        tabPane.setTabMaxWidth(tabWidth);
        tabPane.setTabMinHeight(tabWidth);
        tabPane.setTabMaxHeight(tabWidth);
        tabPane.setRotateGraphic(true);

        tabPane.setSide(Side.LEFT);

        EventHandler<Event> onTabSelectionChange = event -> {
            lastSelectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();

            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                currentTab.setStyle("-fx-background-color: -fx-focus-color;");
            } else {
                currentTab.setStyle("-fx-background-color: -fx-accent;");
            }
        };

        tabPane.getSelectionModel().clearSelection();

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String tabSelected = newValue.getId();
            String prevTabSelected = Objects.nonNull(oldValue) ? oldValue.getId() : "";
            log.info("Tab selected: " + tabSelected);
            if (newValue.getContent() == null) {
                switch (tabSelected) {
                    case "giphyTab":
                        log.debug("loading giphy tab");
                        loadTab(newValue,
                                MainAppController.class.getResource("/ui/giphyPane.fxml"));
                        break;
                    case "vaultTab":
                        log.debug("loading vault tab");
                        loadTab(newValue,
                                MainAppController.class.getResource("/ui/vaultPane.fxml"));
                        if (prevTabSelected.equals("giphyTab")) {
                            log.info("Prev tab was Giphy Tab, releasing all its resources");
                            //GiphyController giphyController = (GiphyController) tabControllerMap.get(prevTabSelected);
                            //giphyController.releaseAllPlayers();
                        }
                        break;
                    default:
                        log.error("invalid tab selected: " + tabSelected);
                        break;
                }
            } else {
                // Content is already loaded. Update the tab if necessary.
                // could unload videos from other tabs
                // Parent root = (Parent) newValue.getContent();
                switch (tabSelected) {
                    case "vaultTab":
                        log.info("vault tab selected - already loaded state");
                        // VaultController vaultController = (VaultController) tabControllerMap.get(tabSelected);
                        // reload current filter
                        // vaultController.doFilterAction(null);
                        break;
                    default:
                        break;
                }
            }
        });
        // By default, select 1st tab and load its content.
        tabPane.getSelectionModel().selectFirst();

        configureTabWithImage(giphyTab, "Giphy", "giphy-logo.png", onTabSelectionChange);
        configureTabWithImage(vaultTab, "Vault", "lock.png", onTabSelectionChange);

        giphyTab.setStyle("-fx-background-color: -fx-focus-color;");

    }

    private void loadTab(Tab newTab, URL resourceURL) {
        try {
            Parent contentView;
            FXMLLoader loader = new FXMLLoader(resourceURL);
            if (resourceURL.toString().endsWith("giphyPane.fxml")) {
                loader.setControllerFactory((type) -> {
                    GiphyController controller = new GiphyController();
                    controller.setMainAppController(this);
                    return controller;
                });
            }
            if (resourceURL.toString().endsWith("vaultPane.fxml")) {
                loader.setControllerFactory((type) -> {
                    VaultController controller = new VaultController();
                    controller.setMainAppController(this);
                    return controller;
                });
            }
            contentView = loader.load();
            tabControllerMap.put(newTab.getId(), loader.getController());
            // Loading content on demand
            newTab.setContent(contentView);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initMenu() {
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        aboutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        aboutMenuItem.setOnAction(event -> {
            AppUtils.loadWindow(MainAppController.class.getResource("/ui/about.fxml"), "About GifVault", null);
        });
        settingsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN));
        settingsMenuItem.setOnAction(event -> {
            AppUtils.loadWindow(MainAppController.class.getResource("/ui/settings.fxml"), "GifVault Settings", null);
        });
    }

    private void configureTabWithImage(Tab tab, String title, String iconPath, EventHandler<Event> onSelectionChangedEvent) {
        double imageWidth = 20.0;

        ImageView imageView = new ImageView(new Image(iconPath));
        imageView.setFitHeight(imageWidth);
        imageView.setFitWidth(imageWidth);

        Label label = new Label(title);
        label.setMaxWidth(tabWidth - 20);
        label.setPadding(new Insets(5, 0, 0, 0));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 9pt; -fx-font-weight: normal;");
        label.setTextAlignment(TextAlignment.CENTER);

        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setMaxWidth(tabWidth);
        tabPane.setCenter(imageView);
        tabPane.setBottom(label);

        tab.setText("");
        tab.setGraphic(tabPane);

        tab.setOnSelectionChanged(onSelectionChangedEvent);
    }

    public void setNotificationsPane(final NotificationPane notificationsPane) {
        this.notificationsPane = notificationsPane;
    }

    public void showSpinner() {
        spinner.setOpacity(1);
    }

    public void hideSpinner() {
        spinner.setOpacity(0);
    }

    public Tab getVaultTab() {
        return vaultTab;
    }

    public Tab getGiphyTab() {
        return giphyTab;
    }

    public NotificationPane getNotificationsPane() {
        return notificationsPane;
    }
}
