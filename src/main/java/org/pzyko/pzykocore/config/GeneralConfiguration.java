package org.pzyko.pzykocore.config;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

import java.util.Date;

public interface GeneralConfiguration extends ConfigurationSection, Configuration {

    float getFloat(String path);

    Location getLocation(String path, String worldName);

    Location getLocation(String path);

    void setLocation(String path, Location value);

    Inventory getInventory(String path);

    void setInventory(String path, Inventory inv);

    void setDate(String path, Date date);

    Date getDate(String path);
}
