package net.frozenorb.KitPVP.MatchSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.StandardLoadout;
import net.frozenorb.KitPVP.MatchSystem.Queue.MatchList;
import net.frozenorb.KitPVP.MatchSystem.Queue.MatchQueue;
import net.frozenorb.KitPVP.MatchSystem.Queue.QueueType;
import net.frozenorb.KitPVP.Pagination.MatchTypeInventory;
import net.frozenorb.KitPVP.Pagination.RequestInventory;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class MatchManager {

	private static Material UNRANKED_MATCHUP_ITEM = Material.SLIME_BALL;
	private static Material RANKED_MATCHUP_ITEM = Material.EYE_OF_ENDER;
	private static Material QUICK_MATCHUP_ITEM = Material.INK_SACK;
	private static Material SELECT_PLAYER_ITEM = Material.BLAZE_ROD;
	private static String RANKED_MATCHUP_TITLE = ChatColor.BLUE + "Choose a Ranked Match Type!";
	private static String UNRANKED_MATCHUP_TITLE = ChatColor.BLUE + "Choose a Casual Match Type!";
	private static ItemStack UNRANKED_ITEM, RANKED_ITEM, QUICK_ITEM, SELECT_ITEM;
	private HashMap<String, Match> currentMatches = new HashMap<String, Match>();
	private MatchList matches = new MatchList();
	private HashMap<String, String> matchRequestsInProgress = new HashMap<String, String>();

	public MatchManager() {
		Bukkit.getPluginManager().registerEvents(new MatchListener(), KitAPI.getKitPVP());
		UNRANKED_ITEM = Core.get().generateItem(UNRANKED_MATCHUP_ITEM, 0, "§b§lCasual Matchup", new ArrayList<String>() {
			private static final long serialVersionUID = -3830569489943814868L;

			{
				add("§9Click to open the Casual Matchup queue.");
			}
		});
		RANKED_ITEM = Core.get().generateItem(RANKED_MATCHUP_ITEM, 0, "§b§lRanked Matchup", new ArrayList<String>() {
			private static final long serialVersionUID = -3830569489943814868L;

			{
				add("§9Click to open the Ranked Matchup queue.");
			}
		});
		QUICK_ITEM = Core.get().generateItem(QUICK_MATCHUP_ITEM, 8, "§b§lQuick Matchup", new ArrayList<String>() {
			private static final long serialVersionUID = -3830569489943814868L;

			{
				add("§9Click this to join the Quick Matchup");
				add("§9You will be inserted into a random, unranked match.");
			}
		});
		SELECT_ITEM = Core.get().generateItem(SELECT_PLAYER_ITEM, 8, "§a§l1v1 Stick", new ArrayList<String>() {
			private static final long serialVersionUID = -3830569489943814868L;
			{
				add("§9Right click a player to challenge them.");
				add("§9Left click to open up the match inventory,");
				add("    §9or find the most recent match.");
			}
		});
	}

	public ArrayList<Material> getMatchItems() {
		return new ArrayList<Material>() {
			private static final long serialVersionUID = 1L;
			{
				add(UNRANKED_MATCHUP_ITEM);
				add(RANKED_MATCHUP_ITEM);
				add(QUICK_MATCHUP_ITEM);
				add(SELECT_PLAYER_ITEM);

			}
		};
	}

	public MatchList getMatches() {
		return matches;
	}

	public boolean isInMatch(String str) {
		return currentMatches.containsKey(str);
	}

	public HashMap<String, Match> getCurrentMatches() {
		return currentMatches;
	}

	public boolean isQueued(String name) {
		for (MatchQueue queue : matches) {
			if (queue.getPlayer().getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public MatchQueue getQueue(String name) {
		for (MatchQueue queue : matches) {
			if (queue.getPlayer().getName().equalsIgnoreCase(name))
				return queue;
		}
		return null;
	}

	public String getOpponent(String str) {
		if (isInMatch(str)) {
			Match m = currentMatches.get(str);
			for (Entry<String, Match> entry : currentMatches.entrySet()) {
				if (!entry.getKey().equals(str) && entry.getValue().equals(m)) {
					return entry.getKey();
				}
			}
		}
		return null;

	}

	public void removeInvitations(String name) {
		for (Match m : currentMatches.values()) {
			if (m.getInvitedPlayer() != null && m.getInvitedPlayer().equalsIgnoreCase(name)) {
				m.setInvitedPlayer(null);
			}
		}
	}

	public Match getInvitation(String name) {

		for (Match m : currentMatches.values()) {
			if (m.getInvitedPlayer() != null && m.getInvitedPlayer().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;

	}

	public boolean hasInvitationTo(String name, String challenger) {

		for (Match m : currentMatches.values()) {
			if (m.getChallenger().getName().equalsIgnoreCase(challenger))
				if (m.getInvitedPlayer() != null && m.getInvitedPlayer().equalsIgnoreCase(name)) {
					return true;
				}
		}
		return false;

	}

	public Match getInvitedMatch(String name, String challenger) {

		for (Match m : currentMatches.values()) {
			if (m.getChallenger().getName().equalsIgnoreCase(challenger))
				if (m.getInvitedPlayer() != null && m.getInvitedPlayer().equalsIgnoreCase(name)) {
					return m;
				}
		}
		return null;

	}

	public void decline(String name, String challenger) {
		Iterator<Match> iter = currentMatches.values().iterator();
		while (iter.hasNext()) {
			Match m = iter.next();
			if (m.getChallenger().getName().equalsIgnoreCase(challenger))
				if (m.getInvitedPlayer() != null && m.getInvitedPlayer().equalsIgnoreCase(name)) {
					iter.remove();
				}
		}

	}

	public void addToQueue(MatchQueue queue) {
		String requester = queue.getPlayer().getName();
		if (queue.getQueueType() == QueueType.QUICK) {
			if (findQuickUnrankedMatch(requester) != null) {
				MatchQueue un = findQuickUnrankedMatch(requester);
				matches.remove(un);
				matchFound(queue.getPlayer(), un.getPlayer(), un.getLoadout(), queue, true);
			} else {
				matches.add(queue);
				queue.getPlayer().sendMessage(ChatColor.GREEN + "You have been added to the Quick Unranked Matchup queue!");
			}
		} else if (queue.getQueueType() == QueueType.RANKED) {
			Loadout requested = queue.getLoadout();
			if (getFirstRanked(requested, requester) != null) {
				MatchQueue un = getFirstRanked(requested, requester);
				matches.remove(un);
				matchFound(queue.getPlayer(), un.getPlayer(), un.getLoadout(), queue, true);
			} else {
				matches.add(queue);
				queue.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined the §bRanked§e Matchup with the §b" + requested.getName() + "§e loadout.");
			}
		} else if (queue.getQueueType() == QueueType.UNRANKED) {
			if (findQuickUnrankedMatch(requester) != null) {
				MatchQueue un = findQuickUnrankedMatch(requester);
				matches.remove(un);
				matchFound(queue.getPlayer(), un.getPlayer(), queue.getLoadout(), queue, true);
				return;
			}
			Loadout requested = queue.getLoadout();
			if (getFirstUnranked(requested, requester) != null) {
				MatchQueue un = getFirstUnranked(requested, requester);
				matches.remove(un);
				matchFound(queue.getPlayer(), un.getPlayer(), un.getLoadout(), queue, true);
			} else {
				matches.add(queue);
				queue.getPlayer().sendMessage(ChatColor.YELLOW + "You have joined the §bUnranked§e Matchup with the §b" + requested.getName() + "§e loadout.");
			}

		}
	}

	public void matchFound(Player p1, Player p2, Loadout loadout, final MatchQueue type, boolean create) {
		int p1Elo = KitAPI.getEloManager().getElo(p1.getName());
		int p2Elo = KitAPI.getEloManager().getElo(p2.getName());
		String p1color = KitAPI.getEloManager().getColor(p1Elo, p2Elo);
		String p2color = KitAPI.getEloManager().getColor(p2Elo, p1Elo);
		String p1Found = ChatColor.GOLD + "A match has been found against " + p2.getDisplayName() + " §a§6(" + p1color + p2Elo + "§6)!";
		String p2Found = ChatColor.GOLD + "A match has been found against " + p1.getDisplayName() + " §a§6(" + p2color + p1Elo + "§6)!";
		String header = "§7=====================================================";
		String footer = "§7=====================================================";
		String lineTwo = "§6Match Type: §a" + type.getQueueTypeName() + " §6| Loadout: §f" + loadout.getName();
		String[] msgp1 = { header, p1Found, lineTwo, footer };
		String[] msgp2 = { header, p2Found, lineTwo, footer };
		p1.sendMessage(msgp1);
		p2.sendMessage(msgp2);
		if (create)
			new Match(p1, p2, loadout) {
				{
					setRanked(type.getQueueType() == QueueType.RANKED);
					if (isRanked())
						setFirstTo(3);
					startMatch();
				}
			};

	}

	public MatchQueue getFirstRanked(Loadout type, String requester) {
		for (int i = 0; i < matches.size(); i += 1) {
			if (matches.get(i) != null)
				if (matches.get(i).getQueueType() == QueueType.RANKED)
					if (matches.get(i).getLoadout().getName().equals(type.getName()))
						if (!matches.get(i).getPlayer().getName().equalsIgnoreCase(requester))
							return matches.get(i);
		}
		return null;
	}

	public MatchQueue findQuickUnrankedMatch(String requester) {
		for (int i = 0; i < matches.size(); i += 1) {
			if (matches.get(i) != null)
				if (matches.get(i).getQueueType() != QueueType.RANKED)
					if (!matches.get(i).getPlayer().getName().equalsIgnoreCase(requester))
						return matches.get(i);
		}
		return null;
	}

	public MatchQueue getFirstUnranked(Loadout type, String requester) {
		for (int i = 0; i < matches.size(); i += 1) {
			if (matches.get(i) != null)
				if (matches.get(i).getQueueType() != QueueType.RANKED)
					if (matches.get(i).getLoadout().getName().equals(type.getName()))
						if (!matches.get(i).getPlayer().getName().equalsIgnoreCase(requester))
							return matches.get(i);
		}
		return null;
	}

	public void removePlayer(String playerName) {
		matches.remove(playerName);
	}

	public void applyArenaInventory(Player p) {
		Core.get().clearPlayer(p);
		p.setMaxHealth(20D);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.getInventory().setItem(0, QUICK_ITEM);
		p.getInventory().setItem(1, UNRANKED_ITEM);
		p.getInventory().setItem(2, RANKED_ITEM);
		p.getInventory().setItem(8, SELECT_ITEM);
		p.updateInventory();
	}

	public void handleInteract(final Player p, final Material m, final int data, final ItemStack item) {
		GamerProfile profile = KitAPI.getPlayerManager().getProfile(p.getName());
		if (m == QUICK_MATCHUP_ITEM) {
			if (item == null)
				return;
			if (data == 8) {
				item.setDurability((short) 10);
				addToQueue(new MatchQueue(p, new StandardLoadout(), QueueType.QUICK));
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("§c§lSearching for a match");
				meta.setLore(new ArrayList<String>() {
					private static final long serialVersionUID = 1L;

					{
						add("§9You are in the Quick Matchup Queue.");
						add("§9Click this to leave the Quick Matchup Queue");
					}
				});
				item.setItemMeta(meta);
				p.playSound(p.getLocation(), Sound.NOTE_PLING, 20F, 20F);
				if (profile.getJSON().containsField("periodTask")) {
					Bukkit.getScheduler().cancelTask(profile.getJSON().getInt("periodTask"));
					profile.getJSON().remove("periodTask");
				}
				int task = new BukkitRunnable() {

					@Override
					public void run() {
						if (p.getItemInHand() != null && p.getItemInHand().getType() == QUICK_MATCHUP_ITEM && ((int) p.getItemInHand().getDurability()) == 10) {
							if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().getLore() != null) {
								String display = p.getItemInHand().getItemMeta().getDisplayName();
								if (p.getItemInHand().getItemMeta().getLore().contains(ChatColor.BLUE + "You are in the Quick Matchup Queue.")) {
									ItemMeta meta = p.getItemInHand().getItemMeta();
									meta.setDisplayName(display.concat(".").replace("....", ""));
									p.getItemInHand().setItemMeta(meta);
								}
							}
						}
						if (!p.getInventory().contains(QUICK_MATCHUP_ITEM) && p.getInventory().first(QUICK_MATCHUP_ITEM) != -1 && p.getInventory().getItem(p.getInventory().first(QUICK_MATCHUP_ITEM)).getDurability() == 10) {
							cancel();
						}
					}
				}.runTaskTimer(KitAPI.getKitPVP(), 15L, 15L).getTaskId();
				profile.getJSON().put("periodTask", task);
			} else {
				if (profile.getJSON().containsField("periodTask")) {
					Bukkit.getScheduler().cancelTask(profile.getJSON().getInt("periodTask"));
					profile.getJSON().remove("periodTask");
				}
				removePlayer(p.getName());
				item.setDurability((short) 8);
				item.setItemMeta(QUICK_ITEM.getItemMeta());
				p.sendMessage(ChatColor.RED + "You have been removed from the quick matchup queue.");
				p.playSound(p.getLocation(), Sound.ARROW_HIT, 20F, 20F);
			}
			p.updateInventory();
		}
		if (m == RANKED_MATCHUP_ITEM) {
			if (profile.getJSON().containsField("periodTask")) {
				Bukkit.getScheduler().cancelTask(profile.getJSON().getInt("periodTask"));
				profile.getJSON().remove("periodTask");
			}
			new MatchTypeInventory(p, RANKED_MATCHUP_TITLE, QueueType.RANKED).loadTypes().openInventory();
		}
		if (m == UNRANKED_MATCHUP_ITEM) {
			if (profile.getJSON().containsField("periodTask")) {
				Bukkit.getScheduler().cancelTask(profile.getJSON().getInt("periodTask"));
				profile.getJSON().remove("periodTask");
			}
			new MatchTypeInventory(p, UNRANKED_MATCHUP_TITLE, QueueType.UNRANKED).loadTypes().openInventory();

		}

	}

	public void handleInteract(Player p, Material m, int data) {
		handleInteract(p, m, data, null);
	}

	public void handleRightClick(Player sender, Player reci, Loadout loadout) {

		if (hasInvitationTo(reci.getName(), sender.getName())) {
			sender.sendMessage(ChatColor.RED + "You already have a request to " + reci.getDisplayName() + "§c.");
			return;
		}
		if (isInMatch(reci.getName())) {
			Match m = currentMatches.get(reci.getName());
			if (m.isInProgress()) {
				sender.sendMessage(String.format("§c%s is currently in a match!", reci.getName()));
				return;
			}
		} else if (isInMatch(sender.getName())) {
			Match m = currentMatches.get(sender.getName());
			if (m.isInProgress()) {
				sender.sendMessage("§cYou are currently in a match!");
				return;
			}
		}
		Match mat = new Match(sender, loadout);
		mat.invitePlayer(reci);
		currentMatches.put(sender.getName(), mat);
	}

	public void openMatchAcceptScreen(final Match match, final Player who) {
		matches.remove(who.getName());
		new MatchAcceptScreen(who, match) {

			@Override
			public void onDecline(Match match) {
				decline(who.getName(), match.getOpponent(who).getName());
				matches.remove(who.getName());
				who.sendMessage(ChatColor.RED + "You have declined " + match.getOpponent(who.getName()).getName() + "'s match request.");
				match.getOpponent(who.getName()).sendMessage(ChatColor.RED + "" + who.getName() + " has declined your match request.");
			}

			@Override
			public void onAccept(Match match) {
				match.accept(who);
				matches.remove(who.getName());
				currentMatches.put(who.getName(), match);
				matchFound(who, match.getChallenger(), match.getType(), new MatchQueue(who, match.getType(), QueueType.UNRANKED), false);
				match.setRanked(false);
				match.startMatch();
			}
		};

	}

	public ArrayList<Match> getMatchRequestsTo(String name) {
		ArrayList<Match> matches = new ArrayList<Match>();
		for (Match m : currentMatches.values()) {
			if (m.getInvitedPlayer() != null && m.getInvitedPlayer().equalsIgnoreCase(name)) {
				if (!m.isInProgress()) {
					matches.add(m);
				}
			}
		}
		return matches;
	}

	private class MatchListener implements Listener {

		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent e) {
			final Player p = e.getPlayer();
			if (p.getItemInHand() != null) {
				if (p.getItemInHand().hasItemMeta()) {
					String display = p.getItemInHand().getItemMeta().getDisplayName();
					if (display != null && display.contains("Warp to the 1v1 Arena")) {
						p.chat("/1v1");
						return;
					}
				}
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, p.getLocation())) {
						int data = (int) p.getItemInHand().getDurability();
						Material m = p.getItemInHand().getType();
						if (getMatchItems().contains(m)) {
							e.setCancelled(true);
							handleInteract(p, m, data, p.getItemInHand());
						}
					}
				}
				if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (p.getItemInHand().getType() == SELECT_PLAYER_ITEM) {
						if (getMatchRequestsTo(p.getName()).size() > 0) {
							if (getMatchRequestsTo(p.getName()).size() == 1) {
								Match match = getInvitation(p.getName());
								openMatchAcceptScreen(match, p);
							} else {
								new RequestInventory(p) {
									@Override
									public void onDecline(Match match) {
										decline(p.getName(), match.getOpponent(p).getName());
										matches.remove(p.getName());
										p.sendMessage(ChatColor.RED + "You have declined " + match.getOpponent(p.getName()).getName() + "'s match request.");
										match.getOpponent(p.getName()).sendMessage(ChatColor.RED + "" + p.getName() + " has declined your match request.");
										update();
									}

									@Override
									public void onAccept(Match match) {
										match.accept(p);
										matches.remove(p.getName());
										currentMatches.put(p.getName(), match);
										matchFound(p, match.getChallenger(), match.getType(), new MatchQueue(p, match.getType(), QueueType.UNRANKED), false);
										match.setRanked(false);
										match.startMatch();
									}
								}.loadTypes().openInventory();
							}
							return;
						} else {
							p.sendMessage(ChatColor.RED + "There are no match requests that are still acceptable.");
						}
					}
				}
			}
		}

		@EventHandler
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
			if (!e.getPlayer().isOp() && isInMatch(e.getPlayer().getName()) && currentMatches.get(e.getPlayer().getName()).isInProgress()) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use commands in a match!");
			}
		}

		@EventHandler
		public void onProjectileLaunch(ProjectileLaunchEvent e) {
			if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getEntity().getLocation())) {
				e.setCancelled(true);
			}
		}

		@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
		public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
			if (e.getRightClicked() instanceof Player) {
				final Player p = (Player) e.getRightClicked();
				final Player clicker = e.getPlayer();
				if (clicker.getItemInHand() != null) {
					if (clicker.getItemInHand().getType().equals((SELECT_PLAYER_ITEM))) {
						e.setCancelled(true);

						if (isInMatch(p.getName())) {
							Match m = currentMatches.get(p.getName());
							if (m.isInProgress()) {
								clicker.sendMessage(String.format("§c%s is currently in a match!", p.getName()));
								return;
							} else if (hasInvitationTo(clicker.getName(), p.getName())) {
								openMatchAcceptScreen(m, clicker);
								return;
							}
						} else if (isInMatch(clicker.getName())) {
							Match m = currentMatches.get(clicker.getName());
							if (m.isInProgress()) {
								clicker.sendMessage("§cYou are currently in a match!");
								return;
							}
						}
						if (hasInvitationTo(p.getName(), clicker.getName())) {
							clicker.sendMessage(ChatColor.RED + "You already have a request to " + p.getDisplayName() + "§c.");
							return;
						}
						new MatchRequest(clicker, p) {
							@Override
							public void onSelect(Loadout loadout) {
								if (p.isOnline() && loadout != null) {
									handleRightClick(clicker, p, loadout);
									matchRequestsInProgress.remove(clicker.getName());
								} else {
									matchRequestsInProgress.remove(clicker.getName());
									clicker.sendMessage(ChatColor.RED + "That player has logged off.");
									clicker.closeInventory();
								}
							}
						};
						matchRequestsInProgress.put(clicker.getName(), p.getName());
					}
				}
			}
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
			if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
				Location loc = e.getEntity().getLocation();
				Player damager = (Player) e.getDamager();
				Player p = (Player) e.getEntity();
				if (isInMatch(p.getName())) {
					if (currentMatches.get(p.getName()).isInProgress()) {
						String opponentName = currentMatches.get(p.getName()).getOpponent(p).getName();
						if (!opponentName.equalsIgnoreCase(damager.getName())) {
							e.setCancelled(true);
							damager.sendMessage(ChatColor.RED + "That player is currently in a match!");
						}
					}

				}
				if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, loc)) {
					KitAPI.getPlayerManager().getSpawnProtection().remove(((Player) e.getEntity()).getName());
					KitAPI.getPlayerManager().getSpawnProtection().remove(((Player) e.getDamager()).getName());
				}
				if (isInMatch(p.getName())) {
					if (isInMatch(damager.getName())) {
						if (currentMatches.get(p.getName()).getOpponent(p).getName().equalsIgnoreCase(damager.getName())) {
							if (currentMatches.get(damager.getName()).getOpponent(damager).getName().equalsIgnoreCase(p.getName())) {
								if (!currentMatches.get(damager.getName()).isInProgress())
									e.setCancelled(true);
								return;
							}
						}
					}
				}
				if (isInMatch(p.getName())) {
					if (currentMatches.get(p.getName()).isInProgress()) {
						damager.sendMessage(ChatColor.RED + "You can only attack during a 1v1.");
						e.setCancelled(true);
					}
				}

			}
		}

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent e) {
			Player p = e.getPlayer();
			for (Entry<String, String> entry : matchRequestsInProgress.entrySet()) {
				if (entry.getValue().equals(p.getName())) {
					Player clicker = Bukkit.getPlayerExact(entry.getKey());
					if (p != null) {
						clicker.sendMessage(ChatColor.RED + "That player has logged off.");
						clicker.closeInventory();
						matchRequestsInProgress.remove(clicker.getName());
					}
				}
			}
			if (p.getInventory().first(Material.INK_SACK) != -1)
				p.getInventory().getItem(p.getInventory().first(Material.INK_SACK)).setDurability((short) 8);
			matches.remove(p.getName());
			if (isInMatch(p.getName())) {
				Match m = currentMatches.get(p.getName());
				if (m.isInProgress()) {
					m.finish(p, p.getName(), MatchFinishReason.PLAYER_LOGOUT);
				}
			}
		}

		@EventHandler
		public void onPlayerQuit(PlayerKickEvent e) {
			Player p = e.getPlayer();
			for (Entry<String, String> entry : matchRequestsInProgress.entrySet()) {
				if (entry.getValue().equals(p.getName())) {
					Player clicker = Bukkit.getPlayerExact(entry.getKey());
					if (p != null) {
						clicker.sendMessage(ChatColor.RED + "That player has logged off.");
						clicker.closeInventory();
						matchRequestsInProgress.remove(clicker.getName());
					}
				}
			}
			if (p.getInventory().first(Material.INK_SACK) != -1)
				p.getInventory().getItem(p.getInventory().first(Material.INK_SACK)).setDurability((short) 8);
			matches.remove(p.getName());
			if (isInMatch(p.getName())) {
				Match m = currentMatches.get(p.getName());
				if (m.isInProgress()) {
					m.finish(p, p.getName(), MatchFinishReason.PLAYER_LOGOUT);
				}
			}
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void onPlayerDeath(PlayerDeathEvent e) {
			final Player p = e.getEntity();
			if (isInMatch(p.getName())) {
				Match m = currentMatches.get(p.getName());
				if (m.isInProgress()) {
					m.finish(p, p.getName(), MatchFinishReason.PLAYER_DEATH);
					if (KitAPI.getServerManager().getWarpToMatch().contains(p.getName())) {
						Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

							@Override
							public void run() {
								KitAPI.getMatchManager().applyArenaInventory(p);

							}
						}, 2L);
					}
				}
			}
		}

	}
}
