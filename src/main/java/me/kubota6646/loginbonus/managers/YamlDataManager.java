package me.kubota6646.loginbonus.managers;

import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.time.LocalDate;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import me.kubota6646.loginbonus.LoginBonusPlugin;

public class YamlDataManager implements DataManager {
    private final LoginBonusPlugin plugin;
    private final File dataFile;
    private final YamlConfiguration data;

    public YamlDataManager(LoginBonusPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public int getStreak(UUID uuid) {
        return data.getInt(uuid.toString() + ".streak", 0);
    }

    @Override
    public void setStreak(UUID uuid, int streak) {
        data.set(uuid.toString() + ".streak", streak);
        save();
    }

    @Override
    public LocalDate getLastStreakDate(UUID uuid) {
        String date = data.getString(uuid.toString() + ".lastStreakDate");
        return date != null ? LocalDate.parse(date) : null;
    }

    @Override
    public void setLastStreakDate(UUID uuid, LocalDate date) {
        data.set(uuid.toString() + ".lastStreakDate", date.toString());
        save();
    }

    @Override
    public int getTodayPlaytime(UUID uuid) {
        return data.getInt(uuid.toString() + ".todayPlaytime", 0);
    }

    @Override
    public void setTodayPlaytime(UUID uuid, int minutes) {
        data.set(uuid.toString() + ".todayPlaytime", minutes);
        save();
    }

    @Override
    public boolean hasClaimedToday(UUID uuid) {
        return data.getBoolean(uuid.toString() + ".claimedToday", false);
    }

    @Override
    public void setClaimedToday(UUID uuid, boolean claimed) {
        data.set(uuid.toString() + ".claimedToday", claimed);
        save();
    }

    @Override
    public void resetDailyPlaytime() {
        for (String key : data.getKeys(false)) {
            data.set(key + ".todayPlaytime", 0);
            data.set(key + ".claimedToday", false);
        }
        save();
    }

    @Override
    public void resetPlaytime(UUID uuid) {
        data.set(uuid.toString() + ".todayPlaytime", 0);
        data.set(uuid.toString() + ".claimedToday", false);
        save();
    }

    @Override
    public Set<UUID> getAllPlayers() {
        Set<UUID> players = new HashSet<>();
        for (String key : data.getKeys(false)) {
            players.add(UUID.fromString(key));
        }
        return players;
    }

    @Override
    public void saveAll() {
        save();
    }

    private void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data: " + e.getMessage());
        }
    }
}