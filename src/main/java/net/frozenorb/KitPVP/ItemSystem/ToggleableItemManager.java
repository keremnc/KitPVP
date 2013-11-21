package net.frozenorb.KitPVP.ItemSystem;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToggleableItemManager {
	private HashMap<String, ArrayList<ToggleableItem>> toggleables = new HashMap<String, ArrayList<ToggleableItem>>();

	public ToggleableItemManager() {

	}

	public void registerItem(ToggleableItem item, Player player) {
		ArrayList<ToggleableItem> items = new ArrayList<ToggleableItem>();
		if (toggleables.containsKey(player.getName())) {
			items = toggleables.get(player.getName());
		}
		items.add(item);
		toggleables.put(player.getName(), items);
	}

	public void unregisterPlayer(Player player) {
		if (toggleables.containsKey(player.getName()))
			toggleables.get(player.getName()).clear();
		toggleables.remove(player.getName());
	}

	public void handleLeftClick(ItemStack item, int slot, Player player) {
		if (toggleables.containsKey(player.getName()))
			for (ToggleableItem tItem : toggleables.get(player.getName())) {
				if (tItem.isThis(item)) {
					tItem.next(item, slot, player);
				}
			}
	}

	public void handleRightClick(ItemStack item, int slot, Player player) {
		if (toggleables.containsKey(player.getName()))
			for (ToggleableItem tItem : toggleables.get(player.getName())) {
				if (tItem.isThis(item)) {
					if (tItem.isSecondary())
						tItem.nextSecondary(item, slot, player);
					else
						tItem.next(item, slot, player);
				}
			}
	}
}