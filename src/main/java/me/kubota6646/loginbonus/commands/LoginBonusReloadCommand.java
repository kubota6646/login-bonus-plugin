package me.kubota6646.loginbonus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import me.kubota6646.loginbonus.LoginBonusPlugin;

public class LoginBonusReloadCommand implements CommandExecutor {
    private final LoginBonusPlugin plugin;

    public LoginBonusReloadCommand(LoginBonusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("loginbonus.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /loginbonus <reload|forcegive|resetplaytime> <player>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                plugin.reloadConfig();
                sender.sendMessage("Config reloaded.");
                break;
            case "forcegive":
                if (args.length < 2) {
                    sender.sendMessage("Usage: /loginbonus forcegive <player>");
                    return true;
                }
                Player target1 = Bukkit.getPlayer(args[1]);
                if (target1 == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                // Force give today's reward
                plugin.getRewardManager().giveLoginReward(target1, plugin.getStreakManager().getStreak(target1.getUniqueId()));
                plugin.getDataManager().setClaimedToday(target1.getUniqueId(), true);
                sender.sendMessage("Forced today's reward to " + target1.getName());
                break;
            case "resetplaytime":
                if (args.length < 2) {
                    sender.sendMessage("Usage: /loginbonus resetplaytime <player>");
                    return true;
                }
                Player target2 = Bukkit.getPlayer(args[1]);
                if (target2 == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                plugin.getPlaytimeManager().resetPlaytime(target2.getUniqueId());
                sender.sendMessage("Reset playtime for " + target2.getName());
                break;
            default:
                sender.sendMessage("Unknown subcommand.");
                break;
        }

        return true;
    }
}