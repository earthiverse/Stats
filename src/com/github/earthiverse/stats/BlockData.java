package com.github.earthiverse.stats;

public class BlockData {
	String type;
	byte data;

	public BlockData(String type, byte data) {
		this.type = type;
		this.data = data;
	}
	
	String getType() {
		return type;
	}
	
	byte getData() {
		return data;
	}
}
