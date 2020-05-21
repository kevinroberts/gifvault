package com.vinberts.gifvault.controllers;

import com.vinberts.gifvault.controllers.main.MainAppController;
import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
@Slf4j
public abstract class ChildController implements Initializable {
    private MainAppController mainAppController;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public void setMainAppController(final MainAppController mainAppController) {
        this.mainAppController = mainAppController;
    }

    public MainAppController getMainAppController() {
        return mainAppController;
    }
}
