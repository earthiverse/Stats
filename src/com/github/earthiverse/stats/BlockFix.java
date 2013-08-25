package com.github.earthiverse.stats;

import org.bukkit.Material;
import org.bukkit.block.Block;

/*
 * Fixes block data to personal preference
 * (I don't think it's worthwhile to store what position the blocks were in, among other things)
 * 
 * Refer to http://www.minecraftwiki.net/wiki/Data_values
 */

public class BlockFix {
	public static BlockData FixData(Block block) {
		Material material = block.getType();

		String type = block.getType().toString();
		byte data = block.getData();

		if (material.equals(Material.LEAVES)) {
			// Remove Leaves Decay Data
			data = (byte) (data % 8);

		} else if (material.equals(Material.SAPLING)) {
			// Remove Sapling Growth Data
			data = (byte) (data % 8);

		} else if (material.equals(Material.LOG)) {
			// Remove Position Data
			// If the data is 12,13,14 or 15 it only has bark
			if (data < 12) {
				data = (byte) (data % 4);
			}

		} else if (material.equals(Material.GLOWING_REDSTONE_ORE)) {
			// Combine GLOWING_REDSTONE_ORE and REDSTONE_ORE
			block.setType(Material.REDSTONE_ORE);

		} else if (material.equals(Material.STONE_PLATE)
				|| material.equals(Material.WOOD_PLATE)) {
			// Remove Pressed Data
			data = (byte) 0;

		} else if (material.equals(Material.BED_BLOCK)
				|| material.equals(Material.CHEST)
				|| material.equals(Material.FURNACE)
				|| material.equals(Material.LADDER)
				|| material.equals(Material.LEVER)
				|| material.equals(Material.SIGN_POST)
				|| material.equals(Material.WALL_SIGN)
				|| material.equals(Material.IRON_DOOR_BLOCK)
				|| material.equals(Material.WOODEN_DOOR)

				|| material.equals(Material.VINE)

				|| material.equals(Material.WOOD_STAIRS)
				|| material.equals(Material.COBBLESTONE_STAIRS)
				|| material.equals(Material.BRICK_STAIRS)
				|| material.equals(Material.SMOOTH_STAIRS)
				|| material.equals(Material.NETHER_BRICK_STAIRS)
				|| material.equals(Material.SANDSTONE_STAIRS)
				|| material.equals(Material.SPRUCE_WOOD_STAIRS)
				|| material.equals(Material.BIRCH_WOOD_STAIRS)
				|| material.equals(Material.JUNGLE_WOOD_STAIRS)
				|| material.equals(Material.QUARTZ_STAIRS)

				|| material.equals(Material.TORCH)
				|| material.equals(Material.REDSTONE_TORCH_OFF)
				|| material.equals(Material.REDSTONE_TORCH_ON)) {
			// Remove Position Data
			data = (byte) 0;

		} else if (material.equals(Material.JACK_O_LANTERN)
				|| material.equals(Material.PUMPKIN)) {
			// Remove Position Data
			// If the data is 4, it has no face
			if (data != 4) {
				data = (byte) 0;
			}

		} else if (material.equals(Material.CARROT)
				|| material.equals(Material.CROPS)
				|| material.equals(Material.SOIL)
				|| material.equals(Material.POTATO)

				|| material.equals(Material.NETHER_WARTS)) {
			// Remove Growth & Wetness Data
			data = (byte) 0;

		} else if (material.equals(Material.COCOA)) {
			// Remove Position Data
			data = (byte) (data >> 2);
			data = (byte) (data << 2);
		}

		return new BlockData(type, data);
	}
}