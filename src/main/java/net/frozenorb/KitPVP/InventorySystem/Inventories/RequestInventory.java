package net.frozenorb.KitPVP.InventorySystem.Inventories;

import java.util.ArrayList;
import java.util.HashMap;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.InventorySystem.PageInventory;
import net.frozenorb.KitPVP.MatchSystem.Match;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.util.Attributes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public abstract class RequestInventory extends PageInventory {
	private ArrayList<Match> matches = new ArrayList<Match>();
	public static HashMap<String, RequestInventory> invs = new HashMap<String, RequestInventory>();

	public RequestInventory(Player player) {
		super(player, true);
		maxInvSize = 36;
		invs.put(player.getName(), this);
		matches.addAll(KitAPI.getMatchManager().getMatchRequestsTo(player.getName()));
		this.title = "§9Pending requests";
		ItemStack item = new ItemStack(Material.CARPET);
		item.setDurability((short) 14);
		backAPage = Core.get().generateItem(item.getType(), item.getDurability(), "§cPrevious page", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("Goes to the previous page of requests");
			}
		});
		backAPage.setAmount(0);
		item.setDurability((short) 5);
		forwardsAPage = Core.get().generateItem(item.getType(), item.getDurability(), "§aNext page", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("Goes to the next page of requests");
			}
		});
		forwardsAPage.setAmount(0);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		invs.remove(e.getPlayer().getName());
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
					if (item.getType() == Material.SKULL_ITEM) {
						String title = ChatColor.stripColor(item.getItemMeta().getDisplayName().split(" ")[0]);
						Match m = KitAPI.getMatchManager().getInvitedMatch(((Player) event.getWhoClicked()).getName(), title);
						boolean matchIsTaken = KitAPI.getMatchManager().isInMatch(m.getChallenger().getName()) && KitAPI.getMatchManager().getCurrentMatches().get(m.getChallenger().getName()).isInProgress();
						if (m == null || matchIsTaken) {
							update();
							return;
						}
						if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT)
							onDecline(m);
						else
							onAccept(m);
					}
				}
			}
		}
	}

	public void update() {
		invs.put(getPlayer().getName(), this);
		inv.setContents(getItems().toArray(new ItemStack[] {}));
	}

	public RequestInventory loadTypes() {
		invs.put(getPlayer().getName(), this);
		matches.clear();
		matches.addAll(KitAPI.getMatchManager().getMatchRequestsTo(getPlayer().getName()));
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Match match : matches) {
			ItemStack item = new ItemStack(Material.SKULL_ITEM);
			item.setDurability((short) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setLore(match.getMetadata(true, true));
			meta.setDisplayName(("§a§l" + match.getChallenger().getDisplayName() + "§e (" + KitAPI.getEloManager().getElo(match.getChallenger().getName()) + ")"));
			item.setItemMeta(meta);
			item.setAmount(1);
			Attributes att = new Attributes(item);
			att.clear();
			items.add(att.getStack());
		}
		setPages(items);
		return this;
	}

	public ArrayList<ItemStack> getItems() {
		matches.clear();
		matches.addAll(KitAPI.getMatchManager().getMatchRequestsTo(getPlayer().getName()));
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Match match : matches) {
			ItemStack item = new ItemStack(Material.SKULL_ITEM);
			item.setDurability((short) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setLore(match.getMetadata(true, true));
			meta.setDisplayName(("§a§l" + match.getChallenger().getDisplayName() + "§e (" + KitAPI.getEloManager().getElo(match.getChallenger().getName()) + ")"));
			item.setItemMeta(meta);
			item.setAmount(1);
			Attributes att = new Attributes(item);
			att.clear();
			items.add(att.getStack());
		}
		return items;
	}

	public abstract void onAccept(Match m);

	public abstract void onDecline(Match m);

}