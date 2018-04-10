package org.pzyko.pzykocore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.pzyko.pzykocore.config.Configuration;
import org.pzyko.pzykocore.config.ConfigurationManager;
import org.pzyko.pzykocore.config.PlayerConfigurationManager;
import org.pzyko.pzykocore.mysql.MySQLConnectionPool;

import java.sql.Connection;

public class PzykoCore extends JavaPlugin {

    public static final String CONFIG_MAIN = "config";

    private static PzykoCore instance;

    public static PzykoCore get() {
        return instance;
    }

    private ConfigurationManager configurationManager;
    private PlayerConfigurationManager playerConfigurationManager;
    private MySQLConnectionPool sqlPool;

    public void onEnable() {
        PzykoCore.instance = this;

        this.configurationManager = new ConfigurationManager(this);
        this.playerConfigurationManager = new PlayerConfigurationManager(this);

        Configuration config = getConfigManager().getConfiguration(CONFIG_MAIN);

        if (!config.isSet("MySQL.url")) config.set("MySQL.url", "jdbc:mysql://localhost:3306/minecraft");

        if (!config.isSet("MySQL.user")) config.set("MySQL.user", "root");

        if (!config.isSet("MySQL.pass")) config.set("MySQL.pass", "root");

        config.forceSave();

        this.sqlPool = new MySQLConnectionPool(config.getString("MySQL.url"), config.getString("MySQL.user"), config.getString("MySQL.pass"));

        try {
            Connection conn = getSqlConnectionPool().getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR");
                System.out.println("Kunne ikke koble til databasen!!!!");
                System.out.println("Stopper serveren for å forhindre skade....!");
                Bukkit.shutdown();
                return;
            }
        } catch (Exception e) {
            System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR");
            System.out.println("Kunne ikke koble til databasen!!!!");
            e.printStackTrace();
            System.out.println("Stopper serveren for å forhindre skade....!");
            Bukkit.shutdown();
            return;
        }

        PzykoCommands.load();
    }

    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        PzykoCommands.onCmd(sender, command, label, args);
        return true;
    }


    public ConfigurationManager getConfigManager() {
        return configurationManager;
    }

    public PlayerConfigurationManager getPlayerConfigManager() {
        return playerConfigurationManager;
    }

    public MySQLConnectionPool getSqlConnectionPool() {
        return sqlPool;
    }

}
