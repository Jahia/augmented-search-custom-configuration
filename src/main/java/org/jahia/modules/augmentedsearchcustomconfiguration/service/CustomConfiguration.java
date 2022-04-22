package org.jahia.modules.augmentedsearchcustomconfiguration.service;

import org.apache.commons.io.IOUtils;
import org.jahia.modules.augmentedsearch.settings.ESConfigService;
import org.jahia.modules.augmentedsearch.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
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
@Component(service = ESConfigService.class, property = { "service.ranking:Integer=100" }, immediate = true)
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
        logger.info("Overriding custom settings provider for Augmented Search");
    }

    @Override
    public JSONObject getMappings() {
        // use Augmented Search default mappings
        return null;
    }

    @Override
    public JSONObject getSettings() {
        logger.info("Merging Augmented Search custom content settings configuration...");
        try {
            final URL baseConf = FrameworkUtil.getBundle(Utils.class).getResource("META-INF/configurations/settings.json");
            final URL customConf = context.getBundle().getResource(CONFIG_LOCATION + "/settings-override.json");
            return mergeConfigurations(baseConf, customConf);
        } catch (JSONException | IOException jsonException) {
            logger.error("Failed to read settings file: {}", jsonException.getMessage());
            return null;
        }
    }

    @Override
    public JSONObject getFileMappings() {
        // use Augmented search default file mappings
        return null;
    }

    @Override
    public JSONObject getFileSettings() {
        logger.info("Merging Augmented Search custom file settings configuration...");
        try {
            final URL baseConf = FrameworkUtil.getBundle(Utils.class).getResource("META-INF/configurations/file-settings.json");
            final URL customConf = context.getBundle().getResource(CONFIG_LOCATION + "/file-settings-override.json");
            return mergeConfigurations(baseConf, customConf);
        } catch (JSONException | IOException jsonException) {
            logger.error("Failed to read settings file: {}", jsonException.getMessage());
            return null;
        }
    }

    private JSONObject mergeConfigurations(URL baseConf, URL customConf) throws JSONException, IOException {
        final JSONObject jsonBaseConf = baseConf == null ? null :
                new JSONObject(IOUtils.toString(new InputStreamReader(baseConf.openStream(), StandardCharsets.UTF_8)));
        final JSONObject jsonCustomConf = customConf == null ? null :
                new JSONObject(IOUtils.toString(new InputStreamReader(customConf.openStream(), StandardCharsets.UTF_8)));
        if (jsonBaseConf == null)
            return jsonCustomConf;
        return Utils.mergeJSONObjects(jsonBaseConf, jsonCustomConf);
    }

    @Override
    public JSONObject getSettingsForLanguage(String language) {
        // use Augmented search default settings
        return null;
    }
}
