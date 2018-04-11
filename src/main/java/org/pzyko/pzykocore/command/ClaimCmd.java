package org.pzyko.pzykocore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClaimCmd implements PzykoCommand {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("§6-------[ Claiming ]-------");
            sendHelp(sender);
            sender.sendMessage("§8--");
            return;
        }

        switch(args[0].toLowerCase()) {
            case "info" : subInfo(sender, Arrays.copyOfRange(args, 1, args.length));
            break;
            case "create" : subCreate(sender, Arrays.copyOfRange(args, 1, args.length));
            break;
            case "remove" : subRemove(sender, Arrays.copyOfRange(args, 1, args.length));
            break;
            case "grant" : subGrant(sender, Arrays.copyOfRange(args, 1, args.length));
            break;
            case "deny" : subDeny(sender, Arrays.copyOfRange(args, 1, args.length));
            default :
                sender.sendMessage("§Wrong use of the command!");
                sendHelp(sender);
                return;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e/claim §7Shows this list");
        sender.sendMessage("§e/claim info §7Displays info about a claim");
        sender.sendMessage("§e/claim create §7Create a new claim");
        sender.sendMessage("§e/claim remove §7Delete the claim you are standing in");
        sender.sendMessage("§e/claim grant <player | ALL> access §7Grants access");
        sender.sendMessage("§e/claim grant <player | ALL> container §7Grants container usage");
        sender.sendMessage("§e/claim grant <player | ALL> build §7Grants building");
        sender.sendMessage("§e/claim grant <player | ALL> manage §7Grants manage the claim");
        sender.sendMessage("§e/claim grant <player | ALL> all §7Grants every permission");
        sender.sendMessage("§e/claim deny <player | ALL> §7Removes all grants");
    }

    private void subInfo(CommandSender sender, String...args) {
        sender.sendMessage("§eTODO.... " + String.join(" ", args));
    }

    private void subCreate(CommandSender sender, String...args) {
        sender.sendMessage("§eTODO.... " + String.join(" ", args));
    }

    private void subRemove(CommandSender sender, String...args) {
        sender.sendMessage("§eTODO.... " + String.join(" ", args));
    }

    private void subGrant(CommandSender sender, String...args) {
        sender.sendMessage("§eTODO.... " + String.join(" ", args));
    }

    private void subDeny(CommandSender sender, String...args) {
        sender.sendMessage("§eTODO.... " + String.join(" ", args));
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args, String curr) {
        return null;
    }
}
