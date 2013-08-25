package com.github.earthiverse.stats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Stats extends JavaPlugin {
	// Logger
	Logger log;

	// Thread for updating the database
	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();
	Runnable updater = new UpdateDB();

	// MySQL Info
	private String table;
	private String hostname;
	private String port;
	private String database;
	private String username;
	private String password;
	private Connection connection;

	// Player Cache
	ConcurrentHashMap<String, Player> cache;

	// // Plugin Startup
	@Override
	public void onEnable() {
		log = getLogger();
		cache = new ConcurrentHashMap<String, Player>();
		for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
			cache.putIfAbsent(player.getName(), new Player());
		}

		// // MySQL Configuration
		this.saveDefaultConfig();

		// Get configuration
		FileConfiguration Config = this.getConfig();
		table = Config.getString("mysql.table");
		hostname = Config.getString("mysql.hostname");
		port = Config.getString("mysql.port");
		database = Config.getString("mysql.database");
		username = Config.getString("mysql.username");
		password = Config.getString("mysql.password");

		// Setup Event Listener
		getServer().getPluginManager()
				.registerEvents(new Listener(cache), this);

		// Setup MySQL Updating Thread
		// TODO: Put & get update time in/from settings
		executor.scheduleWithFixedDelay(updater, 60, 30, TimeUnit.SECONDS);

		// We're good to go!
		log.info("Started!");
	}

	// // Plugin Shutdown
	@Override
	public void onDisable() {
		executor.execute(updater);
		executor.shutdown();

		// Wait at most a minute for the database to finish its thing
		try {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.warning("WARNING: Problem Running Shutdown Updater!");
			e.printStackTrace();
		}

		// Close Connection to MySQL Server
		log.info("Shutdown!");
	}

	private final class UpdateDB implements Runnable {
		@Override
		public void run() {
			// Make connection
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://"
						+ hostname + ":" + port + "/" + database, username,
						password);
			} catch (SQLException e) {
				System.out
						.println("Could not connect to MySQL server! because: "
								+ e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("JDBC Driver not found!");
			}

			String replace_s = "REPLACE INTO " + table
					+ " (`username`,`category`,`statistic`,`data`,`value`)"
					+ " VALUES (?,?,?,?,?);";
			String update_s = "INSERT INTO " + table
					+ " (`username`,`category`,`statistic`,`data`,`value`)"
					+ " VALUES (?,?,?,?,?)"
					+ " ON DUPLICATE KEY UPDATE `value` = `value` + ?;";
			PreparedStatement replace = null;
			PreparedStatement update = null;
			try {

				replace = connection.prepareStatement(replace_s);
				update = connection.prepareStatement(update_s);
			} catch (SQLException e) {
				log.warning("WARNING: Problem Creating Prepared Statement!");
				e.printStackTrace();
			}

			// Go through cache, dumping it to the database then emptying it.
			// Possible TODO: There's a lot of duplicated code in the following
			// section
			for (Entry<String, Player> user : cache.entrySet()) {
				String username = user.getKey();
				Player data = user.getValue();

				try {
					long current_time = System.currentTimeMillis() / 1000L;

					// Set Username
					replace.setString(1, username);
					update.setString(1, username);

					// Update Blocks Destroyed
					update.setString(2, "BlockDestroy");
					for (Entry<Key, Integer> block : data.blocks_destroyed
							.entrySet()) {
						Key block_type = block.getKey();
						int amount = block.getValue();
						if (amount > 0) {
							update.setString(3, block_type.get_block());
							update.setInt(4, block_type.get_data());
							update.setInt(5, amount);
							update.setInt(6, amount);
							update.executeUpdate();
							block.setValue(0);
						}
					}

					// Update Blocks Placed
					update.setString(2, "BlockPlace");
					for (Entry<Key, Integer> block : data.blocks_placed
							.entrySet()) {
						Key block_type = block.getKey();
						int amount = block.getValue();
						if (amount > 0) {
							update.setString(3, block_type.get_block());
							update.setInt(4, block_type.get_data());
							update.setInt(5, amount);
							update.setInt(6, amount);
							update.executeUpdate();
							block.setValue(0);
						}
					}

					// // Player Stats
					update.setString(2, "PlayerStat");
					update.setInt(4, 0);
					replace.setString(2, "PlayerStat");
					replace.setInt(4, 0);

					// Update Player Experience
					int experience = data.get_experience();
					if (experience != 0) {
						update.setString(3, "EXP_GAINED");
						update.setInt(5, experience);
						update.setInt(6, experience);
						update.executeUpdate();
						data.set_experience(0);
					}

					// Update Player Played-For Time
					long last_update_time = data.get_last_update_time();
					long played_for_time = 0;
					if (last_update_time == 0) {
						// Player logged in since last update
						played_for_time = current_time - data.get_login_time();
					} else if (data.get_logout_time() == 0) {
						// Player is still logged in
						played_for_time = current_time - last_update_time;
					} else {
						// Player has logged out since last update
						played_for_time = data.get_logout_time()
								- last_update_time;
					}
					if (played_for_time > 0) {
						update.setString(3, "PLAYED_FOR");
						update.setLong(5, played_for_time);
						update.setLong(6, played_for_time);
						update.executeUpdate();
					}

					// Update Player Last-Login Time
					long login_time = data.get_login_time();
					if (login_time != 0) {
						replace.setString(3, "LOGIN_LAST");
						replace.setLong(5, login_time);
						replace.executeUpdate();
						data.set_login_time(0);
					}

					// Update Player Last-Logout Time
					long logout_time = data.get_logout_time();
					if (logout_time != 0) {
						replace.setString(3, "LOGOUT_LAST");
						replace.setLong(5, logout_time);
						replace.executeUpdate();
						data.set_logout_time(0);
					}

					data.set_last_update_time(current_time);

				} catch (SQLException e) {
					log.warning("WARNING: Problem Adding User Data to Database!");
					e.printStackTrace();
				}
			}

			try {
				connection.close();
			} catch (SQLException e) {
				log.warning("WARNING: Problem Committing and Closing Database!");
				e.printStackTrace();
			}
		}
	}
}