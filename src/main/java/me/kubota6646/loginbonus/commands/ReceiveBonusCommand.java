package me.kubota6646.loginbonus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.kubota6646.loginbonus.LoginBonusPlugin;
import me.kubota6646.loginbonus.managers.PlaytimeManager;
import me.kubota6646.loginbonus.managers.StreakManager;
import me.kubota6646.loginbonus.managers.RewardManager;
import java.util.List;
import java.util.Map;

public class ReceiveBonusCommand implements CommandExecutor {
    private final LoginBonusPlugin plugin;
    private final PlaytimeManager playtimeManager;
    private final StreakManager streakManager;
    private final RewardManager rewardManager;

    public ReceiveBonusCommand(LoginBonusPlugin plugin, PlaytimeManager playtimeManager, StreakManager streakManager, RewardManager rewardManager) {
        this.plugin = plugin;
        this.playtimeManager = playtimeManager;
        this.streakManager = streakManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        int required = plugin.getConfig().getInt("required-playtime-minutes");
        int current = playtimeManager.getTodayPlaytime(uuid);
        int streak = streakManager.getStreak(uuid);
        boolean claimed = plugin.getDataManager().hasClaimedToday(uuid);

        if (claimed) {
            player.sendMessage(plugin.getConfig().getString("messages.bonus-received"));
            return true;
        }

        if (current < required) {
            player.sendMessage(plugin.getConfig().getString("messages.insufficient-playtime").replace("%required%", String.valueOf(required)));
            return true;
        }

        List<Map<?, ?>> rewards = plugin.getConfig().getMapList("login-rewards");
        if (!rewardManager.hasSpace(player, rewards.size())) {
            player.sendMessage(plugin.getConfig().getString("messages.inventory-full").replace("%slots%", String.valueOf(rewards.size())));
            return true;
        }

        rewardManager.giveLoginReward(player, streak);
        rewardManager.giveSpecialStreakReward(player, streak);
        rewardManager.giveMultipleReward(player, streak);

        plugin.getDataManager().setClaimedToday(uuid, true);

        player.sendMessage(plugin.getConfig().getString("messages.bonus-received"));

        return true;
    }
}