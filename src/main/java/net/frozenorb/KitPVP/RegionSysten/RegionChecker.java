package net.frozenorb.KitPVP.RegionSysten;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class RegionChecker {
	WorldGuardPlugin plugin;

	public RegionChecker() {
		this.plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
	}

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

		RegionManager regionManager = plugin.getRegionManager(l.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(l);
		if (!(set.allows(r.getFlag()))) {
			return true;
		} else {
			return false;
		}

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
