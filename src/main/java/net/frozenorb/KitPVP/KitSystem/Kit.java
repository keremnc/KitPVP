package net.frozenorb.KitPVP.KitSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

/**
 * Kit interface, in order to register a kit externally, extend {@link BaseKit}
 * 
 * @author Kerem
 * @since 10/7/2013
 * 
 */
public interface Kit {

	/**
	 * Gets the weight of the kit
	 * 
	 * @return weight
	 */
	public int getWeight();

	/**
	 * Gets the name of the kit
	 * 
	 * @return kitName
	 */
	public String getName();

	/**
	 * Gets the listener to be used during the kit
	 * 
	 * @return listener
	 */
	public Listener getListener();

	/**
	 * Gets the transformed version of the player's inventory
	 * 
	 * @param inv
	 *            player's previous inventory
	 * @return transformed inventory
	 */
	public PlayerInventory transformInventory(PlayerInventory inv);

	/**
	 * Gets the potion effects of the kit
	 * 
	 * @return potion effect array
	 */
	public PotionEffect[] getPotionEffects();

	/**
	 * Gets the permisssion for the kit
	 * 
	 * @return permission
	 */
	public String getPermission();

	/**
	 * Gets the description of the kit
	 * 
	 * @return description
	 */
	public String getDescription();

	/**
	 * Gets the ItemStack for the icon on the kit inventory
	 * 
	 * @return icon
	 */
	public ItemStack getKitIcon();

	/**
	 * Gets whether the player can use the kit or not
	 * 
	 * @param p
	 *            the player to check
	 * @return true if can use
	 */
	public boolean hasKit(Player p);

	/**
	 * Gets whether the kit has ability meta
	 * <p>
	 * Displayed in the pagination book
	 * 
	 * @return has meta
	 */
	public boolean hasAbilityMeta();

	/**
	 * Gets the name of the ability meta
	 * 
	 * @return name
	 */
	public String getMetaName();

}
