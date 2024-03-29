package net.frozenorb.KitPVP.ListenerSystem.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.CommandManager;
import net.frozenorb.KitPVP.Commands.Debug;
import net.frozenorb.KitPVP.Events.PlayerKitSelectEvent;
import net.frozenorb.KitPVP.KitSystem.Data.PlayerKitData;
import net.frozenorb.KitPVP.ListenerSystem.ListenerBase;
import net.frozenorb.KitPVP.MatchSystem.Match;
import net.frozenorb.KitPVP.MatchSystem.MatchFinishReason;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.PlayerSystem.PlayerManager;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.KitPVP.Server.ServerManager;
import net.frozenorb.KitPVP.StatSystem.PlayerData;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatManager;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.KitPVP.StatSystem.Elo.EloManager;
import net.frozenorb.KitPVP.Types.CombatLogRunnable;
import net.frozenorb.Network.Events.SoundSendPacketEvent;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.mShared.API.Events.PlayerProfileLoadEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.mongodb.BasicDBObject;

@SuppressWarnings("deprecation")
public class PlayerListener extends ListenerBase {
	public static boolean CHAT_MUTED = false;
	private static PlayerListener instance;
	private static int COMBAT_LOG_TIME = 8;
	private static HashMap<String, HashMap<String, Double>> assist = new HashMap<String, HashMap<String, Double>>();
	private static Map<String, Double> totalDamage = new HashMap<String, Double>();
	private HashMap<String, String> combatLog = new HashMap<String, String>();
	private HashMap<String, CombatLogRunnable> combatLogRunnables = new HashMap<String, CombatLogRunnable>();

