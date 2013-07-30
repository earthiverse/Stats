package com.github.earthiverse.stats;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {
	ConcurrentHashMap<String, Player> cache;

	Listener(ConcurrentHashMap<String, Player> cache) {
		this.cache = cache;
	}

	// // Player Related
	// Login
	@EventHandler
	public void PlayerLogin(PlayerLoginEvent event) {
		String username = event.getPlayer().getName();
		Long time = System.currentTimeMillis() / 1000L;
		Player user = cache.get(username);
		if(user == null) {
			// Player is new since Plugin was loaded
			cache.put(username, new Player(0, time, 0));
		} else {
			user.update_login(time);
		}
	}

	// Logout
	@EventHandler
	public void PlayerLogout(PlayerQuitEvent event) {
		Long time = System.currentTimeMillis() / 1000L;
		Player user = cache.get(event.getPlayer().getName());
		user.set_logout_time(time);
	}

	// // Block Related
	// Block Break
	@EventHandler
	public void BlockBreak(BlockBreakEvent event) {
		Player user = cache.get(event.getPlayer().getName());
		BlockData block = BlockFix.FixData(event.getBlock());
		user.destroy_block(block.getType(), block.getData(), 1);
	}

	// Block Place
	@EventHandler
	public void BlockPlace(BlockPlaceEvent event) {
		Player user = cache.get(event.getPlayer().getName());
		BlockData block = BlockFix.FixData(event.getBlock());
		user.place_block(block.getType(), block.getData(), 1);
	}

	// Empty Bucket Usage (For lava and water block placing)
	@EventHandler
	public void BucketEmpty(PlayerBucketEmptyEvent event) {
		Player user = cache.get(event.getPlayer().getName());
		if (event.getBucket().equals(Material.WATER_BUCKET)) {
			user.place_block(Material.WATER.toString(), 0, 1);
		} else if (event.getBucket().equals(Material.LAVA_BUCKET)) {
			user.place_block(Material.LAVA.toString(), 0, 1);
		}
	}

	// Fill Bucket Usage (For lava and water block picking up)
	@EventHandler
	public void BucketFill(PlayerBucketFillEvent event) {
		Player user = cache.get(event.getPlayer().getName());
		if (event.getBlockClicked().getType().equals(Material.STATIONARY_WATER)) {
			// Combine Water and Stationary Water to Water
			user.destroy_block(Material.WATER.toString(), 0, 1);
		} else if (event.getBlockClicked().getType()
				.equals(Material.STATIONARY_LAVA)) {
			// Combine Lava and Stationary Lava to Lava
			user.destroy_block(Material.LAVA.toString(), 0, 1);
		}
	}
}
