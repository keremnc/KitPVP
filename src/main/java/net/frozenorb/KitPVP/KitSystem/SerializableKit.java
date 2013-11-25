package net.frozenorb.KitPVP.KitSystem;

import org.bukkit.inventory.ItemStack;

/**
 * Interface used to create kits from commands and json
 * 
 * @author Kerem Celik
 * 
 */
public interface SerializableKit extends Kit {
	/**
	 * Gets the armor contents of the kit
	 * 
	 * @return armor contents
	 */
	public ItemStack[] getArmorContents();

	/**
	 * Gets the items that go with the kit
	 * 
	 * @return items
	 */
	public ItemStack[] getInventoryContents();
}
