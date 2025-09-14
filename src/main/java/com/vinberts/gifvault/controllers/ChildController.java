package com.vinberts.gifvault.controllers;

import com.vinberts.gifvault.controllers.main.MainAppController;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public abstract class ChildController implements Initializable {
    private MainAppController mainAppController;
    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChildController.class);

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
