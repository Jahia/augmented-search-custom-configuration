package org.jahia.modules.augmentedsearchcustomconfiguration.service;

import org.apache.commons.io.IOUtils;
import org.jahia.modules.augmentedsearch.settings.ESConfigService;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Augment search configuration provider
 */
@Component(service = ESConfigService.class, immediate = true)
public class CustomConfiguration implements ESConfigService {
    private static final Logger logger = LoggerFactory.getLogger(CustomConfiguration.class);

    private static final String CONFIG_LOCATION = "META-INF/configurations";

    private BundleContext context;

    /**
     * Activates bundle
     *
     * @param context BundleContext
     */
    @Activate
    public void activate(BundleContext context) {
        this.context = context;
        logger.info("Activated custom settings provider for Augmented Search");
    }

    @Override
    public JSONObject getMappings() {
        URL configResourceURL = context.getBundle().getResource(CONFIG_LOCATION + "/mapping.json");
        if (configResourceURL != null) {
            try {
                return new JSONObject(IOUtils.toString(new InputStreamReader(configResourceURL.openStream(), StandardCharsets.UTF_8)));
            } catch (JSONException | IOException e) {
                logger.error("Failed to read mappings file: {}", e.getMessage());
            }
        }
        return null;
    }

    @Override
    public JSONObject getSettings() {
        URL configResourceURL = context.getBundle().getResource(CONFIG_LOCATION + "/settings.json");
        if (configResourceURL != null) {
            try {
                return new JSONObject(IOUtils.toString(new InputStreamReader(configResourceURL.openStream(), StandardCharsets.UTF_8)));
            } catch (JSONException | IOException e) {
                logger.error("Failed to read settings file: {}", e.getMessage());
            }
        }
        return null;
    }

    @Override
    public JSONObject getSettingsForLanguage(String language) {
        URL configResourceURL = context.getBundle().getResource(CONFIG_LOCATION + "/settings_" + language + ".json" );
        if (configResourceURL != null) {
            try {
                return new JSONObject(IOUtils.toString(new InputStreamReader(configResourceURL.openStream(), StandardCharsets.UTF_8)));
            } catch (JSONException | IOException e) {
                logger.error("Failed to read settings file: {}", e.getMessage());
            }
        }
        return null;
    }
}
