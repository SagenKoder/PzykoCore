package org.pzyko.pzykocore.config;

import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FilePlayerConfiguration extends FileGeneralConfiguration implements PlayerConfiguration {

    PlayerConfigurationManager playerConfigurationManager;

    private final Object saveLock = new Object();
    private final UUID playerUUID;
    private File file = null;

    FilePlayerConfiguration(PlayerConfigurationManager playerConfigurationManager, OfflinePlayer p) {
        this(playerConfigurationManager, p.getUniqueId());
    }

    FilePlayerConfiguration(PlayerConfigurationManager playerConfigurationManager, final UUID u) {
        super();
        this.playerConfigurationManager = playerConfigurationManager;
        final File dataFolder = playerConfigurationManager.plugin.getDataFolder();
        this.file = new File(dataFolder + File.separator + "userdata" + File.separator + u + ".yml");
        try {
            this.load(this.file);
        } catch (final Exception ignored) {
        }
        this.playerUUID = u;
    }

    @SuppressWarnings("unused")
    private FilePlayerConfiguration() {
        this.playerUUID = null;
    }

    @Override
    public boolean createFile() {
        try {
            return this.file.createNewFile();
        } catch (final IOException ignored) {
            return false;
        }
    }

    @Override
    public void discard() {
        this.discard(false);
    }

    @Override
    public void discard(final boolean save) {
        if (save) this.forceSave();
        playerConfigurationManager.discard(this);
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public void forceSave() {
        synchronized (this.saveLock) {
            try {
                this.save(this.file);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public UUID getManagerPlayerUUID() {
        return this.playerUUID;
    }

    @Override
    public boolean isFirstJoin() {
        return this.getBoolean("first_join", true);
    }

    @Override
    public void setFirstJoin(boolean firstJoin) {
        this.set("first_join", firstJoin);
    }

    @Override
    public String toString() {
        return String.format("PConfManager@%s[playerUUID=%s, file=%s]", this.hashCode(), this.playerUUID, this.file);
    }
}