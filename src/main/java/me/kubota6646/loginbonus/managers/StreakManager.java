package me.kubota6646.loginbonus.managers;

import java.time.LocalDate;
import java.util.UUID;

public class StreakManager {
    private final DataManager dataManager;

    public StreakManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public int getStreak(UUID uuid) {
        return dataManager.getStreak(uuid);
    }

    public void setStreak(UUID uuid, int streak) {
        dataManager.setStreak(uuid, streak);
    }

    public LocalDate getLastStreakDate(UUID uuid) {
        return dataManager.getLastStreakDate(uuid);
    }

    public void setLastStreakDate(UUID uuid, LocalDate date) {
        dataManager.setLastStreakDate(uuid, date);
    }

    public void updateStreak(UUID uuid) {
        LocalDate lastDate = getLastStreakDate(uuid);
        LocalDate today = LocalDate.now();
        int currentStreak = getStreak(uuid);

        if (lastDate == null) {
            setStreak(uuid, 1);
            setLastStreakDate(uuid, today);
        } else if (lastDate.equals(today.minusDays(1))) {
            setStreak(uuid, currentStreak + 1);
            setLastStreakDate(uuid, today);
        } else if (!lastDate.equals(today)) {
            setStreak(uuid, 1);
            setLastStreakDate(uuid, today);
        }
    }

    public void resetStreaksIfNotMet() {
        for (UUID uuid : dataManager.getAllPlayers()) {
            LocalDate lastDate = getLastStreakDate(uuid);
            LocalDate today = LocalDate.now();
            if (lastDate != null && !lastDate.equals(today) && !lastDate.equals(today.minusDays(1))) {
                setStreak(uuid, 0);
            }
        }
    }
}