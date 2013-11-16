package net.frozenorb.KitPVP.RegionSysten;

import org.bukkit.entity.Player;

/**
 * Class that is attached to Region enum so regions can have special abilities
 * 
 * @author Kerem
 * @since 11/15/2013
 * 
 */
public interface RegionMeta {
	/**
	 * Called when a player warps to a region
	 * 
	 * @param p
	 *            the player that warps
	 */
	public void onWarp(Player p);
}
