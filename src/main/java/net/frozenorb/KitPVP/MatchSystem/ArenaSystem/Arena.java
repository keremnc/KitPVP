package net.frozenorb.KitPVP.MatchSystem.ArenaSystem;

import org.bukkit.Location;

/**
 * Arena class to represent individual 1v1 arenas
 * 
 * @since 11/8/2013
 * @author Kerem
 * 
 */
public class Arena {
	private int id;
	private Location loc1, loc2;

	public Arena(int id, Location loc1, Location loc2) {
		this.id = id;
		this.loc1 = loc1;
		this.loc2 = loc2;
	}

	public int getId() {
		return id;
	}

	public Location getFirstLocation() {
		return loc1;
	}

	public Location getSecondLocation() {
		return loc2;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Arena)
			return ((Arena) arg0).getId() == id;
		return super.equals(arg0);
	}
}
