package me.kubota6646.loginbonus;

import org.bukkit.plugin.java.JavaPlugin;
import me.kubota6646.loginbonus.listeners.PlayerJoinListener;
import me.kubota6646.loginbonus.listeners.PlayerQuitListener;
import me.kubota6646.loginbonus.managers.DataManager;
import me.kubota6646.loginbonus.managers.YamlDataManager;
import me.kubota6646.loginbonus.managers.MySqlDataManager;
import me.kubota6646.loginbonus.managers.StreakManager;
import me.kubota6646.loginbonus.managers.PlaytimeManager;
import me.kubota6646.loginbonus.managers.RewardManager;
import me.kubota6646.loginbonus.commands.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LoginBonusPlugin extends JavaPlugin {
    private StreakManager streakManager;
    private PlaytimeManager playtimeManager;
    private RewardManager rewardManager;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // ストレージタイプに応じてDataManagerを選択
        String storageType = getConfig().getString("storage.type", "yaml");
        if ("mysql".equals(storageType)) {
            dataManager = new MySqlDataManager(this);
        } else {
            dataManager = new YamlDataManager(this);
        }
        this.streakManager = new StreakManager(dataManager);
        this.playtimeManager = new PlaytimeManager(dataManager);
        this.rewardManager = new RewardManager(this);

        // リスナー登録
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, playtimeManager, streakManager, rewardManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playtimeManager), this);

        // command.ymlからコマンドを動的に登録
        registerCommandsFromConfig();

        // 毎日リセットスケジューラー (0時)
        getServer().getScheduler().runTaskTimer(this, () -> {
            playtimeManager.resetDailyPlaytime();
            streakManager.resetStreaksIfNotMet();
        }, 0L, 24000L); // 毎日チェック (20分 * 60 * 24)

        getLogger().info("LoginBonusPlugin enabled!");
    }

    @Override
    public void onDisable() {
        dataManager.saveAll();
        getLogger().info("LoginBonusPlugin disabled!");
    }

    private void registerCommandsFromConfig() {
        File commandFile = new File(getDataFolder(), "command.yml");
        if (!commandFile.exists()) {
            saveResource("command.yml", false);
        }
        FileConfiguration commandConfig = YamlConfiguration.loadConfiguration(commandFile);

        if (commandConfig.contains("commands")) {
            for (String commandName : commandConfig.getConfigurationSection("commands").getKeys(false)) {
                String description = commandConfig.getString("commands." + commandName + ".description", "");
                String usage = commandConfig.getString("commands." + commandName + ".usage", "");
                String permission = commandConfig.getString("commands." + commandName + ".permission", "");
                List<String> aliases = commandConfig.getStringList("commands." + commandName + ".aliases");

                CommandExecutor executor = getCommandExecutor(commandName);
                if (executor != null) {
                    registerCommand(commandName, executor, aliases, description, usage, permission);
                }
            }
        }
    }

    private CommandExecutor getCommandExecutor(String commandName) {
        switch (commandName) {
            case "receivebonus":
                return new ReceiveBonusCommand(this, playtimeManager, streakManager, rewardManager);
            case "forcereward":
                return new ForceRewardCommand(this, rewardManager);
            case "setstreak":
                return new SetStreakCommand(this, streakManager);
            case "loginbonus":
                return new LoginBonusReloadCommand(this);
            default:
                return null;
        }
    }

    private void registerCommand(String name, CommandExecutor executor, List<String> aliases, String description, String usage, String permission) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(name, this);
            command.setExecutor(executor);
            command.setDescription(description);
            command.setUsage(usage);
            command.setPermission(permission);
            command.setAliases(aliases);

            Field field = getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(getServer());
            commandMap.register(getDescription().getName(), command);
        } catch (Exception e) {
            getLogger().severe("Failed to register command: " + name + " - " + e.getMessage());
        }
    }

    public StreakManager getStreakManager() { return streakManager; }
    public PlaytimeManager getPlaytimeManager() { return playtimeManager; }
    public RewardManager getRewardManager() { return rewardManager; }
    public DataManager getDataManager() { return dataManager; }
}