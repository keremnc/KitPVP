package net.frozenorb.KitPVP.MatchSystem;

import java.util.Random;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena;
import net.frozenorb.KitPVP.MatchSystem.MatchTypes.Loadout;
import net.frozenorb.KitPVP.Reflection.CommandManager;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

public class Match {
	private Player challenger, victim;
	private Loadout type;
	private String invitedPlayer;
	private boolean inProgress;
	private boolean ranked = false;
	private Arena arena;

	public Match(Player challenger, Player victim, Loadout type) {
		this.challenger = challenger;
		this.victim = victim;
		this.type = type;
		KitAPI.getMatchManager().getCurrentMatches().put(challenger.getName(), this);
		KitAPI.getMatchManager().getCurrentMatches().put(victim.getName(), this);

	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public Arena getArena() {
		return arena;
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

	public Match(Player challenger, Loadout type) {
		this.challenger = challenger;
		this.type = type;
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

	public void finish(final Player loser, final String loserName, MatchFinishReason reason) {
		final Player winner = getOpponent(loserName);
		loser.setHealth(20D);
		winner.hidePlayer(loser);
		loser.teleport(loser.getLocation().clone().add(0, 3, 0));
		loser.setAllowFlight(true);
		loser.setFlying(true);
		if (reason == MatchFinishReason.PLAYER_DEATH) {
			loser.sendMessage(ChatColor.GOLD + "You have lost the §a" + getType().getName() + "§6 match to " + winner.getDisplayName() + "§6. ");
			loser.sendMessage(winner.getDisplayName() + " §6had §c" + KitAPI.getServerManager().getSoupsInHotbar(winner) + " §6soups and §c" + KitAPI.getServerManager().getHearts(winner) + "§6 hearts left.");
			winner.sendMessage(ChatColor.GOLD + "You have killed §e" + loser.getDisplayName() + "§6 in a match. You had §c" + KitAPI.getServerManager().getSoupsInHotbar(winner) + " §6soups and §c" + KitAPI.getServerManager().getHearts(winner) + "§6 hearts left.");
			winner.sendMessage(loser.getDisplayName() + "§6 had§c " + KitAPI.getServerManager().getSoupsInHotbar(loser) + "§6 soups left.");
		} else if (reason == MatchFinishReason.PLAYER_LOGOUT) {
			winner.sendMessage(ChatColor.GOLD + "§c" + loserName + "§6 has logged out, so you have won the match.");
		}

		if (isRanked()) {
			int kElo = KitAPI.getEloManager().getElo(winner.getName().toLowerCase());
			int pElo = KitAPI.getEloManager().getElo(loserName.toLowerCase());
			int[] finalElo = KitAPI.getEloManager().getNewElo(kElo, pElo);
			KitAPI.getEloManager().setElo(winner.getName(), finalElo[0]);
			KitAPI.getEloManager().setElo(loserName, finalElo[1]);
			winner.sendMessage(ChatColor.GOLD + "Your new rating is §a" + KitAPI.getEloManager().getElo(winner.getName()) + " (+" + (KitAPI.getEloManager().getElo(winner.getName()) - kElo) + ")§6.");
			loser.sendMessage(ChatColor.GOLD + "Your new rating is §c" + KitAPI.getEloManager().getElo(loser.getName()) + " (-" + Math.abs(KitAPI.getEloManager().getElo(loserName) - pElo) + ")§6.");

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
				winner.showPlayer(loser);
				loser.setAllowFlight(false);
				loser.setFlying(false);
				KitAPI.getMatchManager().getCurrentMatches().remove(winner.getName());
				KitAPI.getMatchManager().getCurrentMatches().remove(loserName);
				setInProgress(false);
				KitAPI.getArenaManager().unregisterArena(arena);
				Core.get().clearPlayer(winner);
				KitAPI.getKitPVP().getCommandManager().teleport(loser, CommandManager.DUEL_LOCATION);
				KitAPI.getKitPVP().getCommandManager().teleport(winner, CommandManager.DUEL_LOCATION);
			}
		}, 60L);

	}

	/**
	 * Sends an invitation to a player
	 * 
	 * @param p
	 *            the player to invite
	 */
	public void invitePlayer(Player p) {
		invitedPlayer = p.getName();
		challenger.sendMessage(ChatColor.GOLD + "You have challenged " + ChatColor.RED + "" + p.getName() + ChatColor.GOLD + " to a 1v1. Type: " + getType().getName());
		p.sendMessage(ChatColor.GOLD + challenger.getName() + ChatColor.GOLD + " has challenged you to a 1v1! Right click them to accept. Type: " + getType().getName());
	}

	public void accept(Player invitee) {
		victim = invitee;
	}

	/**
	 * Starts the match
	 */
	public void startMatch() {
		if (victim == null) {
			return;
		}
		final Arena a = KitAPI.getArenaManager().requestArena();
		if (a == null) {
			victim.sendMessage("no arena available");
			challenger.sendMessage("no arena available");
			return;
		}
		a.getFirstLocation().getChunk().load(true);
		a.getFirstLocation().getChunk().load();
		a.getSecondLocation().getChunk().load();
		a.getSecondLocation().getChunk().load(true);
		KitAPI.getMatchManager().getMatches().remove(challenger.getName());
		KitAPI.getMatchManager().getMatches().remove(victim.getName());
		arena = a;
		victim.showPlayer(challenger);
		challenger.showPlayer(victim);
		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				victim.teleport(a.getFirstLocation());
			}
		}, 4L);
		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				challenger.teleport(a.getSecondLocation());
			}
		}, 6L);
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
		victim.showPlayer(challenger);
		challenger.showPlayer(victim);
	}
}
