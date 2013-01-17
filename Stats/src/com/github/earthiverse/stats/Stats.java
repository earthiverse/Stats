package com.github.earthiverse.stats;

import lib.PatPeter.SQLibrary.MySQL;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.earthiverse.stats.UserList.Player;

public class Stats extends JavaPlugin {
	
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	Runnable UpdateDB = new UpdateDB();
	
	private MySQL mysql;
	private String table_name;
	UserList Cache = UserList.getInstance();
	
	/** Plugin Startup **/
	@Override
	public void onEnable() {
		// Load settings from config.yml in Stats plugin directory
	    this.saveDefaultConfig();
		String mysql_hostname = this.getConfig().getString("mysql.hostname");
		int mysql_port = this.getConfig().getInt("mysql.port");
		String mysql_database = this.getConfig().getString("mysql.database");
		this.table_name = this.getConfig().getString("mysql.table");
		String mysql_username = this.getConfig().getString("mysql.username");
		String mysql_password = this.getConfig().getString("mysql.password");

		// Connect to MySQL Server
		mysql = new MySQL(getLogger(), "[Stats]", mysql_hostname, mysql_port, mysql_database, mysql_username, mysql_password);
		mysql.open();
		if(mysql.isConnected()) {
			System.out.println("[Stats] Connected to Database @ " + mysql_hostname + ":" + mysql_port + "!");
		} else {
			System.out.println("[Stats] Problem connecting to " + mysql_hostname + ":" + mysql_port + "!");
		}
		
		// Start Event Handlers
		getServer().getPluginManager().registerEvents(new Listener(), this);
		
		// Update the database in 60 seconds, and every 30 seconds thereafter
		executor.scheduleWithFixedDelay(UpdateDB, 60, 30, TimeUnit.SECONDS);
	}
	
	/** Plugin Shutdown **/
	@Override
	public void onDisable() {
		// Update the database one last time
		executor.execute(UpdateDB);
		executor.shutdown();
		
		// Wait at most a minute for the database to finish its thing
		try {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Close Connection to MySQL Server
		mysql.close();
	}
	
	/** Update Database Loop **/
	// Updates the database
	private final class UpdateDB implements Runnable {
		@Override
		public void run() {
			/*	Generic statement to update the stats table in the database
			 *	1 - player name (e.g. earthiverse)
			 *	2 - category (e.g. BlockDestroy)
			 *	3 - stat (e.g. DIRT)
			 *	4 - data (e.g. Different types of leaves)
			 *	5 and 6 - amount (e.g. 5 blocks destroyed)
			 */
			PreparedStatement replaceStat = null;
			String replaceStat_Statement = "INSERT INTO " + table_name + " (`player`,`category`,`stat`,`data`,`amount`) "
					   + "VALUES (?,?,?,?,?) "
					   + "ON DUPLICATE KEY UPDATE `amount` = ?;";
			PreparedStatement updateStat = null;
			String updateStat_Statement = "INSERT INTO " + table_name + " (`player`,`category`,`stat`,`data`,`amount`) "
							   + "VALUES (?,?,?,?,?) "
							   + "ON DUPLICATE KEY UPDATE `amount` = `amount` + ?;";
			try {
				replaceStat = mysql.prepare(replaceStat_Statement);
				updateStat = mysql.prepare(updateStat_Statement);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Open User Cache
			for (Entry<String, Player> user : Cache.getPlayerSet()) {
				// Set player
				try {
					replaceStat.setString(1, user.getKey());
					updateStat.setString(1, user.getKey());
				} catch (SQLException e) {
					e.printStackTrace();
				}

				// Update Blocks Destroyed
				try {
					updateStat.setString(2, "BlockDestroy");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				for (Entry<String, Integer> block: user.getValue().BlocksDestroyed.entrySet()) {
					try {
						// Set Block Name
						updateStat.setString(3, block.getKey().split(",")[0]);
						
						// Set Block Data
						updateStat.setInt(4, Integer.parseInt(block.getKey().split(",")[1]));
						
						// Set Blocks Destroyed
						updateStat.setInt(5, block.getValue());
						updateStat.setInt(6, block.getValue());
						updateStat.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				// Remove the values we just added
				user.getValue().BlocksDestroyed.clear();
				
				// Update Blocks Placed
				try {
					updateStat.setString(2, "BlockPlace");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				for (Entry<String, Integer> block: user.getValue().BlocksPlaced.entrySet()) {
					try {
						// Set Block Name
						updateStat.setString(3, block.getKey().split(",")[0]);
						
						// Set Block Data
						updateStat.setInt(4, Integer.parseInt(block.getKey().split(",")[1]));
						
						// Set Blocks Destroyed
						updateStat.setInt(5, block.getValue());
						updateStat.setInt(6, block.getValue());
						updateStat.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				// Remove the values we just added
				user.getValue().BlocksPlaced.clear();
				
				// Update Player Experience
				if(user.getValue().Experience != 0) {
					try {
						updateStat.setString(2, "PlayerStat");
						updateStat.setString(3, "EXP_GAINED");
						updateStat.setInt(4, 0);
						updateStat.setInt(5, user.getValue().Experience);
						updateStat.setInt(6, user.getValue().Experience);
						updateStat.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// Reset EXP cache to zero
					user.getValue().Experience = 0;
				}
				
				// Replace Player Last_Login Time
				if(user.getValue().Login != 0) {
					try {
						replaceStat.setString(2, "PlayerStat");
						replaceStat.setString(3, "LOGIN_LAST");
						replaceStat.setInt(4, 0);
						replaceStat.setLong(5, user.getValue().Login);
						replaceStat.setLong(6, user.getValue().Login);
						replaceStat.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// Reset Login to zero
					user.getValue().Login = 0;
				}
				
				// Replace Player Last_Logout Time
				if(user.getValue().Logout != 0) {
					try {
						replaceStat.setString(2, "PlayerStat");
						replaceStat.setString(3, "LOGOUT_LAST");
						replaceStat.setInt(4, 0);
						replaceStat.setLong(5, user.getValue().Logout);
						replaceStat.setLong(6, user.getValue().Logout);
						replaceStat.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// Reset Login to zero
					user.getValue().Logout = 0;
					Cache.removePlayer(user.getKey());
				}
			}
		}
	}
}