package org.pzyko.pzykocore;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.pzyko.pzykocore.command.PzykoCmd;
import org.pzyko.pzykocore.command.PzykoCommand;

import java.util.ArrayList;
import java.util.List;

public class PzykoCommands implements TabCompleter {

    public static List<PzykoCommand> commands = new ArrayList<>();
    public static List<String> disabled;
    public static PzykoCommands pzykoCommands;

    public static void load() {
        // load commands
        commands.add(new PzykoCmd());

        pzykoCommands = new PzykoCommands();

        for (PzykoCommand cmd : commands) {
            Bukkit.getPluginCommand("pzykocore:" + cmd.getName()).setTabCompleter(pzykoCommands);
        }
    }

    public static void onCmd(final CommandSender sender, Command cmd, String label, final String[] args) {
        if (label.contains(":")) label = label.split(":")[1];

        for (PzykoCommand pcmd : commands) {
            if (label.equals(pcmd.getName()) || pcmd.getAliases().contains(label)) {
                pcmd.run(sender, label, args);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.contains(":")) label = label.split(":")[1];

        List<String> ret = new ArrayList<>();

        for (PzykoCommand pcmd : commands) {
            if (label.equals(pcmd.getName()) || pcmd.getAliases().contains(label)) {
                try {
                    ret = pcmd.onTabComplete(sender, cmd, label, args, args[args.length - 1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (ret == null) {
            ret = new ArrayList<>();
            for (Player o : Bukkit.getOnlinePlayers()) {
                ret.add(o.getName());
            }
        }

        List<String> rem = new ArrayList<>();
        for (String s : ret) {
            if (!StringUtil.startsWithIgnoreCase(s, args[args.length - 1])) {
                rem.add(s);
            }
        }
        ret.removeAll(rem);
        return ret;
    }
}
