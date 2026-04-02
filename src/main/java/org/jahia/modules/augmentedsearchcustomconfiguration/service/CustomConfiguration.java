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
import java.util.List;

/**
 * Provides custom mappings and settings for Augmented Search indexation
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
        return getConfigResource("/mapping.json");
    }

    @Override
    public JSONObject getSettings() {
        return getConfigResource("/settings.json");
    }

    @Override
    public JSONObject getFileMappings() {
        // Default file mappings will be used when returning null
        return null;
    }

    @Override
    public JSONObject getFileSettings() {
        // Default file settings will be used when returning null
        return null;
    }

    @Override
    public JSONObject getSettingsForLanguage(String language) {
        // Return en to reproduce issue in AS 4.0.0 - See https://github.com/Jahia/jahia-private/issues/4856
        return (List.of("en", "ja", "pl").contains(language)) ?
                getConfigResource(String.format("/settings_%s.json", language)) : null;
    }

    protected final JSONObject getConfigResource(String resourcePath) {
        URL configResourceURL = context.getBundle().getResource(CONFIG_LOCATION + resourcePath);
        if (configResourceURL != null) {
            try {
                return new JSONObject(IOUtils.toString(new InputStreamReader(configResourceURL.openStream(), StandardCharsets.UTF_8)));
            } catch (JSONException | IOException e) {
                logger.error("Failed to read custom configuration file for resourcePath {}: {}",
                        CONFIG_LOCATION + resourcePath, e.getMessage());
            }
        }
        return null;
    }
}
