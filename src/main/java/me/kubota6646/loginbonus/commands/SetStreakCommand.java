package me.kubota6646.loginbonus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import me.kubota6646.loginbonus.LoginBonusPlugin;
import me.kubota6646.loginbonus.managers.StreakManager;

public class SetStreakCommand implements CommandExecutor {
    private final LoginBonusPlugin plugin;
    private final StreakManager streakManager;

    public SetStreakCommand(LoginBonusPlugin plugin, StreakManager streakManager) {
        this.plugin = plugin;
        this.streakManager = streakManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("loginbonus.admin")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /setstreak <player> <days>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        int days;
        try {
            days = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid days.");
            return true;
        }

        streakManager.setStreak(target.getUniqueId(), days);
        sender.sendMessage("Set streak for " + target.getName() + " to " + days + " days.");
        return true;
    }
}