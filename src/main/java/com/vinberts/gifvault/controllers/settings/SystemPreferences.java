package com.vinberts.gifvault.controllers.settings;

import com.google.gson.Gson;
import com.vinberts.gifvault.utils.AlertMaker;
import com.vinberts.gifvault.utils.SystemPropertyLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.vinberts.gifvault.utils.AppConstants.GIF_VAULT_FOLDER_LOC_PROP;
import static com.vinberts.gifvault.utils.AppConstants.GIPHY_API_KEY_PROP;

/**
 *
 */
@Slf4j
public class SystemPreferences {
    private static final String CONFIG_FILE = "config.json";
    private String gifVaultFolderLocation;
    private String giphyAPIKey;

    public SystemPreferences() {
        SystemPropertyLoader.loadPropValues();
        Path vaultFolderPath = FileSystems.getDefault().getPath(System.getProperty(GIF_VAULT_FOLDER_LOC_PROP));
        if (Files.notExists(vaultFolderPath)) {
            log.info("The specified gif vault location does not exist, creating base directory in User home directory /gifvault");
            Path fallbackVaultPath = FileSystems.getDefault().getPath(FileUtils.getUserDirectoryPath() + File.separator + "gifvault");
            if (Files.notExists(fallbackVaultPath)) {
                try {
                    Files.createDirectory(fallbackVaultPath);
                    System.setProperty(GIF_VAULT_FOLDER_LOC_PROP, fallbackVaultPath.toString());
                } catch (IOException e) {
                    log.error("Could not create Gif Vault folder location at " + fallbackVaultPath.toString(), e);
                    System.exit(1);
                }
            }

        }
        setGifVaultFolderLocation(System.getProperty(GIF_VAULT_FOLDER_LOC_PROP));
        setGiphyAPIKey(System.getProperty(GIPHY_API_KEY_PROP));
    }

    public static void initConfig() {
        Writer writer = null;
        try {
            SystemPreferences preference = new SystemPreferences();
            Gson gson = new Gson();
            Optional<String> folder = Optional.ofNullable(preference.getGifVaultFolderLocation());
            writer = new FileWriter(folder.orElse("") + File.separator + CONFIG_FILE);
            gson.toJson(preference, writer);
        } catch (IOException ex) {
            log.error("Could not load system preferences");
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                log.error("could not close system preferences");
            }
        }
    }

    public static SystemPreferences getPreferences() {
        Gson gson = new Gson();
        SystemPreferences preferences = new SystemPreferences();
        try {
            preferences = gson.fromJson(new FileReader(preferences.getGifVaultFolderLocation() + File.separator + CONFIG_FILE), SystemPreferences.class);
            String giphyKeySystem = System.getProperty(GIPHY_API_KEY_PROP);
            String gifVaultPathSystem = System.getProperty(GIF_VAULT_FOLDER_LOC_PROP);
            if (!StringUtils.equals(giphyKeySystem, preferences.getGiphyAPIKey())) {
                log.info("Configuration giphy key does not match system key, updating value");
                System.setProperty(GIPHY_API_KEY_PROP, preferences.getGiphyAPIKey());
            }
            if (!StringUtils.equals(gifVaultPathSystem, preferences.getGifVaultFolderLocation())) {
                System.setProperty(GIF_VAULT_FOLDER_LOC_PROP, preferences.getGifVaultFolderLocation());
            }
        } catch (FileNotFoundException ex) {
            log.info("SystemPreferences Config file is missing. Creating new one with default config");
            initConfig();
        }
        return preferences;
    }

    public static void writePreferenceToFile(SystemPreferences preference) {
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(preference.getGifVaultFolderLocation() + File.separator + CONFIG_FILE);
            gson.toJson(preference, writer);
            System.setProperty(GIPHY_API_KEY_PROP, preference.getGiphyAPIKey());
            System.setProperty(GIF_VAULT_FOLDER_LOC_PROP, preference.getGifVaultFolderLocation());

            AlertMaker.showSimpleAlert("Success", "Settings updated");
        } catch (IOException ex) {
            AlertMaker.showErrorMessage(ex, "Failed", "Cant save configuration file");
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                log.error("IoException: ", ex);
            }
        }
    }

    public void setGifVaultFolderLocation(final String gifVaultFolderLocation) {
        this.gifVaultFolderLocation = gifVaultFolderLocation;
    }

    public void setGiphyAPIKey(final String giphyAPIKey) {
        this.giphyAPIKey = giphyAPIKey;
    }

    public String getGifVaultFolderLocation() {
        return gifVaultFolderLocation;
    }

    public String getGiphyAPIKey() {
        return giphyAPIKey;
    }
}
