package com.github.earthiverse.stats;

import org.bukkit.Material;
import org.bukkit.block.Block;

/*
 * Fixes block data to personal preference
 * (I don't think it's worthwhile to store what position the blocks were in,
 * among other things)
 * 
 * Refer to http://www.minecraftwiki.net/wiki/Data_values
 */

public class BlockFix {
	public static Block FixData(Block block) {
		if (block.getType().equals(Material.LEAVES)) {
			// Remove Leaves Decay Data
			block.setData((byte) (block.getData() % 8));

		} else if (block.getType().equals(Material.SAPLING) && block.getData() >= 8) {
			// Remove Sapling Growth Data
			block.setData((byte) (block.getData() % 8));

		} else if (block.getType().equals(Material.TORCH)
				|| block.getType().equals(Material.REDSTONE_TORCH_OFF)
				|| block.getType().equals(Material.REDSTONE_TORCH_ON)) {
			// Remove Torch Position Data
			block.setData((byte) 0);

		} else if (block.getType().equals(Material.WOOD_STAIRS)
				|| block.getType().equals(Material.COBBLESTONE_STAIRS)
				|| block.getType().equals(Material.BRICK_STAIRS)
				|| block.getType().equals(Material.SMOOTH_STAIRS)
				|| block.getType().equals(Material.NETHER_BRICK_STAIRS)
				|| block.getType().equals(Material.SANDSTONE_STAIRS)
				|| block.getType().equals(Material.SPRUCE_WOOD_STAIRS)
				|| block.getType().equals(Material.BIRCH_WOOD_STAIRS)
				|| block.getType().equals(Material.JUNGLE_WOOD_STAIRS)) {
				//|| block.getType().equals(Material.QUARTZ_STAIRS)
			// Remove Stairs Position Data
			block.setData((byte) 0);

		} else if (block.getType().equals(Material.VINE)) {
			// Remove Vine Position Data
			block.setData((byte) 0);

		} else if (block.getType().equals(Material.GLOWING_REDSTONE_ORE)) {
			// Merge GLOWING_REDSTONE_ORE in to REDSTONE_ORE
			block.setType(Material.REDSTONE_ORE);

		}

		return block;
	}
}