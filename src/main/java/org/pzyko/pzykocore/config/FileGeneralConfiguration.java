package org.pzyko.pzykocore.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class FileGeneralConfiguration extends YamlConfiguration implements GeneralConfiguration {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");

    @Override
    public float getFloat(final String path) {
        return (float) this.getDouble(path);
    }

    @Override
    public Location getLocation(final String path, final String worldName) {
        if (!this.isSet(path)) return null;
        final double x = this.getDouble(path + ".x");
        final double y = this.getDouble(path + ".y");
        final double z = this.getDouble(path + ".z");
        final float pitch = this.getFloat(path + ".pitch");
        final float yaw = this.getFloat(path + ".yaw");
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    @Override
    public Location getLocation(final String path) {
        if (!this.isSet(path)) return null;
        final String world = this.getString(path + ".w");
        final double x = this.getDouble(path + ".x");
        final double y = this.getDouble(path + ".y");
        final double z = this.getDouble(path + ".z");
        final float pitch = this.getFloat(path + ".pitch");
        final float yaw = this.getFloat(path + ".yaw");
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    @Override
    public void setLocation(final String path, final Location value) {
        if (value == null) {
            this.set(path, null);
            return;
        }
        this.set(path + ".w", value.getWorld().getName());
        this.set(path + ".x", value.getX());
        this.set(path + ".y", value.getY());
        this.set(path + ".z", value.getZ());
        this.set(path + ".pitch", value.getPitch());
        this.set(path + ".yaw", value.getYaw());
    }

    @Override
    public Inventory getInventory(String path) {
        return InventorySerialization.StringToInventory(this.getString(path), path.split("\\.")[0]);
    }

    @Override
    public void setInventory(String path, Inventory inventory) {
        this.set(path, InventorySerialization.InventoryToString(inventory));
    }

    @Override
    public void setDate(String path, Date date) {
        this.set(path, DATE_FORMAT.format(date));
    }

    @Override
    public Date getDate(String path) {
        try {
            return DATE_FORMAT.parse(this.getString(path));
        } catch (ParseException e) {
            System.out.println("Error while parsing date");
            e.printStackTrace();
        }
        return null;
    }
}
