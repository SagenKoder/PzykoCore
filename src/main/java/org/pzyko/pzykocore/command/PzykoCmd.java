package org.pzyko.pzykocore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.pzyko.pzykocore.PzykoCore;

import java.util.ArrayList;
import java.util.List;

public class PzykoCmd implements PzykoCommand {

    @Override
    public String getName() {
        return "pzyko";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<String>();
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        sender.sendMessage("§6>----[ §6§lPzyko.Org §6]----<");

        String authors = String.join(", ", PzykoCore.get().getDescription().getAuthors());
        sender.sendMessage("Developer: §b" + authors);
        sender.sendMessage("Version: §b" + PzykoCore.get().getDescription().getVersion());
        sender.sendMessage("Description: §b" + PzykoCore.get().getDescription().getDescription());

        sender.sendMessage("§7--");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args, String curr) {
        return null;
    }

}
