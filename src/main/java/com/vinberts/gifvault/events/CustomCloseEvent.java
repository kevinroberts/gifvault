package com.vinberts.gifvault.events;

import com.vinberts.gifvault.controllers.vault.VaultController;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class CustomCloseEvent implements EventHandler<WindowEvent> {

    VaultController vaultController;

    @Override
    public void handle(final WindowEvent event) {
        log.debug("Custom close event, Refreshing list of folders");
        // refresh list of folders
        vaultController.refreshFolders();

    }

    public VaultController getVaultController() {
        return vaultController;
    }

    public void setVaultController(final VaultController vaultController) {
        this.vaultController = vaultController;
    }
}
