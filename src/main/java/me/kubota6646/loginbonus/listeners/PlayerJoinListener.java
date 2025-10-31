package me.kubota6646.loginbonus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import me.kubota6646.loginbonus.LoginBonusPlugin;
import me.kubota6646.loginbonus.managers.PlaytimeManager;
import me.kubota6646.loginbonus.managers.StreakManager;
import me.kubota6646.loginbonus.managers.RewardManager;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final LoginBonusPlugin plugin;
    private final PlaytimeManager playtimeManager;
    private final StreakManager streakManager;
    private final RewardManager rewardManager;

    public PlayerJoinListener(LoginBonusPlugin plugin, PlaytimeManager playtimeManager, StreakManager streakManager, RewardManager rewardManager) {
        this.plugin = plugin;
        this.playtimeManager = playtimeManager;
        this.streakManager = streakManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int required = plugin.getConfig().getInt("required-playtime-minutes");
        int current = playtimeManager.getTodayPlaytime(uuid);
        boolean claimed = plugin.getDataManager().hasClaimedToday(uuid);

        streakManager.updateStreak(uuid);

        BarColor color = BarColor.valueOf(plugin.getConfig().getString("bossbar-color", "GREEN"));
        BarStyle style = BarStyle.valueOf(plugin.getConfig().getString("bossbar-style", "SOLID"));
        BossBar bar = plugin.getServer().createBossBar("", color, style);

        if (claimed) {
            bar.setTitle(plugin.getConfig().getString("messages.bossbar-claimed"));
            bar.setProgress(1.0);
        } else if (current >= required) {
            bar.setTitle(plugin.getConfig().getString("messages.bossbar-claimable"));
            bar.setProgress(1.0);
        } else {
            String msg = plugin.getConfig().getString("messages.bossbar-progress")
                    .replace("%current%", String.valueOf(current))
                    .replace("%required%", String.valueOf(required));
            bar.setTitle(msg);
            bar.setProgress((double) current / required);
        }

        bar.addPlayer(player);
        bar.setVisible(true);
    }
}