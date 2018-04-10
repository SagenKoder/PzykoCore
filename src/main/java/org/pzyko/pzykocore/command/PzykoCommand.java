package org.pzyko.pzykocore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface PzykoCommand {

    String getName();

    String getPermission();

    List<String> getAliases();

    void run(CommandSender sender, String label, String[] args);

    List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args, String curr);

}
