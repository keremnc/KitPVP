package net.frozenorb.KitPVP.InventorySystem.Inventories;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.InventorySystem.PageInventory;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.MatchSystem.Queue.MatchQueue;
import net.frozenorb.KitPVP.MatchSystem.Queue.QueueType;
import net.frozenorb.KitPVP.PlayerSystem.PlayerManager;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.Utilities.Attributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class MatchTypeInventory extends PageInventory {
	private ArrayList<Loadout> maches = new ArrayList<Loadout>();
	private QueueType type;
	private final ItemStack accept;
	private final ItemStack decline;
	private static ArrayList<MatchTypeInventory> openInventories = new ArrayList<MatchTypeInventory>();

	public MatchTypeInventory(Player player, String title, QueueType type) {
		super(player, true);
		accept = Core.get().generateItem(Material.WOOL, 5, "§a§lYes", new String[] { "§9Click this to enter", "§9the Ranked Matchup queue." });
		decline = Core.get().generateItem(Material.WOOL, 14, "§c§lCancel", new String[] { "§9Click this to go back." });
		maxInvSize = 36;
		this.type = type;
		maches.addAll(Loadout.getLoadouts());
		this.title = title;
		if (type == QueueType.RANKED) {
			this.title = "Do you want to join Ranked?";
		}
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
	public void onClose(InventoryCloseEvent event) {
		if (event.getPlayer() == user) {
			openInventories.remove(this);
		}

		super.onClose(event);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getViewers().equals(inv.getViewers())) {
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				if (item.equals(accept)) {
					Loadout type = Loadout.getByName("Buffed w/ Speed II");
					MatchQueue queue = new MatchQueue((Player) event.getWhoClicked(), type, this.type);
					KitAPI.getMatchManager().addToQueue(queue);
					event.getWhoClicked().closeInventory();
					return;
				}
				if (item.equals(getBackPage())) {
					setPage(currentPage - 1);
				} else if (item.equals(getForwardsPage())) {
					setPage(currentPage + 1);
				} else {
					String name = item.getItemMeta().getDisplayName();
					name = ChatColor.stripColor(name);
					Loadout type = Loadout.getByName(name.replace("Ranked ", ""));
					if (type == null) {
						event.getWhoClicked().closeInventory();
						KitAPI.getMatchManager().handleInteract((Player) event.getWhoClicked(), item.getType(), (int) item.getDurability());
						return;
					}
					MatchQueue queue = new MatchQueue((Player) event.getWhoClicked(), type, this.type);
					KitAPI.getMatchManager().addToQueue(queue);
					event.getWhoClicked().closeInventory();

				}
			}
		}
	}

	public MatchTypeInventory loadTypes() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if (type == QueueType.RANKED) {
			inv = Bukkit.createInventory(null, 36, getTitle());
			ItemStack info = getRankedItemStack();
			int[] acc = new int[] { 10, 11, 19, 20 };
			int[] dec = new int[] { 15, 16, 24, 25 };
			for (int i = 0; i < 36; i += 1) {
				inv.setItem(i, Utilities.generateItem(PlayerManager.UNUSABLE_SLOT, " "));
			}
			for (int i : acc) {
				inv.setItem(i, accept);
			}
			for (int i : dec) {
				inv.setItem(i, decline);
			}
			Attributes att = new Attributes(info);
			att.clear();
			inv.setItem(22, att.getStack());
		} else {
			for (Loadout kit : maches) {
				ItemStack item = kit.getIcon();
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§a" + kit.getName());
				ArrayList<String> lores = new ArrayList<String>();
				lores.addAll(wrap(kit.getDescription()));
				if (KitAPI.getMatchManager().getFirstUnranked(kit, getPlayer().getName()) != null) {
					lores.add("");
					lores.add("§dThere is §e1§d player in this queue.");
				}
				meta.setLore(lores);
				item.setItemMeta(meta);
				item.setAmount(1);
				Attributes att = new Attributes(item);
				att.clear();
				items.add(att.getStack());
			}
		}
		openInventories.add(this);
		setPages(items);
		return this;
	}

	private ItemStack getRankedItemStack() {
		String data = "§dThere are §eno§d players in this queue.";
		String timeTo = "§dMatches will be selected in §e" + KitAPI.getMatchMaker().getSecondsUntilSelect() + "§d seconds.";
		int amount = 1;
		int fCheck = KitAPI.getMatchManager().getPlayersInQueue(QueueType.RANKED);
		if (fCheck != 0) {
			amount = fCheck;
			data = "§dThere is §e" + fCheck + "§d player in this queue.";
		}
		ItemStack info = Core.get().generateItem(Material.EMPTY_MAP, 0, "§b§lQueue Data", new String[] { "", data, timeTo });
		info.setAmount(amount);
		Attributes a = new Attributes(info);
		a.clear();
		return a.getStack();
	}

	public static void updateAllOpenInventories() {
		for (MatchTypeInventory mti : openInventories) {
			if (mti.type == QueueType.RANKED) {
				mti.inv.setItem(22, mti.getRankedItemStack());
				mti.getPlayer().updateInventory();
			} else {
				int val = 0;
				for (Loadout kit : mti.maches) {

					ItemStack item = kit.getIcon();
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName("§a" + kit.getName());
					ArrayList<String> lores = new ArrayList<String>();
					lores.addAll(mti.wrap(kit.getDescription()));
					if (KitAPI.getMatchManager().getFirstUnranked(kit, mti.getPlayer().getName()) != null) {
						lores.add("");
						lores.add("§dThere is §e1§d player in this queue.");
					}
					meta.setLore(lores);
					item.setItemMeta(meta);
					item.setAmount(1);
					Attributes att = new Attributes(item);
					att.clear();
					mti.inv.setItem(val, att.getStack());
					val++;

				}
			}
		}
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