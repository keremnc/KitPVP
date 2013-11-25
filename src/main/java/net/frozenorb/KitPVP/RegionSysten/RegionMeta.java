package net.frozenorb.KitPVP.RegionSysten;

import net.frozenorb.KitPVP.KitSystem.Kit;

import org.bukkit.entity.Player;

/**
 * Class that is attached to Region enum so regions can have special abilities
 * 
 * @author Kerem
 * @since 11/15/2013
 * 
 */
public interface RegionMeta {
	@SuppressWarnings("unchecked")
	public static Class<? super Kit>[] EMPTY_KIT_ARRAY = (Class<? super Kit>[]) new Class<?>[] {};

	/**
	 * Called when a player warps to a region
	 * 
	 * @param p
	 *            the player that warped to the region
	 */
	public void onWarp(Player p);

	/**
	 * Gets a list of usable kits in the region
	 * <p>
	 * Leave as null to allow all
	 * <p>
	 * Return an empty array to allowed none, example: <br>
	 * <code>(Class<? super Kit>[]) new Class<?>[] { };</code>
	 * 
	 * @return allowed kits
	 */
	public Class<? super Kit>[] getUsableKits();

	/**
	 * Gets a list of blocked kits in the region
	 * <p>
	 * To block a kit, return something like: <br>
	 * <code>(Class<? super Kit>[]) new Class<?>[] { PVP.class };</code>
	 * 
	 * @return blocked kits
	 */
	public Class<? super Kit>[] getBlockedKits();
}
