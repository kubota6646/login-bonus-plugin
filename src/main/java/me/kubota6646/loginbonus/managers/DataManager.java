package me.kubota6646.loginbonus.managers;

import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

public interface DataManager {
    int getStreak(UUID uuid);
    void setStreak(UUID uuid, int streak);
    LocalDate getLastStreakDate(UUID uuid);
    void setLastStreakDate(UUID uuid, LocalDate date);
    int getTodayPlaytime(UUID uuid);
    void setTodayPlaytime(UUID uuid, int minutes);
    boolean hasClaimedToday(UUID uuid);
    void setClaimedToday(UUID uuid, boolean claimed);
    void resetDailyPlaytime();
    void resetPlaytime(UUID uuid);
    Set<UUID> getAllPlayers();
    void saveAll();
}