package com.github.earthiverse.stats;

/* Key for the blocks destroyed and placed HashMaps */
public class Key {
	String block;
	int data;

	public Key(String block, int data) {
		this.block = block;
		this.data = data;
	}

	public String get_block() {
		return this.block;
	}

	public int get_data() {
		return this.data;
	}
}