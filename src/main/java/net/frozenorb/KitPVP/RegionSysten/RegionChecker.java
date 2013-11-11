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

	public boolean isRegion(Region r, Location l) {

		RegionManager regionManager = plugin.getRegionManager(l.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(l);
		if (!(set.allows(r.getFlag()))) {
			return true;
		} else {
			return false;
		}

	}
}
