package org.pzyko.pzykocore.config;

import java.io.File;
import java.io.IOException;

public class Configuration extends FileGeneralConfiguration {

    private final Object saveLock = new Object();

    ConfigurationManager configurationManager;

    final String path;
    final String name;
    File pconfl = null;

    Configuration(ConfigurationManager configurationManager, String filename) {
        super();
        this.configurationManager = configurationManager;
        final File dataFolder = configurationManager.plugin.getDataFolder();
        this.path = dataFolder + File.separator + filename + ".yml";
        this.pconfl = new File(this.path);
        try {
            this.load(this.pconfl);
        } catch (Exception ignored) {
        }
        this.name = filename;
    }

    Configuration(ConfigurationManager configurationManager, File file) {
        this(configurationManager, file.getName());
    }

    private Configuration() {
        this.path = "";
        this.name = "";
    }

    public boolean createFile() {
        try {
            return this.pconfl.createNewFile();
        } catch (IOException ignored) {
            return false;
        }
    }

    public void discard() {
        this.discard(false);
    }

    public void discard(boolean save) {
        configurationManager.discardConfiguration(this, save);
    }

    public boolean exists() {
        return this.pconfl.exists();
    }

    public void forceSave() {
        synchronized (this.saveLock) {
            try {
                System.out.println("Saving configuration " + path);
                this.save(this.pconfl);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void reload() {
        forceSave();
        try {
            this.load(this.pconfl);
        } catch (Exception ignored) {
        }
    }

}
