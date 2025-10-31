package me.kubota6646.loginbonus.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import me.kubota6646.loginbonus.managers.PlaytimeManager;
import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final PlaytimeManager playtimeManager;

    public PlayerQuitListener(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        // ログアウト時の処理（必要に応じて追加）
    }
}