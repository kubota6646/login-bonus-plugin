package me.kubota6646.loginbonus.managers;

import java.util.UUID;

public class PlaytimeManager {
    private final DataManager dataManager;

    public PlaytimeManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public int getTodayPlaytime(UUID uuid) {
        return dataManager.getTodayPlaytime(uuid);
    }

    public void setTodayPlaytime(UUID uuid, int minutes) {
        dataManager.setTodayPlaytime(uuid, minutes);
    }

    public void addPlaytime(UUID uuid, int minutes) {
        int current = getTodayPlaytime(uuid);
        setTodayPlaytime(uuid, current + minutes);
    }

    public void resetDailyPlaytime() {
        dataManager.resetDailyPlaytime();
    }

    public void resetPlaytime(UUID uuid) {
        dataManager.resetPlaytime(uuid);
    }
}