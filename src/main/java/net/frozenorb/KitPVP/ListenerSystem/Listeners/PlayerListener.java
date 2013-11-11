package net.frozenorb.KitPVP.ListenerSystem.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Events.PlayerKitSelectEvent;
import net.frozenorb.KitPVP.ListenerSystem.ListenerBase;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.KitPVP.StatSystem.LocalPlayerData;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.KitPVP.StatSystem.Elo.EloManager;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.mongodb.BasicDBObject;

public class PlayerListener extends ListenerBase {
	public static boolean CHAT_MUTED = false;
	private static HashMap<String, HashMap<String, Double>> assist = new HashMap<String, HashMap<String, Double>>();
	private static Map<String, Double> totalDamage = new HashMap<String, Double>();
	private static PlayerListener instance;

	public PlayerListener() {
		instance = this;
	}

	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		KitAPI.getPlayerManager().registerProfile(e.getPlayer().getName().toLowerCase(), new GamerProfile(new BasicDBObject(), e.getPlayer().getName()));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		KitAPI.getKitManager().loadCustomKits(e.getPlayer().getName());
		KitAPI.getStatManager().loadStats(e.getPlayer().getName());
		KitAPI.getPlayerManager().registerProfile(e.getPlayer().getName().toLowerCase(), new GamerProfile(new BasicDBObject(), e.getPlayer().getName()));
		KitAPI.getScoreboardManager().loadScoreboard(e.getPlayer());
		if (KitAPI.getStatManager().getLocalData(e.getPlayer().getName()) == null) {
			KitAPI.getStatManager().setLocalData(e.getPlayer().getName().toLowerCase(), new LocalPlayerData(e.getPlayer().getName().toLowerCase(), new BasicDBObject("elo", EloManager.STARTING_ELO)));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (KitAPI.getServerManager().isClearOnLogout(e.getPlayer().getName())) {
			e.getPlayer().getInventory().clear();
			e.getPlayer().getInventory().setArmorContents(null);
			Core.get().clearPlayer(e.getPlayer());
			KitAPI.getServerManager().removeLogout(e.getPlayer().getName());
		}
		if (KitAPI.getStatManager().getLocalData(e.getPlayer().getName()) != null) {
			KitAPI.getStatManager().getLocalData(e.getPlayer().getName()).save();
		}
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		if (e.getRegainReason() != RegainReason.MAGIC && e.getRegainReason() != RegainReason.MAGIC_REGEN && e.getRegainReason() != RegainReason.REGEN)
			e.setCancelled(true);
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		if (CHAT_MUTED && !e.getPlayer().hasPermission("kitpvp.muted.chat")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "Chat is currently muted!");
		}
	}

	@EventHandler
	public void onKitSelect(PlayerKitSelectEvent e) {
		GamerProfile prof = KitAPI.getPlayerManager().getProfile(e.getPlayer().getName().toLowerCase());
		prof.setLastUsedKit(e.getKit());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		e.setDroppedExp(0);
		e.getDrops().clear();
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
							as.sendMessage(ChatColor.AQUA + "You received " + ChatColor.YELLOW + "" + (val) + " credits" + ChatColor.AQUA + " for doing " + ChatColor.YELLOW + cent + "%" + ChatColor.AQUA + " of the damage done to " + ChatColor.YELLOW + p.getName());
						}

				}

		}
		totalDamage.remove(p.getName());
		assist.remove(p.getName());
		if (p.getKiller() != null && p.getKiller() instanceof Player) {
			Player killer = p.getKiller();
			KitAPI.getStatManager().checkCombo(killer);
			if (!killer.getName().equalsIgnoreCase(p.getName())) {
				Stat killerStat = KitAPI.getStatManager().getStat(killer.getName());
				killerStat.increment(StatObjective.KILLS, 1);
				killerStat.increment(StatObjective.KILLSTREAK, 1);

			}
			if (!KitAPI.getMatchManager().isInMatch(p.getName())) {
				killer.sendMessage(ChatColor.AQUA + "You have receieved " + ChatColor.YELLOW + value + " credits " + ChatColor.AQUA + "for killing " + e.getEntity().getName() + "!");
				p.sendMessage(ChatColor.AQUA + "You were killed by " + ChatColor.YELLOW + killer.getName() + ChatColor.AQUA + " who had §e" + KitAPI.getServerManager().getSoupsInHotbar(killer) + " soups§b and §e" + KitAPI.getServerManager().getHearts(killer) + "§b hearts left.");
			}
		}
	}

	@EventHandler
	public void onent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, p.getLocation())) {
				KitAPI.getPlayerManager().getSpawnProtection().remove(p.getName());
			}
			((CraftPlayer) p).getHandle().getDataWatcher().watch(9, (byte) 0);

			if (KitAPI.getPlayerManager().getSpawnProtection().contains(p.getName())) {
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
						if (KitAPI.getPlayerManager().getSpawnProtection().contains(damager.getName()) && !KitAPI.getPlayerManager().getSpawnProtection().contains(p.getName())) {
							KitAPI.getPlayerManager().getSpawnProtection().remove(damager.getName());
							damager.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
						}
					}
				}
				if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
					Player da = (Player) ((EntityDamageByEntityEvent) e).getDamager();
					if (KitAPI.getPlayerManager().getSpawnProtection().contains(da.getName()) && !KitAPI.getPlayerManager().getSpawnProtection().contains(p.getName())) {
						KitAPI.getPlayerManager().getSpawnProtection().remove(da.getName());
						da.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
					}
				}
				if (((EntityDamageByEntityEvent) e).getDamager() instanceof Arrow) {
					Player da = (Player) ((Arrow) ((EntityDamageByEntityEvent) e).getDamager()).getShooter();
					if (KitAPI.getPlayerManager().getSpawnProtection().contains(da.getName()) && !KitAPI.getPlayerManager().getSpawnProtection().contains(p.getName())) {
						KitAPI.getPlayerManager().getSpawnProtection().remove(da.getName());
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
			if (!(KitAPI.getRegionChecker().isRegion(Region.SPAWN, to)) && KitAPI.getRegionChecker().isRegion(Region.SPAWN, from)) {

				if (KitAPI.getPlayerManager().getSpawnProtection().contains(e.getPlayer().getName())) {
					e.getPlayer().sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
				}
				KitAPI.getPlayerManager().getSpawnProtection().remove(e.getPlayer().getName());

			}
		}
		if (e.getTo().distance(e.getFrom()) > 0.2) {
			if (KitAPI.getPlayerManager().getProfile(e.getPlayer().getName().toLowerCase()).getJSON().containsField("warpTask")) {
				Bukkit.getScheduler().cancelTask(KitAPI.getPlayerManager().getProfile(e.getPlayer().getName().toLowerCase()).getJSON().getInt("warpTask"));
				KitAPI.getPlayerManager().getProfile(e.getPlayer().getName().toLowerCase()).getJSON().remove("warpTask");
				e.getPlayer().sendMessage(ChatColor.RED + "Warp cancelled!");
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getCause() == TeleportCause.END_PORTAL)
			return;
		if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getFrom()) && KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getTo())) {
			KitAPI.getMatchManager().getCurrentMatches().remove(e.getPlayer().getName());
		}
		if (KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getTo()) && !KitAPI.getRegionChecker().isRegion(Region.SPAWN, e.getFrom())) {
			if (!KitAPI.getPlayerManager().getSpawnProtection().contains(e.getPlayer().getName())) {
				KitAPI.getPlayerManager().getSpawnProtection().add(e.getPlayer().getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
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
						if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Kits")) {
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
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntDm(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Location loc = e.getEntity().getLocation();
			Player damager = (Player) e.getDamager();
			Player p = (Player) e.getEntity();
			if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, loc)) {
				KitAPI.getPlayerManager().getSpawnProtection().remove(p.getName());
				KitAPI.getPlayerManager().getSpawnProtection().remove(damager.getName());
			}
		}
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
			Location loc = e.getEntity().getLocation();
			Player damager = (Player) ((Arrow) e.getDamager()).getShooter();
			Player p = (Player) e.getEntity();
			if (!KitAPI.getRegionChecker().isRegion(Region.SPAWN, loc)) {
				if (KitAPI.getPlayerManager().getSpawnProtection().contains(damager.getName())) {
					damager.sendMessage(ChatColor.GRAY + "You no longer have spawn protection.");
				}
				KitAPI.getPlayerManager().getSpawnProtection().remove(p.getName());
				KitAPI.getPlayerManager().getSpawnProtection().remove(damager.getName());
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent e) {
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
			} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
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
