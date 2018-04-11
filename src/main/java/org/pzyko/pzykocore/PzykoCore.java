package org.pzyko.pzykocore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.pzyko.pzykocore.config.Configuration;
import org.pzyko.pzykocore.config.ConfigurationManager;
import org.pzyko.pzykocore.config.PlayerConfigurationManager;
import org.pzyko.pzykocore.listener.PlayerListener;
import org.pzyko.pzykocore.mysql.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class PzykoCore extends JavaPlugin {

    public static final String CONFIG_MAIN = "config";

    private static PzykoCore instance;

    public static PzykoCore get() {
        return instance;
    }

    private ConfigurationManager configurationManager;
    private PlayerConfigurationManager playerConfigurationManager;
    private SQL sqlPool;
    private PlayerManager playerManager;

    public void onEnable() {
        PzykoCore.instance = this;

        this.configurationManager = new ConfigurationManager(this);
        this.playerConfigurationManager = new PlayerConfigurationManager(this);

        Configuration config = getConfigManager().getConfiguration(CONFIG_MAIN);

        if (!config.isSet("MySQL.url")) config.set("MySQL.url", "jdbc:mysql://localhost:3306/minecraft");
        if (!config.isSet("MySQL.user")) config.set("MySQL.user", "root");
        if (!config.isSet("MySQL.pass")) config.set("MySQL.pass", "root");
        if (!config.isSet("MySQL.prefix")) config.set("MySQL.prefix", "pzyko_");
        config.forceSave();

        SQL.PREFIX = config.getString("MySQL.prefix");

        SQL.SQL_CREATE_USER = SQL.SQL_CREATE_USER.replace("pzyko_", SQL.PREFIX);
        SQL.SQL_CREATE_CLAIM = SQL.SQL_CREATE_CLAIM.replace("pzyko_", SQL.PREFIX);
        SQL.SQL_CREATE_CLAIM_USER_ROLE = SQL.SQL_CREATE_CLAIM_USER_ROLE.replace("pzyko_", SQL.PREFIX);
        SQL.SQL_CREATE_CLAIM_FLAG = SQL.SQL_CREATE_CLAIM_FLAG.replace("pzyko_", SQL.PREFIX);

        SQL.SQL_REPLACE_INTO_USER = SQL.SQL_REPLACE_INTO_USER.replace("pzyko_", SQL.PREFIX);

        this.sqlPool = new SQL(config.getString("MySQL.url"), config.getString("MySQL.user"), config.getString("MySQL.pass"));

        try (Connection conn = getSql().getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR");
                System.out.println("Could not connect to the database!!!!");
                System.out.println("Disabling plugin.....!");
                getPluginLoader().disablePlugin(this);
                return;
            }
        } catch (Exception e) {
            System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR");
            System.out.println("Could not connect to the database!!!!");
            e.printStackTrace();
            System.out.println("Disabling plugin.....!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        List<String> tables = getSql().getTables();
        if(tables.size() > 0) {
            System.out.println("Found database with " +  tables.size() + " tables");
            for (String s : tables) {
                System.out.println("- " + s);
            }
            System.out.println("*****************************************");
        } else {
            System.out.println("Found empty database....");
        }

        if(!tables.contains(SQL.PREFIX + "user") ||
                !tables.contains(SQL.PREFIX + "claim") ||
                !tables.contains(SQL.PREFIX + "claim_role_permission") ||
                !tables.contains(SQL.PREFIX + "claim_user_role") ||
                !tables.contains(SQL.PREFIX + "claim_flag")) {

            System.out.println("Missing one or more, creating...");

            try (Connection conn = getSql().getConnection();
                 PreparedStatement ps = conn.prepareStatement("SET foreign_key_checks = 0;")) {
                conn.setAutoCommit(false);
                ps.addBatch(SQL.SQL_CREATE_USER);
                ps.addBatch(SQL.SQL_CREATE_CLAIM);
                ps.addBatch(SQL.SQL_CREATE_CLAIM_USER_ROLE);
                ps.addBatch(SQL.SQL_CREATE_CLAIM_FLAG);
                ps.addBatch("SET foreign_key_checks = 1;");
                ps.executeBatch();
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR ERROR ERROR ERROR ERROR ERROR");
                System.out.println("Could not create necesary tables!!!!");
                System.out.println("Disabling plugin....!");
                getPluginLoader().disablePlugin(this);
                return;
            }

            List<String> newTables = getSql().getTables();
            if(newTables.size() > 0) {
                System.out.println("Found database with " + newTables.size() + " tables after creation...");
                for (String s : newTables) {
                    System.out.println("- " + s);
                }
                System.out.println("*****************************************");
            }
        }

        this.playerManager = new PlayerManager();

        PzykoCommands.load();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public void onDisable() {
        try {
            getSql().close();
        } catch (Exception e){}; // ignore
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

    public SQL getSql() {
        return sqlPool;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
