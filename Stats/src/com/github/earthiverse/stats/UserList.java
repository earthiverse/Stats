package com.github.earthiverse.stats;

import java.util.HashMap;
import java.util.Map.Entry;

public class UserList {
	public HashMap<String, User> userlist = new HashMap<String, User>();

	/** User Cache Structure **/
	public class User {
		// For the following, String should be "BlockName,BlockData".
		// BlockDestroy Cache
		public HashMap<String, Integer> BlocksDestroyed = new HashMap<String, Integer>();
		// BlockPlace Cache
		public HashMap<String, Integer> BlocksPlaced = new HashMap<String, Integer>();
		
		// Experience Gained Cache
		public int ExpGained;
		
		// Login/Logout Time Cache
		public long Login;
		public long Logout;
	}

	/** Check Username **/
	public void checkUser(String username) {
		// Check if username is in Cache
		if (!this.userlist.containsKey(username)) {
			// If not, add user
			this.userlist.put(username, new User());
		}
	}
	
	public void addEntry(HashMap<String, Integer> hash, String player, String stat, int data, int amount) {
		// This is what we're using as the "String" key for now,
		// "<Block_Name>,<Block_Data>"
		String key = stat + "," + data;
		
		if(!hash.containsKey(key)) {
			// Add initial amount
			hash.put(key, amount);
		} else {
			// Increase amount
			hash.put(key, hash.get(key) + amount);
		}
	}

	// TODO: Remove after we've debugged enough
	public String printInfo() {
		String return_string = "\n\nEntries:";
		for (Entry<String, User> user : userlist.entrySet()) {
			return_string = return_string + "\n User = " + user.getKey();
			for (Entry<String, Integer> block: user.getValue().BlocksDestroyed.entrySet()) {
				return_string = return_string + "\n  Block = " + block.getKey() + ", # Destroyed = " + block.getValue();
			}
		}
		return return_string + "\n";
	}
}