	public PlayerListener() {
		instance = this;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		GamerProfile profile = KitAPI.getPlayerManager().getProfile(e.getPlayer().getName());
		if (profile.isObject("build")) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == PlayerManager.UNUSABLE_SLOT) {
			e.setCancelled(true);
			((Player) e.getWhoClicked()).updateInventory();

		}
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent e) {
		if ((e.getBlock().getType() == Material.RED_MUSHROOM || e.getBlock().getType() == Material.BROWN_MUSHROOM) && e.getPlayer().getInventory().firstEmpty() != -1) {
			final Material mat = e.getBlock().getType();
			if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, e.getBlock().getLocation())) {
				e.getPlayer().getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
				e.getBlock().setType(Material.AIR);
				Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

					@Override
					public void run() {
						e.getBlock().setType(mat);
					}
				}, 300L);
				return;
			}
		}
		if ((e.getBlock().getType() == Material.RED_MUSHROOM || e.getBlock().getType() == Material.BROWN_MUSHROOM) && e.getPlayer().getInventory().firstEmpty() == -1) {
			if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, e.getBlock().getLocation())) {
				e.setCancelled(true);
			}
		}
		GamerProfile profile = KitAPI.getPlayerManager().getProfile(e.getPlayer().getName());
		if (profile.isObject("build")) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		PlayerManager pManager = KitAPI.getPlayerManager();
		ServerManager sManager = KitAPI.getServerManager();
		StatManager statManager = KitAPI.getStatManager();
		e.setJoinMessage(null);
		pManager.registerProfile(e.getPlayer().getName().toLowerCase(), new GamerProfile(new BasicDBObject(), e.getPlayer().getName()));
		pManager.giveSpawnProtection(e.getPlayer());
		e.getPlayer().teleport(sManager.getSpawn());
		pManager.registerProfile(e.getPlayer().getName().toLowerCase(), new GamerProfile(new BasicDBObject(), e.getPlayer().getName()));
		if (statManager.getPlayerData(e.getPlayer().getName()) == null) {
			statManager.setPlayerData(e.getPlayer().getName().toLowerCase(), new PlayerData(e.getPlayer().getName().toLowerCase(), new BasicDBObject("elo", EloManager.STARTING_ELO).append("kitData", new PlayerKitData())));
		}
		KitAPI.getStatManager().loadStats(e.getPlayer().getName());
		KitAPI.getScoreboardManager().loadScoreboard(e.getPlayer());
		if (pManager.isInventoryEmpty(e.getPlayer())) {
			sManager.addSpawnItems(e.getPlayer());
		}

	}

	@EventHandler
	public void onPlayerProfileLoad(PlayerProfileLoadEvent e) {
		KitAPI.getStatManager().loadStats(e.getName());
	}

	@EventHandler
	public void onServerSendSound(SoundSendPacketEvent e) {}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		long now = System.currentTimeMillis();
		Player p = e.getPlayer();
		KitAPI.getBossBarManager().unregisterPlayer(p);
		if (KitAPI.getMatchManager().isInMatch(p.getName())) {
			Match m = KitAPI.getMatchManager().getCurrentMatches().get(p.getName());
			if (m.isInProgress()) {
				m.finish(p, p.getName(), MatchFinishReason.PLAYER_LOGOUT);
			}
		}
		if (this.combatLogRunnables.containsKey(p.getName()) && !KitAPI.getPlayerManager().hasSpawnProtection(p)) {

			p.setHealth(0.0D);

		}

		e.setQuitMessage(null);
		if (KitAPI.getServerManager().isClearOnLogout(e.getPlayer().getName())) {
			e.getPlayer().getInventory().clear();
			e.getPlayer().getInventory().setArmorContents(null);
			Core.get().clearPlayer(e.getPlayer());
			KitAPI.getServerManager().removeLogout(e.getPlayer().getName());
		}
		if (KitAPI.getStatManager().getPlayerData(e.getPlayer().getName()) != null) {
			KitAPI.getStatManager().getPlayerData(e.getPlayer().getName()).save();
		}
		KitAPI.getStatManager().getStat(e.getPlayer().getName()).saveStat();
		Debug.handleTiming(now, "playerlistener quit");
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		if (CHAT_MUTED && !e.getPlayer().hasPermission("kitpvp.muted.chat")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "Chat is currently muted!");
		}
	}

	@EventHandler
	public void onPlayerTrampleCrop(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL)
			if (e.getClickedBlock().getType() == Material.SOIL)
				e.setCancelled(true);
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		KitAPI.getStatManager().loadPlayerData(e.getName().toLowerCase());
	}

	@EventHandler
	public void onKitSelect(PlayerKitSelectEvent e) {
		GamerProfile prof = KitAPI.getPlayerManager().getProfile(e.getPlayer().getName().toLowerCase());
		prof.setLastUsedKit(e.getKit());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(final PlayerDeathEvent e) {
		long now = System.currentTimeMillis();
		e.setDeathMessage(null);
		e.setDroppedExp(0);
		// if (ArcadeAPI.get().getMinigameManager().isGameInProgress() && ArcadeAPI.get().getMinigameManager().getCurrentMinigame().getPlayers().contains(e.getEntity().getName())) {
		// e.getDrops().clear();
		// e.getEntity().setHealth(20D);
		// ArcadeAPI.get().getMinigameManager().getCurrentMinigame().handleDeath(e.getEntity());
		// return;
		// }
		if (KitAPI.getPlayerManager().getProfile(e.getEntity().getName()).getLastUsedKit() != null)
			KitAPI.getStatManager().getPlayerData(e.getEntity().getName()).getPlayerKitData().get(KitAPI.getPlayerManager().getProfile(e.getEntity().getName()).getLastUsedKit()).incrementDeaths(1);
		if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, e.getEntity().getLocation())) {
			e.getDrops().clear();
			for (int i = 0; i < 9; i += 1)
				if (e.getEntity().getInventory().getItem(i) != null && e.getEntity().getInventory().getItem(i).getType() == Material.MUSHROOM_SOUP) {
					final Item ite = e.getEntity().getLocation().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.MUSHROOM_SOUP));
					Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

						@Override
						public void run() {
							if (ite.isValid() && !ite.isDead())
								ite.remove();
						};
					}, 60L);
				}
			Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

				@Override
				public void run() {
					KitAPI.getPlayerManager().teleport(e.getEntity(), CommandManager.EARLY_HG_LOCATION);
				}
			}, 5L);
		} else {
			e.getDrops().clear();
		}
		final Player p = e.getEntity();
		KitAPI.getServerManager().handleRespawn(p);
		Stat pStat = KitAPI.getStatManager().getStat(p.getName());
		pStat.increment(StatObjective.DEATHS, 1);
		int ks = pStat.get(StatObjective.KILLSTREAK);
		if (ks > 9) {
			Bukkit.broadcastMessage(p.getKiller().getDisplayName() + ChatColor.DARK_AQUA + " has ended " + ChatColor.GRAY + p.getDisplayName() + "'s" + ChatColor.DARK_AQUA + " killstreak of " + ChatColor.YELLOW + ks + ChatColor.DARK_AQUA + ".");
		}

		pStat.set(StatObjective.KILLSTREAK, 0);

		int value = new Random().nextInt(33 - 21) + 21;
		if (assist.containsKey(p.getName())) {
			HashMap<String, Double> sub = assist.get(p.getName());
			for (String name : sub.keySet())
				if (Bukkit.getPlayerExact(name) != null) {
					Player as = Bukkit.getPlayerExact(name);
					double amount = sub.get(as.getName());
					double finalam = amount / totalDamage.get(p.getName());
					if ((p.getKiller() != null) && !p.getKiller().getName().equals(as.getName()))
						if (finalam * 100 > 13) {
							double cent = Math.floor((finalam * 100) * 100) / 100;
							int val = (int) Math.floor((finalam * value) * 100) / 100;
							Basic.get().getEconomyManager().depositPlayer(as.getName(), val);
							as.sendMessage(ChatColor.AQUA + "You received " + ChatColor.YELLOW + "" + (val) + " credits" + ChatColor.AQUA + " for doing " + ChatColor.YELLOW + cent + "%" + ChatColor.AQUA + " of the damage done to " + ChatColor.YELLOW + p.getName());
						}

				}

		}
		totalDamage.remove(p.getName());
		assist.remove(p.getName());
		if (p.getKiller() != null && p.getKiller() instanceof Player) {
			Player killer = p.getKiller();
			if (KitAPI.getPlayerManager().getProfile(killer.getName()).getLastUsedKit() != null)
				KitAPI.getStatManager().getPlayerData(killer.getName()).getPlayerKitData().get(KitAPI.getPlayerManager().getProfile(killer.getName()).getLastUsedKit()).incrementKills(1);

			KitAPI.getStatManager().checkCombo(killer);
			if (!killer.getName().equalsIgnoreCase(p.getName())) {
				Stat killerStat = KitAPI.getStatManager().getStat(killer.getName());
				killerStat.increment(StatObjective.KILLS, 1);
				killerStat.increment(StatObjective.KILLSTREAK, 1);
				int k = killerStat.get(StatObjective.KILLSTREAK);
				if (killerStat.get(StatObjective.HIGHEST_KILLSTREAK) < k) {
					killerStat.set(StatObjective.HIGHEST_KILLSTREAK, k);
				}
				if (k % 5 == 0) {
					Bukkit.broadcastMessage(p.getKiller().getDisplayName() + ChatColor.DARK_AQUA + " has gotten a killstreak of " + ChatColor.YELLOW + k + ChatColor.DARK_AQUA + ".");
				}
			}
			if (!KitAPI.getMatchManager().isInMatch(p.getName())) {
				killer.sendMessage(ChatColor.AQUA + "You have receieved " + ChatColor.YELLOW + value + " credits " + ChatColor.AQUA + "for killing " + e.getEntity().getName() + "!");
				p.sendMessage(ChatColor.AQUA + "You were killed by " + ChatColor.YELLOW + killer.getName() + ChatColor.AQUA + " who had §e" + KitAPI.getServerManager().getSoupsInHotbar(killer) + " soups§b and §e" + KitAPI.getServerManager().getHearts(killer) + "§b hearts left.");
			}
		}
		Debug.handleTiming(now, "player death");

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(final PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, p.getLocation())) {
			Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

				@Override
				public void run() {
					if (e.getItemDrop().isValid() && !e.getItemDrop().isDead())
						e.getItemDrop().remove();
				}
			}, 60L);
			return;
		}
		ItemStack item = e.getItemDrop().getItemStack();
		if (item.getType() == Material.BOWL) {
			e.getItemDrop().remove();
			return;
		}
		if (KitAPI.getMatchManager().getMatchItems().contains(item.getType()) || item.getType() == PlayerManager.UNUSABLE_SLOT) {
			e.setCancelled(true);
			return;
		}
		if (KitAPI.getMatchManager().isInMatch(p.getName()) && KitAPI.getMatchManager().getCurrentMatches().get(p.getName()).isInProgress()) {
			e.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You can't drop this in a match.");
			return;
		}
		if (item.getType() == Material.ENCHANTED_BOOK || item.getType() == Material.EMERALD || item.getType() == Material.FEATHER || item.getType() == Material.BLAZE_POWDER || item.getType() == Material.WATCH || item.getType().toString().toLowerCase().contains("sword") || item.getType().equals((Material.BOW)) || item.getType().equals((Material.FISHING_ROD)) || item.getType().equals((Material.NETHER_STAR)) || item.getType().equals((Material.TRIPWIRE_HOOK)) || item.getType().equals((Material.BLAZE_ROD))) {
			p.sendMessage(ChatColor.RED + "You can only drop this by using '/drop'.");
			e.setCancelled(true);
		} else {
			e.setCancelled(false);
			e.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, p.getLocation())) {
				KitAPI.getPlayerManager().removeSpawnProtection(p);
			}
			((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0);

			if (KitAPI.getPlayerManager().hasSpawnProtection(p)) {
				e.setCancelled(true);
				if (e instanceof EntityDamageByEntityEvent) {
					if (((EntityDamageByEntityEvent) e).getDamager().getType() == EntityType.SNOWBALL) {
						Player damager = (Player) ((Snowball) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
						damager.sendMessage(ChatColor.RED + "That player has spawn protection.");
					}
					if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
						((Player) ((EntityDamageByEntityEvent) e).getDamager()).sendMessage(ChatColor.RED + "That player has spawn protection.");
					}
					if (((EntityDamageByEntityEvent) e).getDamager() instanceof Arrow) {
						((Player) ((Arrow) ((EntityDamageByEntityEvent) e).getDamager()).getShooter()).sendMessage(ChatColor.RED + "That player has spawn protection.");
					}
				}
			}
			if (e instanceof EntityDamageByEntityEvent) {
				if (((EntityDamageByEntityEvent) e).getDamager().getType() == EntityType.SNOWBALL) {
					if (((Snowball) ((EntityDamageByEntityEvent) e).getDamager()).getShooter() instanceof Player) {
						Player damager = (Player) ((Snowball) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
						if (KitAPI.getPlayerManager().hasSpawnProtection(damager) && !KitAPI.getPlayerManager().hasSpawnProtection(p)) {
							KitAPI.getPlayerManager().removeSpawnProtection(damager);
							damager.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
						}
					}
				}
				if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
					Player da = (Player) ((EntityDamageByEntityEvent) e).getDamager();
					if (KitAPI.getPlayerManager().hasSpawnProtection(da) && !KitAPI.getPlayerManager().hasSpawnProtection(p)) {
						KitAPI.getPlayerManager().removeSpawnProtection(da);
						da.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
					}
				}
				if (((EntityDamageByEntityEvent) e).getDamager() instanceof Arrow && ((Arrow) ((EntityDamageByEntityEvent) e).getDamager()).getShooter() instanceof Player) {
					Player da = (Player) ((Arrow) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
					if (KitAPI.getPlayerManager().hasSpawnProtection(da) && !KitAPI.getPlayerManager().hasSpawnProtection(p)) {
						KitAPI.getPlayerManager().removeSpawnProtection(da);
						da.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
					}
				}
			}

		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		double fromX = from.getX(), fromZ = from.getZ(), fromY = from.getY(), toX = to.getX(), toZ = to.getZ(), toY = to.getY();
		if (fromX != toX || fromZ != toZ || fromY != toY) {
			GamerProfile profile = KitAPI.getPlayerManager().getProfile(e.getPlayer().getName());
			if (profile.isObject("cancelMove")) {
				e.setTo(e.getFrom());
				return;
			}
			if (KitAPI.getPlayerManager().hasSpawnProtection(e.getPlayer())) {
				if (!(KitAPI.getRegionChecker().isRegion(Region.SPAWN, to)) && KitAPI.getRegionChecker().isRegion(Region.SPAWN, from)) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
					KitAPI.getPlayerManager().removeSpawnProtection(e.getPlayer());

				}

			}
			if (profile.getJSON().containsField("warpTask")) {
				if (e.getTo().distance(e.getFrom()) > 0.2) {
					Bukkit.getScheduler().cancelTask(profile.getJSON().getInt("warpTask"));
					profile.getJSON().remove("warpTask");
					e.getPlayer().sendMessage(ChatColor.RED + "Warp cancelled!");
				}
			}
		}

	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getTo().distance(e.getFrom()) > 3) {
			if (KitAPI.getRegionChecker().getRegion(e.getTo()) != null)
				KitAPI.getRegionChecker().getRegion(e.getTo()).getMeta().onWarp(e.getPlayer());
			if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getFrom()) && KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getTo())) {
				KitAPI.getMatchManager().getCurrentMatches().remove(e.getPlayer().getName());
			}
			if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, e.getFrom()) && KitAPI.getRegionChecker().getRegion(e.getTo()) != null && KitAPI.getRegionChecker().getRegion(e.getTo()) == Region.SPAWN) {
				Core.get().clearPlayer(e.getPlayer());
				KitAPI.getServerManager().addSpawnItems(e.getPlayer());
			}
			if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getFrom()) && KitAPI.getRegionChecker().getRegion(e.getTo()) != null && KitAPI.getRegionChecker().getRegion(e.getTo()) == Region.SPAWN) {
				Core.get().clearPlayer(e.getPlayer());
				KitAPI.getServerManager().addSpawnItems(e.getPlayer());
			}
			if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getFrom()) && !KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getTo())) {
				KitAPI.getMatchManager().getMatches().remove(e.getPlayer().getName());
			}
			if (KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getTo()) && !KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getFrom())) {
				if (!KitAPI.getPlayerManager().hasSpawnProtection(e.getPlayer())) {
					KitAPI.getPlayerManager().giveSpawnProtection(e.getPlayer());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		long now = System.currentTimeMillis();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			double h = ((Damageable) e.getPlayer()).getHealth();
			if (e.getPlayer().getItemInHand().getType() == Material.MUSHROOM_SOUP && h < 20D) {
				e.setCancelled(true);
				Player p = e.getPlayer();
				if (h + 7 > ((Damageable) p).getMaxHealth()) {
					p.setHealth(20D);
				} else {
					p.setHealth(h + 7);
				}
				e.getPlayer().getItemInHand().setType(Material.BOWL);
			} else if (e.getPlayer().getItemInHand().getType() == Material.MUSHROOM_SOUP && e.getPlayer().getFoodLevel() < 20) {
				e.setCancelled(true);
				int foodLevel = e.getPlayer().getFoodLevel();
				Player p = e.getPlayer();
				if (foodLevel + 7 > 20D) {
					p.setFoodLevel(20);
				} else {
					p.setFoodLevel(foodLevel + 7);
				}
				e.getPlayer().getItemInHand().setType(Material.BOWL);
			}

			if (e.getPlayer().getItemInHand() != null) {
				if (e.getPlayer().getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
					if (e.getPlayer().getItemInHand().hasItemMeta()) {
						if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "§lKits")) {
							KitAPI.getServerManager().openKitInventory(e.getPlayer());
						}
					}
				}
				if (e.getPlayer().getItemInHand().getType().equals(Material.WATCH)) {
					ItemStack it = e.getPlayer().getItemInHand();
					if (it.hasItemMeta() && it.getItemMeta().getDisplayName() != null) {
						String dname = it.getItemMeta().getDisplayName();
						String kitName = ChatColor.stripColor(dname.split(":")[dname.split(":").length - 1]).trim();
						e.getPlayer().chat("/kit " + kitName);
					}
				}

			}

		}
		Debug.handleTiming(now, "soup eaten");

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent e) {
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))) {
			final Player p = (Player) e.getEntity();
			if (!this.combatLog.containsKey(p.getName())) {
				this.combatLog.put(((Player) e.getEntity()).getName(), ((Player) e.getDamager()).getName());
				CombatLogRunnable c = new CombatLogRunnable(p, COMBAT_LOG_TIME) {

					@Override
					public void onFinish() {
						combatLog.remove(p.getName());
						combatLogRunnables.remove(p.getName());

					}
				};
				c.runTaskTimer(KitPVP.get(), 20L, 20L);
				this.combatLogRunnables.put(((Player) e.getEntity()).getName(), c);
			} else {
				((CombatLogRunnable) this.combatLogRunnables.get(p.getName())).setTime(COMBAT_LOG_TIME);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Location loc = e.getEntity().getLocation();
			if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, loc))
				e.setCancelled(true);
			Player damager = (Player) e.getDamager();
			Player p = (Player) e.getEntity();
			if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, loc)) {
				KitAPI.getPlayerManager().removeSpawnProtection(p);
				KitAPI.getPlayerManager().removeSpawnProtection(damager);

			}
		}
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player) {
			Location loc = e.getEntity().getLocation();
			Player damager = (Player) ((Arrow) e.getDamager()).getShooter();
			Player p = (Player) e.getEntity();
			if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, loc)) {
				if (KitAPI.getPlayerManager().hasSpawnProtection(damager)) {
					damager.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
				}
				KitAPI.getPlayerManager().removeSpawnProtection(p);
				KitAPI.getPlayerManager().removeSpawnProtection(damager);
			}
		}
		if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getEntity().getLocation())) {
			if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
				final Player victim = (Player) e.getEntity();
				final Player damager = (Player) e.getDamager();

				if (totalDamage.containsKey(victim.getName())) {
					totalDamage.put(victim.getName(), totalDamage.get(victim.getName()) + (e.getDamage()));
				} else {
					totalDamage.put(victim.getName(), (e.getDamage()));
				}

				if (assist.containsKey(victim.getName())) {
					HashMap<String, Double> sub = assist.get(victim.getName());
					if (sub.containsKey(damager.getName())) {
						sub.put(damager.getName(), sub.get(damager.getName()) + (e.getDamage()));
					} else {
						sub.put(damager.getName(), (e.getDamage()));
					}
				} else {
					HashMap<String, Double> d = new HashMap<String, Double>();
					d.put(damager.getName(), (e.getDamage()));
					assist.put(victim.getName(), d);
				}
			} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
				Player victim = (Player) e.getEntity();

				if (totalDamage.containsKey(victim.getName())) {
					totalDamage.put(victim.getName(), totalDamage.get(victim.getName()) + (e.getDamage()));
				} else {
					totalDamage.put(victim.getName(), (e.getDamage()));
				}

				Player damager = (Player) ((Projectile) e.getDamager()).getShooter();
				if (assist.containsKey(victim.getName())) {
					HashMap<String, Double> sub = assist.get(victim.getName());
					if (sub.containsKey(damager.getName())) {
						sub.put(damager.getName(), sub.get(damager.getName()) + (e.getDamage()));
					} else {
						sub.put(damager.getName(), (e.getDamage()));
					}
				} else {
					HashMap<String, Double> d = new HashMap<String, Double>();
					d.put(damager.getName(), (e.getDamage()));
					assist.put(victim.getName(), d);
				}

			}
		}
	}

	public static PlayerListener get() {
		return instance;
	}

}
