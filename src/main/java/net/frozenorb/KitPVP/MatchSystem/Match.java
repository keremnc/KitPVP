package net.frozenorb.KitPVP.MatchSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.CommandManager;
import net.frozenorb.KitPVP.Commands.Debug;
import net.frozenorb.KitPVP.InventorySystem.Inventories.RequestInventory;
import net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mShared.Shared;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class Match {
	private Player challenger, victim;
	private Loadout type;
	private String invitedPlayer;
	private boolean inProgress;
	private boolean ranked = false;
	private Arena arena;
	private int firstTo = 1;
	private int matchesDone = 0;
	private long matchStartTime;
	private long matchFinishTime;

	private HashMap<String, Integer> wins = new HashMap<String, Integer>();

	public Match(Player challenger, Player victim, Loadout type) {
		firstTo = type.getFirstTo();
		this.challenger = challenger;
		this.victim = victim;
		this.type = type;
		KitAPI.getMatchManager().getCurrentMatches().put(challenger.getName(), this);
		KitAPI.getMatchManager().getCurrentMatches().put(victim.getName(), this);
	}

	public HashMap<String, Integer> getWins() {
		if (getFirstTo() > 1)
			return wins;
		return null;
	}

	public Match(Player challenger, Loadout type) {
		firstTo = type.getFirstTo();
		this.challenger = challenger;
		this.type = type;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public Arena getArena() {
		return arena;
	}

	public int getFirstTo() {
		return firstTo;
	}

	public void setFirstTo(int firstTo) {
		this.firstTo = firstTo;
	}

	public Player getChallenger() {
		return challenger;
	}

	public boolean isRanked() {
		return ranked;
	}

	public Match setRanked(boolean ranked) {
		this.ranked = ranked;
		return this;
	}

	public Player getVictim() {
		return victim;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public String getInvitedPlayer() {
		return invitedPlayer;
	}

	/**
	 * Gets the {@link Loadout} of the Match
	 * 
	 * @return the type
	 */
	public Loadout getType() {
		return type != null ? type : new Loadout() {

			@Override
			public PotionEffect[] getPotionEffects() {
				return new PotionEffect[] {};
			}

			@Override
			public int getWeight() {
				return 0;
			}

			@Override
			public String getName() {
				return "Undetermined";
			}

			@Override
			public PlayerInventory applyInventory(PlayerInventory inventory) {
				return inventory;
			}

			@Override
			public String getDescription() {
				return "Who knows?";
			}
		};
	}

	/**
	 * Sets the type of the Match
	 * 
	 * @param type
	 *            the {@link Loadout} type
	 */
	public void setType(Loadout type) {
		this.type = type;
	}

	public void setInvitedPlayer(String invitedPlayer) {
		this.invitedPlayer = invitedPlayer;
	}

	public Player getOpponent(Player player) {
		if (player.getName().equalsIgnoreCase(challenger.getName())) {
			return victim;
		} else
			return challenger;
	}

	public Player getOpponent(String name) {
		if (name.equalsIgnoreCase(challenger.getName())) {
			return victim;
		} else
			return challenger;

	}

	@Override
	public String toString() {
		BasicDBObject db = new BasicDBObject().append("challenger", challenger.getName()).append("firstTo", getFirstTo()).append("inProgress", isInProgress()).append("loadout", new BasicDBObject("type", type.getName()).append("data", type.getInfo())).append("invitedPlayer", invitedPlayer != null ? invitedPlayer : "none").append("ranked", isRanked()).append("arena", arena != null ? arena.getId() : "none");
		return db.toString();
	}

	public void finish(final Player loser, final String loserName, final MatchFinishReason reason) {
		long now = System.currentTimeMillis();
		final Player winner = getOpponent(loserName);
		loser.setHealth(20D);
		final boolean logout = reason == MatchFinishReason.PLAYER_LOGOUT;
		wins.put(winner.getName(), wins.get(winner.getName()) + 1);
		if (getFirstTo() > 1 && !logout) {
			KitAPI.getBossBarManager().registerStrings(victim, new String[] { victim.getDisplayName() + "§6: §e" + wins.get(victim.getName()) + "§6 - " + challenger.getDisplayName() + "§6: §e" + wins.get(challenger.getName()), "§6KitPVP.com - §cFirst to " + getFirstTo() });
			KitAPI.getBossBarManager().registerStrings(challenger, new String[] { challenger.getDisplayName() + "§6: §e" + wins.get(challenger.getName()) + "§6 - " + victim.getDisplayName() + "§6: §e" + wins.get(victim.getName()), "§6KitPVP.com - §cFirst to " + getFirstTo() });
		}
		if (((hasFinished() && getFirstTo() > 1) || getFirstTo() == 1)) {
			matchFinishTime = System.currentTimeMillis();
			setInProgress(false);
			if (isRanked()) {
				KitAPI.getStatManager().getPlayerData(loserName).increment(StatObjective.RANKED_MATCHES_PLAYED);
				KitAPI.getStatManager().getPlayerData(winner.getName()).increment(StatObjective.RANKED_MATCHES_PLAYED);
			}
		}
		if (loser != null && loser.isOnline()) {
			KitAPI.getServerManager().setVisible(loser, false);
			loser.setNoDamageTicks(60);
			winner.setNoDamageTicks(60);
			loser.teleport(loser.getLocation().clone().add(0, 3, 0));
			loser.setAllowFlight(true);
			loser.setFlying(true);
		}
		if (reason == MatchFinishReason.PLAYER_DEATH) {
			loser.sendMessage(ChatColor.GOLD + "You have lost the §a" + getType().getName() + "§6 match to " + winner.getDisplayName() + "§6. ");
			loser.sendMessage(winner.getDisplayName() + " §6had §c" + KitAPI.getServerManager().getSoupsInHotbar(winner) + " §6" + getType().getHealType() + "s and §c" + KitAPI.getServerManager().getHearts(winner) + "§6 hearts left.");
			winner.sendMessage(ChatColor.GOLD + "You have killed §e" + loser.getDisplayName() + "§6 in a match. You had §c" + KitAPI.getServerManager().getSoupsInHotbar(winner) + " §6" + getType().getHealType() + "s and §c" + KitAPI.getServerManager().getHearts(winner) + "§6 hearts left.");
			winner.sendMessage(loser.getDisplayName() + "§6 had§c " + KitAPI.getServerManager().getSoupsInHotbar(loser) + "§6 " + getType().getHealType() + "s left.");
		} else if (reason == MatchFinishReason.PLAYER_LOGOUT) {
			Core.get().clearPlayer(loser);
			winner.sendMessage(ChatColor.GOLD + "§c" + loserName + "§6 has logged out, so you have won the match.");
			KitAPI.getArenaManager().unregisterArena(arena);
			setInProgress(false);
			KitAPI.getPlayerManager().teleport(winner, CommandManager.DUEL_LOCATION);
			KitAPI.getBossBarManager().unregisterPlayer(winner);
			KitAPI.getMatchManager().getCurrentMatches().remove(winner.getName());
			KitAPI.getMatchManager().getCurrentMatches().remove(loserName);
			BasicDBObject dbObject = constructObject(winner);
			Shared.get().getEventManager().registerNewEvent(new BasicDBObject("type", "1v1").append("when", Shared.get().getUtilities().getTime(System.currentTimeMillis())).append("data", dbObject));
			Stat s = KitAPI.getStatManager().getStat(winner.getName().toLowerCase());
			s.increment(StatObjective.DUEL_WINS);
			Stat ls = KitAPI.getStatManager().getStat(loserName.toLowerCase());
			ls.increment(StatObjective.DUEL_LOSSES);
			victim = null;
			challenger = null;
		}

		if (isRanked()) {
			if (getFirstTo() == 1) {
				int kElo = KitAPI.getEloManager().getElo(winner.getName().toLowerCase());
				int pElo = KitAPI.getEloManager().getElo(loserName.toLowerCase());
				int[] finalElo = KitAPI.getEloManager().getNewElo(kElo, pElo, KitAPI.getStatManager().getPlayerData(winner.getName()).get(StatObjective.RANKED_MATCHES_PLAYED), KitAPI.getStatManager().getPlayerData(loserName).get(StatObjective.RANKED_MATCHES_PLAYED));
				KitAPI.getEloManager().setElo(winner.getName(), finalElo[0]);
				KitAPI.getEloManager().setElo(loserName, finalElo[1]);
				winner.sendMessage(ChatColor.GOLD + "Your new rating is §a" + KitAPI.getEloManager().getElo(winner.getName()) + " (+" + (KitAPI.getEloManager().getElo(winner.getName()) - kElo) + ")§6.");
				if (loser.isOnline())
					loser.sendMessage(ChatColor.GOLD + "Your new rating is §c" + KitAPI.getEloManager().getElo(loser.getName()) + " (-" + Math.abs(KitAPI.getEloManager().getElo(loserName) - pElo) + ")§6.");
			} else if (hasFinished() && getFirstTo() > 1 && !logout) {
				int kElo = KitAPI.getEloManager().getElo(winner.getName().toLowerCase());
				int pElo = KitAPI.getEloManager().getElo(loserName.toLowerCase());
				int[] finalElo = KitAPI.getEloManager().getNewElo(kElo, pElo, KitAPI.getStatManager().getPlayerData(winner.getName()).get(StatObjective.RANKED_MATCHES_PLAYED), KitAPI.getStatManager().getPlayerData(loserName).get(StatObjective.RANKED_MATCHES_PLAYED));
				KitAPI.getEloManager().setElo(winner.getName(), finalElo[0]);
				KitAPI.getEloManager().setElo(loserName, finalElo[1]);
				winner.sendMessage(ChatColor.GOLD + "Your new rating is §a" + KitAPI.getEloManager().getElo(winner.getName()) + " (+" + (KitAPI.getEloManager().getElo(winner.getName()) - kElo) + ")§6.");
				if (loser.isOnline())
					loser.sendMessage(ChatColor.GOLD + "Your new rating is §c" + KitAPI.getEloManager().getElo(loser.getName()) + " (-" + Math.abs(KitAPI.getEloManager().getElo(loserName) - pElo) + ")§6.");
			}
		}
		if (getFirstTo() > 1 && !logout) {
			String msg = "§6Match §e" + matchesDone + "§6 has ended.";
			String first = wins.get(victim.getName()) > wins.get(challenger.getName()) ? victim.getDisplayName() : loser.getDisplayName();
			String winsMsg = "§6Stats: §f" + first + "§e(" + wins.get(ChatColor.stripColor(first)) + ")§f - " + getOpponent(ChatColor.stripColor(first)).getDisplayName() + "§e(" + wins.get(getOpponent(ChatColor.stripColor(first)).getName()) + ")";
			challenger.sendMessage(new String[] { msg, winsMsg });
			victim.sendMessage(new String[] { msg, winsMsg });
		}
		if (getFirstTo() > 1 && hasFinished() && !logout) {
			String first = wins.get(victim.getName()) > wins.get(challenger.getName()) ? victim.getDisplayName() : challenger.getDisplayName();
			String msg = first + "§6 has won the§e first to " + getFirstTo() + "§6!";
			challenger.sendMessage(new String[] { msg });
			victim.sendMessage(new String[] { msg });
		}
		getType().onDefeat(winner, loser);
		for (int i = 0; i < 7; i += 1) {
			Firework fw = (Firework) winner.getWorld().spawnEntity(winner.getLocation().clone().add(new Random().nextInt(2) - 1, 0, new Random().nextInt(2) - 1), EntityType.FIREWORK);
			Random r = new Random();
			FireworkMeta fwm = fw.getFireworkMeta();
			int rt = r.nextInt(4) + 1;
			Type type = Type.BALL;
			if (rt == 1)
				type = Type.BALL;
			if (rt == 2)
				type = Type.BALL_LARGE;
			if (rt == 3)
				type = Type.BURST;
			if (rt == 4)
				type = Type.CREEPER;
			if (rt == 5)
				type = Type.STAR;
			int r1i = r.nextInt(17) + 1;
			int r2i = r.nextInt(17) + 1;
			Color c1 = Core.get().getColor(r1i);
			Color c2 = Core.get().getColor(r2i);
			FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
			fwm.addEffect(effect);
			int rp = r.nextInt(2) + 1;
			fwm.setPower(rp);
			fw.setFireworkMeta(fwm);
		}
		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				if (loser != null && loser.isOnline()) {
					KitAPI.getServerManager().setVisible(loser, true);
					loser.setAllowFlight(false);
					loser.setFlying(false);
				}
				if (!logout)
					Core.get().clearPlayer(winner);
				if (!hasFinished() && getFirstTo() > 1 && !logout)
					startMatch();
				else {
					if (!logout) {
						setInProgress(false);
						Stat s = KitAPI.getStatManager().getStat(winner.getName().toLowerCase());
						s.increment(StatObjective.DUEL_WINS);
						Stat ls = KitAPI.getStatManager().getStat(loserName.toLowerCase());
						ls.increment(StatObjective.DUEL_LOSSES);
						KitAPI.getArenaManager().unregisterArena(arena);
						KitAPI.getPlayerManager().teleport(winner, CommandManager.DUEL_LOCATION);
						KitAPI.getBossBarManager().unregisterPlayer(winner);
						KitAPI.getMatchManager().getCurrentMatches().remove(winner.getName());
						Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

							@Override
							public void run() {
								KitAPI.getPlayerManager().teleport(loser, CommandManager.DUEL_LOCATION);

							}
						}, 5L);
						KitAPI.getBossBarManager().unregisterPlayer(loser);
						KitAPI.getMatchManager().getCurrentMatches().remove(loserName);
						BasicDBObject dbObject = constructObject(winner);
						Shared.get().getEventManager().registerNewEvent(new BasicDBObject("type", "1v1").append("when", Shared.get().getUtilities().getTime(System.currentTimeMillis())).append("data", dbObject));
						victim = null;
						challenger = null;
					}
				}
			}
		}, 60L);
		Debug.handleTiming(now, "finish match");

	}

	/**
	 * Gets the matches result, in JSON
	 * <p>
	 * Should <b>ONLY</b> be called when the match has completed.
	 * 
	 * @return JSON output of the match
	 */
	public BasicDBObject constructObject(Player winner) {
		BasicDBObject ma = new BasicDBObject();
		ma.append("challenger", challenger.getName()).append("victim", victim.getName()).append("arenaID", arena.getId());
		ma.append("winner", winner.getName());
		BasicDBObject ldt = new BasicDBObject("loadout", type.getName());
		if (type.isCustom()) {
			ldt.append("data", type.getInfo());
		}
		ma.append("loadout", ldt);
		ma.append("firstTo", firstTo);
		BasicDBList stats = new BasicDBList();
		if (getWins() != null && getFirstTo() > 1) {
			for (Entry<String, Integer> entry : getWins().entrySet())
				stats.add(new BasicDBObject(entry.getKey(), entry.getValue()));

		}
		if (getFirstTo() > 1)
			ma.append("stats", stats);
		ma.append("durationSeconds", (matchFinishTime - matchStartTime) / 1000);
		return ma;
	}

	public ArrayList<String> getMetadata(boolean acceptName, boolean showStats) {
		ArrayList<String> meta = new ArrayList<>();
		Stat s = KitAPI.getStatManager().getStat(challenger.getName());
		if (showStats) {
			meta.add("§6Kills/Deaths: §f" + s.get(StatObjective.KILLS) + "/" + s.get(StatObjective.DEATHS));
			meta.add("§61v1 Wins/Losses: §f" + s.get(StatObjective.DUEL_WINS) + "/" + s.get(StatObjective.DUEL_LOSSES));
			meta.add(" ");
		}
		meta.add("§6Match Type:§f " + type.getName());
		if (type.isCustom()) {
			meta.add("§6Refilling:§f " + type.getInfo().getString("Refilling"));
			meta.add("§6Sword:§f " + ((BasicDBObject) type.getInfo().get("Sword")).getString("name") + " " + ((BasicDBObject) type.getInfo().get("Sword")).getString("data"));
			meta.add("§6Armor:§f " + ((BasicDBObject) type.getInfo().get("Armor")).getString("name") + " " + ((BasicDBObject) type.getInfo().get("Armor")).getString("data"));
			meta.add("§6Healing Type:§f " + type.getInfo().getString("Healing Type"));
			meta.add("§6Potions:§f " + type.getInfo().getString("Potions"));
		}
		if (type.getFirstTo() > 1) {
			meta.add("§6Match Count: §f" + type.getFirstTo());
		}
		if (acceptName) {
			meta.add(" ");
			meta.add(" ");
			meta.add("§6§lLEFT-CLICK: §aAccept");
			meta.add("§6§lRIGHT-CLICK: §cDecline");
		}
		return meta;

	}

	/**
	 * Sends an invitation to a player
	 * 
	 * @param p
	 *            the player to invite
	 */
	public void invitePlayer(final Player p) {
		invitedPlayer = p.getName();
		challenger.sendMessage(ChatColor.GOLD + "You have challenged " + ChatColor.RED + "" + p.getName() + ChatColor.GOLD + " to a 1v1. Type: " + getType().getName());
		p.sendMessage(ChatColor.GOLD + challenger.getName() + ChatColor.GOLD + " has challenged you to a 1v1! Right click them to accept. Type: " + getType().getName());
		if (RequestInventory.invs.containsKey(p.getName()))
			Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

				@Override
				public void run() {
					RequestInventory.invs.get(p.getName()).update();

				}
			}, 1L);
	}

	public void accept(Player invitee) {
		victim = invitee;
	}

	public boolean hasFinished() {
		return (wins.get(victim.getName()) == getFirstTo() || wins.get(challenger.getName()) == getFirstTo());
	}

	/**
	 * Starts the match
	 */
	public void startMatch() {
		long now = System.currentTimeMillis();
		KitAPI.getMatchManager().getCurrentMatches().put(victim.getName(), this);
		KitAPI.getMatchManager().getCurrentMatches().put(challenger.getName(), this);
		matchesDone++;
		if (!wins.containsKey(victim.getName())) {
			matchStartTime = System.currentTimeMillis();
			wins.put(challenger.getName(), 0);
			wins.put(victim.getName(), 0);
		}

		if (victim == null) {
			return;
		}
		if (arena == null) {
			final Arena a = KitAPI.getArenaManager().requestArena();
			if (a == null) {
				victim.sendMessage("no arena available");
				challenger.sendMessage("no arena available");
				return;
			}
			arena = a;
		}
		KitAPI.getArenaManager().setArenaInUse(arena);
		arena.getFirstLocation().getChunk().load();
		arena.getFirstLocation().getChunk().load(true);
		arena.getSecondLocation().getChunk().load();
		arena.getSecondLocation().getChunk().load(true);
		KitAPI.getMatchManager().getMatches().remove(challenger.getName());
		KitAPI.getMatchManager().getMatches().remove(victim.getName());
		victim.closeInventory();
		challenger.closeInventory();
		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				victim.teleport(arena.getFirstLocation());
			}
		}, 3L);
		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				challenger.teleport(arena.getSecondLocation());
				if (getFirstTo() > 1) {
					KitAPI.getBossBarManager().registerStrings(victim, new String[] { victim.getDisplayName() + "§6: §e" + wins.get(victim.getName()) + "§6 - " + challenger.getDisplayName() + "§6: §e" + wins.get(challenger.getName()), "§6KitPVP.com - §cFirst to " + getFirstTo() });
					KitAPI.getBossBarManager().registerStrings(challenger, new String[] { challenger.getDisplayName() + "§6: §e" + wins.get(challenger.getName()) + "§6 - " + victim.getDisplayName() + "§6: §e" + wins.get(victim.getName()), "§6KitPVP.com - §cFirst to " + getFirstTo() });
				}
			}
		}, 6L);
		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				victim.closeInventory();
				challenger.closeInventory();
				victim.setHealth(((Damageable) victim).getMaxHealth());
				challenger.setHealth(((Damageable) challenger).getMaxHealth());
				getType().applyInventory(victim.getInventory());
				getType().applyInventory(challenger.getInventory());
				victim.getInventory().setHeldItemSlot(0);
				challenger.getInventory().setHeldItemSlot(0);
				for (PotionEffect pot : getType().getPotionEffects()) {
					victim.addPotionEffect(pot);
					challenger.addPotionEffect(pot);
				}
				PacketPlayOutNamedEntitySpawn eV = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) victim).getHandle());
				PacketPlayOutNamedEntitySpawn eC = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) challenger).getHandle());
				((CraftPlayer) victim).getHandle().playerConnection.sendPacket(eC);
				((CraftPlayer) challenger).getHandle().playerConnection.sendPacket(eV);

				challenger.showPlayer(victim);
				victim.showPlayer(challenger);

			}
		}, 8L);
		if (getFirstTo() > 1) {
			victim.sendMessage(ChatColor.GOLD + "§7===§6Match §e§l" + matchesDone + "§r§6 is starting!§7===");
			challenger.sendMessage(ChatColor.GOLD + "§7===§6Match §e§l" + matchesDone + "§r§6 is starting!§7===");
		}
		invitedPlayer = null;
		setInProgress(true);
		victim.setHealth(((Damageable) victim).getMaxHealth());
		challenger.setHealth(((Damageable) challenger).getMaxHealth());
		getType().applyInventory(victim.getInventory());
		getType().applyInventory(challenger.getInventory());
		for (PotionEffect pot : getType().getPotionEffects()) {
			victim.addPotionEffect(pot);
			challenger.addPotionEffect(pot);
		}
		KitAPI.getServerManager().freezePlayer(challenger);
		KitAPI.getServerManager().freezePlayer(victim);
		final AtomicInteger current = new AtomicInteger(0);
		new BukkitRunnable() {

			@Override
			public void run() {
				if (current.get() > 2) {
					victim.setHealth(((Damageable) victim).getMaxHealth());
					challenger.setHealth(((Damageable) challenger).getMaxHealth());
					victim.showPlayer(challenger);
					challenger.showPlayer(victim);
					KitAPI.getServerManager().unfreezePlayer(challenger);
					KitAPI.getServerManager().unfreezePlayer(victim);
					challenger.sendMessage(ChatColor.GREEN + "GO!");
					victim.sendMessage(ChatColor.GREEN + "GO!");
					cancel();
				} else {
					challenger.sendMessage(ChatColor.RED + "" + (3 - current.get()) + "...");
					victim.sendMessage(ChatColor.RED + "" + (3 - current.get()) + "...");
					current.set(current.get() + 1);
				}

			}
		}.runTaskTimer(KitPVP.get(), 0L, 20L);
		victim.showPlayer(challenger);
		challenger.showPlayer(victim);
		Debug.handleTiming(now, "Match#startMatch()");
	}
}
