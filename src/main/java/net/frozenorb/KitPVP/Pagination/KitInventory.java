package net.frozenorb.KitPVP.Pagination;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.util.Attributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitInventory extends PageInventory {
	private ArrayList<Kit> kiterate = new ArrayList<Kit>(); // haha get it? KITerate

	public KitInventory(Player player) {
		super(player, true);
		maxInvSize = 36;
		kiterate.addAll(KitPVP.getKits());
		title = "§9Kit Selection";
		ItemStack item = new ItemStack(Material.CARPET);
		item.setDurability((short) 14);
		backAPage = Core.get().generateItem(item.getType(), item.getDurability(), "§cPrevious page", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add("Goes to the previous page of kits");
			}
		});
		backAPage.setAmount(0);
		item.setDurability((short) 5);
		forwardsAPage = Core.get().generateItem(item.getType(), item.getDurability(), "§aNext page", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add("Goes to the next page of kits");
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
					Kit k = KitAPI.getKitManager().getByName(name);
					if (k == null)
						return;
					if (k.hasKit(((Player) event.getWhoClicked()))) {
						((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.NOTE_PLING, 20F, 20F);
						Bukkit.dispatchCommand((CommandSender) event.getWhoClicked(), "kit " + k.getName());
						event.getWhoClicked().closeInventory();
					} else {
						((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ARROW_HIT, 20F, 20F);
						((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You do not have access to that kit!");
					}
				}
			}
		}
	}

	public void setKits() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (final Kit kit : kiterate) {
			ItemStack item = kit.getKitIcon();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName((kit.hasKit(user) ? "§a" : "§c") + kit.getName());
			ArrayList<String> lore = new ArrayList<String>() {
				private static final long serialVersionUID = 1;
				{
					addAll(wrap(kit.getDescription()));
					add("");
					add("§6Kills: " + KitAPI.getStatManager().getLocalData(getPlayer().getName()).getPlayerKitData().get(kit).getKills());
					add("§6Deaths: " + KitAPI.getStatManager().getLocalData(getPlayer().getName()).getPlayerKitData().get(kit).getDeaths());
					add("§6Uses: " + KitAPI.getStatManager().getLocalData(getPlayer().getName()).getPlayerKitData().get(kit).getUses());
					if (kit.hasAbilityMeta()) {
						add("§6" + kit.getMetaName() + ": " + KitAPI.getStatManager().getLocalData(getPlayer().getName()).getPlayerKitData().get(kit).getAbility());
					}
				}
			};
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setAmount(1);
			Attributes att = new Attributes(item);
			att.clear();
			items.add(att.getStack());
		}
		this.setPages(items);
	}

	private List<String> wrap(String string) {
		String[] split = string.split(" ");
		string = "";
		ChatColor color = ChatColor.BLUE;
		ArrayList<String> newString = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			if (string.length() > 20 || string.endsWith(".") || string.endsWith("!")) {
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