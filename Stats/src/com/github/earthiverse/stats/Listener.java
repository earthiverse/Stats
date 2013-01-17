package com.github.earthiverse.stats;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener {
	UserList Cache = UserList.getInstance();
	
	/*
	 * Block Related Listeners
	 */
	
	// Block - Break
	@EventHandler
	public void BlockBreak(BlockBreakEvent event) {
		String player = event.getPlayer().getName();
		Block block = BlockFix.FixData(event.getBlock());
		Cache.updateBlocksDestroyed(player, block.getType().toString(), block.getData(), 1);
	}
	
	// Block - Place
	@EventHandler
	public void BlockPlace(BlockPlaceEvent event) {
		String player = event.getPlayer().getName();
		Block block = BlockFix.FixData(event.getBlock());
		Cache.updateBlocksPlaced(player, block.getType().toString(), block.getData(), 1);
	}
	
	// Empty Bucket Usage (For lava and water block placing)
	@EventHandler
	public void BucketEmpty(PlayerBucketEmptyEvent event) {
		String player = event.getPlayer().getName();
		if(event.getBucket().equals(Material.WATER_BUCKET)) {
			Cache.updateBlocksPlaced(player, Material.WATER.toString(), 0, 1);
		} else if(event.getBucket().equals(Material.LAVA_BUCKET)) {
			Cache.updateBlocksPlaced(player, Material.LAVA.toString(), 0, 1);
		}
	}
	
	// Fill Bucket Usage (For lava and water block picking up)
	@EventHandler
	public void BucketFill(PlayerBucketFillEvent event) {
		String player = event.getPlayer().getName();
		if(event.getBlockClicked().getType().equals(Material.STATIONARY_WATER)) {
			// We want to combine water and stationary_water in to one count
			Cache.updateBlocksDestroyed(player, Material.WATER.toString(), 0, 1);
		} else if(event.getBlockClicked().getType().equals(Material.STATIONARY_LAVA)) {
			// We want to combine lava and stationary_lava in to one count
			Cache.updateBlocksDestroyed(player, Material.LAVA.toString(), 0, 1);
		}
	}
	
	/*
	 * Player Related Listeners
	 */

	// Player - Login
	@EventHandler
	public void PlayerLogin(PlayerLoginEvent event) {
		String player = event.getPlayer().getName();
		Long time = System.currentTimeMillis()/1000l;
		Cache.addPlayer(player);
		Cache.updateLogin(player, time);
	}
	
	// Player - Logout
	@EventHandler
	public void PlayerLogout(PlayerQuitEvent event) {
		String player = event.getPlayer().getName();
		Long time = System.currentTimeMillis()/1000l;
		Cache.updateLogout(player, time);
	}
	
	// Player - Experience
	@EventHandler
	public void PlayerExp(PlayerExpChangeEvent event) {
		String player = event.getPlayer().getName();
		Cache.updateExp(player, event.getAmount());
	}
	
	// TODO: Player Movement
}