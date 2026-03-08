package de.guntram.mcmod.easierchests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationHandler {

    private static ConfigurationHandler instance;

    private Properties config = new Properties();
    private File configFile;

    public static ConfigurationHandler getInstance() {
        if (instance == null)
            instance = new ConfigurationHandler();
        return instance;
    }

    private boolean extraLargeChests;
    private boolean halfSizeButtons;
    private boolean toneDownButtons;
    private boolean enableSearch;
    private String matchHighlightColor;

    public void load(File file) {
        if (file == null) return;
        this.configFile = file;
        config = new Properties();
        // Set defaults
        config.setProperty("easierchests.config.largechests",  "false");
        config.setProperty("easierchests.config.halfsize",      "false");
        config.setProperty("easierchests.config.transparent",   "true");
        config.setProperty("easierchests.config.enablesearch",  "true");
        config.setProperty("easierchests.config.highlight",     "4000ff00");

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                config.load(fis);
            } catch (IOException e) {
                // use defaults
            }
        }
        loadConfig();
        save();
    }

    private void loadConfig() {
        extraLargeChests    = Boolean.parseBoolean(config.getProperty("easierchests.config.largechests",  "false"));
        halfSizeButtons     = Boolean.parseBoolean(config.getProperty("easierchests.config.halfsize",      "false"));
        toneDownButtons     = Boolean.parseBoolean(config.getProperty("easierchests.config.transparent",   "true"));
        enableSearch        = Boolean.parseBoolean(config.getProperty("easierchests.config.enablesearch",  "true"));
        matchHighlightColor = config.getProperty("easierchests.config.highlight", "4000ff00");
    }

    public void save() {
        if (configFile == null) return;
        if (configFile.getParentFile() != null)
            configFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            config.store(fos, "EasierChests configuration");
        } catch (IOException e) {
            // ignore
        }
    }

    public static String getConfigFileName() {
        return getInstance().configFile != null ? getInstance().configFile.getPath() : "";
    }

    public static boolean allowExtraLargeChests() {
        return getInstance().extraLargeChests;
    }

    public static boolean toneDownButtons() {
        return getInstance().toneDownButtons;
    }

    public static boolean halfSizeButtons() {
        return getInstance().halfSizeButtons;
    }

    public static boolean enableSearch() {
        return getInstance().enableSearch;
    }

    public static void toggleSearchBox() {
        ConfigurationHandler inst = getInstance();
        inst.enableSearch = !inst.enableSearch;
        inst.config.setProperty("easierchests.config.enablesearch", Boolean.toString(inst.enableSearch));
        inst.save();
    }

    public static String getHighlightColor() {
        return getInstance().matchHighlightColor;
    }

    public static void setAllOptions(boolean extraLargeChests, boolean halfSizeButtons,
                                     boolean toneDownButtons, boolean enableSearch,
                                     String highlightColor) {
        ConfigurationHandler inst = getInstance();
        inst.extraLargeChests    = extraLargeChests;
        inst.halfSizeButtons     = halfSizeButtons;
        inst.toneDownButtons     = toneDownButtons;
        inst.enableSearch        = enableSearch;
        inst.matchHighlightColor = highlightColor;
        inst.config.setProperty("easierchests.config.largechests",  Boolean.toString(extraLargeChests));
        inst.config.setProperty("easierchests.config.halfsize",     Boolean.toString(halfSizeButtons));
        inst.config.setProperty("easierchests.config.transparent",  Boolean.toString(toneDownButtons));
        inst.config.setProperty("easierchests.config.enablesearch", Boolean.toString(enableSearch));
        inst.config.setProperty("easierchests.config.highlight",    highlightColor);
        inst.save();
    }
}