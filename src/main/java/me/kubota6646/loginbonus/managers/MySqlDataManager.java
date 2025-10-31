package me.kubota6646.loginbonus.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.time.LocalDate;
import me.kubota6646.loginbonus.LoginBonusPlugin;

public class MySqlDataManager implements DataManager {
    private final LoginBonusPlugin plugin;
    private Connection connection;

    public MySqlDataManager(LoginBonusPlugin plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        String host = plugin.getConfig().getString("storage.mysql.host");
        int port = plugin.getConfig().getInt("storage.mysql.port");
        String database = plugin.getConfig().getString("storage.mysql.database");
        String username = plugin.getConfig().getString("storage.mysql.username");
        String password = plugin.getConfig().getString("storage.mysql.password");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";

        try {
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("Connected to MySQL database.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to MySQL: " + e.getMessage());
            connection = null;
        }
    }

    private void createTable() {
        if (connection == null) return;
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "streak INT DEFAULT 0, " +
                "last_streak_date VARCHAR(10), " +
                "today_playtime INT DEFAULT 0, " +
                "claimed_today BOOLEAN DEFAULT FALSE)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create table: " + e.getMessage());
        }
    }

    @Override
    public int getStreak(UUID uuid) {
        if (connection == null) return 0;
        String sql = "SELECT streak FROM player_data WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("streak");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get streak: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void setStreak(UUID uuid, int streak) {
        if (connection == null) return;
        String sql = "INSERT INTO player_data (uuid, streak) VALUES (?, ?) ON DUPLICATE KEY UPDATE streak = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, streak);
            stmt.setInt(3, streak);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set streak: " + e.getMessage());
        }
    }

    @Override
    public LocalDate getLastStreakDate(UUID uuid) {
        if (connection == null) return null;
        String sql = "SELECT last_streak_date FROM player_data WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String date = rs.getString("last_streak_date");
                return date != null ? LocalDate.parse(date) : null;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get last streak date: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setLastStreakDate(UUID uuid, LocalDate date) {
        if (connection == null) return;
        String sql = "INSERT INTO player_data (uuid, last_streak_date) VALUES (?, ?) ON DUPLICATE KEY UPDATE last_streak_date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, date.toString());
            stmt.setString(3, date.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set last streak date: " + e.getMessage());
        }
    }

    @Override
    public int getTodayPlaytime(UUID uuid) {
        if (connection == null) return 0;
        String sql = "SELECT today_playtime FROM player_data WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("today_playtime");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get today playtime: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void setTodayPlaytime(UUID uuid, int minutes) {
        if (connection == null) return;
        String sql = "INSERT INTO player_data (uuid, today_playtime) VALUES (?, ?) ON DUPLICATE KEY UPDATE today_playtime = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, minutes);
            stmt.setInt(3, minutes);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set today playtime: " + e.getMessage());
        }
    }

    @Override
    public boolean hasClaimedToday(UUID uuid) {
        if (connection == null) return false;
        String sql = "SELECT claimed_today FROM player_data WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getBoolean("claimed_today");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get claimed today: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void setClaimedToday(UUID uuid, boolean claimed) {
        if (connection == null) return;
        String sql = "INSERT INTO player_data (uuid, claimed_today) VALUES (?, ?) ON DUPLICATE KEY UPDATE claimed_today = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setBoolean(2, claimed);
            stmt.setBoolean(3, claimed);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set claimed today: " + e.getMessage());
        }
    }

    @Override
    public void resetDailyPlaytime() {
        if (connection == null) return;
        String sql = "UPDATE player_data SET today_playtime = 0, claimed_today = FALSE";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to reset daily playtime: " + e.getMessage());
        }
    }

    @Override
    public void resetPlaytime(UUID uuid) {
        if (connection == null) return;
        String sql = "UPDATE player_data SET today_playtime = 0, claimed_today = FALSE WHERE uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to reset playtime: " + e.getMessage());
        }
    }

    @Override
    public Set<UUID> getAllPlayers() {
        Set<UUID> players = new HashSet<>();
        if (connection == null) return players;
        String sql = "SELECT uuid FROM player_data";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.add(UUID.fromString(rs.getString("uuid")));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get all players: " + e.getMessage());
        }
        return players;
    }

    @Override
    public void saveAll() {
        // MySQLは自動コミットなので不要
    }
}