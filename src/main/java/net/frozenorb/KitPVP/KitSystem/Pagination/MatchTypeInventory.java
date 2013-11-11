package net.frozenorb.KitPVP.KitSystem.Pagination;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.MatchSystem.MatchTypes.Loadout;
import net.frozenorb.KitPVP.MatchSystem.Queue.MatchQueue;
import net.frozenorb.KitPVP.MatchSystem.Queue.QueueType;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.util.Attributes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MatchTypeInventory extends PageInventory {
	private ArrayList<Loadout> maches = new ArrayList<Loadout>();
	private QueueType type;

	public MatchTypeInventory(Player player, String title, QueueType type) {
		super(player, true);
		maxInvSize = 36;
		this.type = type;
		maches.addAll(Loadout.getLoadouts());
		this.title = title;
		ItemStack item = new ItemStack(Material.CARPET);
		item.setDurability((short) 14);
		backAPage = Core.get().generateItem(item.getType(), item.getDurability(), "§cPrevious page", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add("Goes to the previous page of match types");
			}
		});
		backAPage.setAmount(0);
		item.setDurability((short) 5);
		forwardsAPage = Core.get().generateItem(item.getType(), item.getDurability(), "§aNext page", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add("Goes to the next page of match types");
			}
		});
		forwardsAPage.setAmount(0);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getViewers().equals(inv.getViewers())) {
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				if (item.equals(getBackPage())) {
					setPage(currentPage - 1);
				} else if (item.equals(getForwardsPage())) {
					setPage(currentPage + 1);
				} else {
					String name = item.getItemMeta().getDisplayName();
					name = ChatColor.stripColor(name);
					Loadout type = Loadout.getByName(name.replace("Ranked ", ""));
					MatchQueue queue = new MatchQueue((Player) event.getWhoClicked(), type, this.type);
					KitAPI.getMatchManager().addToQueue(queue);
				}
			}
		}
	}

	public MatchTypeInventory loadTypes() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Loadout kit : maches) {
			ItemStack item = kit.getIcon();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName((type == QueueType.RANKED ? "§a§lRanked " : "§a") + kit.getName());
			meta.setLore(wrap(kit.getDescription()));
			item.setItemMeta(meta);
			item.setAmount(1);
			Attributes att = new Attributes(item);
			att.clear();
			items.add(att.getStack());
		}
		this.setPages(items);
		return this;
	}

	private List<String> wrap(String string) {
		String[] split = string.split(" ");
		string = "";
		ChatColor color = ChatColor.BLUE;
		ArrayList<String> newString = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			if (string.length() > 26 || string.endsWith(".") || string.endsWith("!")) {
				newString.add(color + string);
				if (string.endsWith(".") || string.endsWith("!"))
					newString.add("");
				string = "";
			}
			string += (string.length() == 0 ? "" : " ") + split[i];
		}
		newString.add(color + string);
		return newString;
	}

}