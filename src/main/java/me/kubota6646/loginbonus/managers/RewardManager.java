package me.kubota6646.loginbonus.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import me.kubota6646.loginbonus.LoginBonusPlugin;
import java.util.List;
import java.util.Map;

public class RewardManager {
    private final LoginBonusPlugin plugin;

    public RewardManager(LoginBonusPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasSpace(Player player, int slotsNeeded) {
        PlayerInventory inv = player.getInventory();
        int freeSlots = 0;
        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null) {
                freeSlots++;
            }
        }
        return freeSlots >= slotsNeeded;
    }

    public void giveLoginReward(Player player, int streak) {
        List<Map<?, ?>> rewards = plugin.getConfig().getMapList("login-rewards");
        for (Map<?, ?> reward : rewards) {
            Material material = Material.valueOf((String) reward.get("material"));
            int amount = ((Number) reward.get("amount")).intValue() * streak;
            ItemStack item = new ItemStack(material, amount);
            player.getInventory().addItem(item);
        }
    }

    public void giveSpecialStreakReward(Player player, int streak) {
        if (!plugin.getConfig().getBoolean("special-streak-rewards-enabled")) return;
        String key = String.valueOf(streak);
        if (plugin.getConfig().contains("special-streak-rewards." + key)) {
            Map<String, Object> reward = plugin.getConfig().getConfigurationSection("special-streak-rewards." + key).getValues(false);
            String message = (String) reward.get("message");
            if (message != null) {
                player.sendMessage(plugin.getConfig().getString("messages.special-bonus-received").replace("%days%", String.valueOf(streak)));
            }
            List<Map<?, ?>> items = (List<Map<?, ?>>) reward.get("items");
            dropItems(player, items);
        }
    }

    public void giveMultipleReward(Player player, int streak) {
        if (!plugin.getConfig().getBoolean("multiples-enabled")) return;
        for (String key : plugin.getConfig().getConfigurationSection("multiples").getKeys(false)) {
            int multiple = Integer.parseInt(key);
            if (streak % multiple == 0) {
                Map<String, Object> reward = plugin.getConfig().getConfigurationSection("multiples." + key).getValues(false);
                String message = (String) reward.get("message");
                if (message != null) {
                    player.sendMessage(message.replace("%days%", String.valueOf(streak)));
                }
                List<Map<?, ?>> items = (List<Map<?, ?>>) reward.get("items");
                dropItems(player, items);
            }
        }
    }

    private void dropItems(Player player, List<Map<?, ?>> items) {
        for (Map<?, ?> item : items) {
            Material material = Material.valueOf((String) item.get("type"));
            int amount = ((Number) item.get("amount")).intValue();
            ItemStack stack = new ItemStack(material, amount);
            player.getWorld().dropItem(player.getLocation(), stack);
        }
    }
}