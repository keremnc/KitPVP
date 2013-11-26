package net.frozenorb.KitPVP.RegionSysten;

import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import org.bukkit.Location;

public class RegionChecker {

	/**
	 * Checks if the given region is applicable for the location
	 * 
	 * @param r
	 *            the region to check
	 * @param l
	 *            the location to check
	 * @return applicable?
	 */
	public boolean isRegion(Region r, Location l) {
		return RegionManager.get().hasTag(l, r.getTag());

	}

	/**
	 * Gets the region for the given location
	 * 
	 * @param l
	 *            the location to check
	 * @return region that is found, null if none
	 */
	public Region getRegion(Location l) {
		for (Region r : Region.getRegions()) {
			if (isRegion(r, l))
				return r;
		}
		return null;
	}
}
