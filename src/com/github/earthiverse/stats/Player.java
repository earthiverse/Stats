package com.github.earthiverse.stats;

import java.util.HashMap;

public class Player {
	HashMap<Key, Integer> blocks_placed;
	HashMap<Key, Integer> blocks_destroyed;

	int experience;

	long last_update_time;
	long login_time;
	long logout_time;

	Player(int experience, long login_time, long logout_time) {
		this.experience = experience;
		last_update_time = 0L;
		this.login_time = login_time;
		this.logout_time = logout_time;
		blocks_placed = new HashMap<Key, Integer>();
		blocks_destroyed = new HashMap<Key, Integer>();
	}

	public Player() {
		experience = 0;
		last_update_time = 0L;
		login_time = 0L;
		logout_time = 0L;
		blocks_placed = new HashMap<Key, Integer>();
		blocks_destroyed = new HashMap<Key, Integer>();
	}

	int get_experience() {
		return experience;
	}

	void set_experience(int experience) {
		this.experience = experience;
	}

	long get_login_time() {
		return login_time;
	}

	void set_login_time(long login_time) {
		this.login_time = login_time;
	}

	long get_logout_time() {
		return logout_time;
	}

	void set_logout_time(long logout_time) {
		this.logout_time = logout_time;
	}
	
	long get_last_update_time() {
		return last_update_time;
	}
	
	void set_last_update_time(long last_update_time) {
		this.last_update_time = last_update_time;
	}
	
	HashMap<Key, Integer> get_blocks_destroyed() {
		return blocks_destroyed;
	}

	void destroy_block(String block, int data, int amount) {
		Key key = new Key(block, data);

		Integer entry = blocks_destroyed.get(key);
		if (entry == null) {
			// Make Entry
			blocks_destroyed.put(key, amount);
		} else {
			// Increase Amount
			blocks_destroyed.put(key, entry + amount);
		}
	}
	
	HashMap<Key, Integer> get_blocks_placed() {
		return blocks_placed;
	}

	void place_block(String block, int data, int amount) {
		Key key = new Key(block, data);

		Integer entry = blocks_placed.get(key);
		if (entry == null) {
			// Make Entry
			blocks_placed.put(key, amount);
		} else {
			// Increase Amount
			blocks_placed.put(key, entry + amount);
		}
	}

	void gain_experience(int exp) {
		// Only update if experience is gained
		if (exp > 0) {
			experience += exp;
		}
	}
}