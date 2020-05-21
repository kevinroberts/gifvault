package com.vinberts.gifvault.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static com.vinberts.gifvault.utils.AppConstants.GIF_VAULT_FOLDER_LOC_PROP;
import static com.vinberts.gifvault.utils.AppConstants.GIPHY_API_KEY_PROP;

/**
 *
 */
@Slf4j
public class SystemPropertyLoader {

    public static void loadPropValues() {
        InputStream inputStream = null;
        try {
            final Properties prop = new Properties();
            final String propFileName = "config.properties";

            inputStream = SystemPropertyLoader.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }


            // get the property value and print it out
            final String giphyApiKey = prop.getProperty(GIPHY_API_KEY_PROP);
            final String gifVaultFolderLoc = prop.getProperty(GIF_VAULT_FOLDER_LOC_PROP);

            for (String name : prop.stringPropertyNames()) {
                String value = prop.getProperty(name);
                System.setProperty(name, value);
            }

            if (Objects.isNull(giphyApiKey) || giphyApiKey.equals("")) {
                log.error( GIPHY_API_KEY_PROP + " is not set - please set one in system properties");
                System.exit(1);
            }
            if (StringUtils.isEmpty(gifVaultFolderLoc)) {
                log.error( GIF_VAULT_FOLDER_LOC_PROP + " is not set - please set one in system properties");
                System.exit(1);
            }

        } catch (Exception e) {
            log.error("Exception: " + e);
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("IOException reached", e);
                }
            }
        }
    }

}
