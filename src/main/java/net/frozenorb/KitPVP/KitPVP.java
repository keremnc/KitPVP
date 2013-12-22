package net.frozenorb.KitPVP;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.frozenorb.Arcade.ArcadeAPI;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.CommandManager;
import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.KitPVP.ListenerSystem.ListenerBase;
import net.frozenorb.KitPVP.ListenerSystem.Listeners.PlayerListener;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.Minigames.KitMinigameManager;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.Reflection.ReflectionManager;
import net.frozenorb.KitPVP.StatSystem.LeaderboardUpdater;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.mBasic.EconomySystem.EconomyListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.BasicDBObject;

public class KitPVP extends JavaPlugin {
	private ReflectionManager reflectionManager;
	private CommandManager commandManager;
	private LeaderboardUpdater leaderboardUpdater;
	private static KitPVP instance; // plugin instance
	private static ArrayList<Kit> kits = new ArrayList<Kit>();

	/*
	 * ----------SINGLETON GETTER----------------
	 */
	/**
	 * Gets the instance of the plugin
	 * 
	 * @return instance
	 */
	public static KitPVP get() {
		return instance;
	}

	/*
	 * ----------GETTERS--------------
	 */
	/**
	 * Gets the HashSet of kits
	 * 
	 * @return kits
	 */
	public static ArrayList<Kit> getKits() {
		return kits;
	}

	/**
	 * Gets the instance of the command manager
	 * 
	 * @return commandManager
	 */
	public CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * Gets the LeaderboardUpdater instance
	 * 
	 * @return leaderboardupdater
	 */
	public LeaderboardUpdater getLeaderboardUpdater() {
		return leaderboardUpdater;
	}

	/**
	 * Gets the instance of the Reflection Manager
	 * 
	 * @return reflection manager
	 */
	public ReflectionManager getReflectionManager() {
		return reflectionManager;
	}

	/*
	 * ------------PLUGIN LOGIC-----------
	 */
	/**
	 * called when the plugin is enabled
	 */
	@Override
	public void onEnable() {
		for (Entity e : Bukkit.getWorlds().get(0).getEntities())
			if (!(e instanceof Player))
				e.remove();
		instance = this;
		leaderboardUpdater = new LeaderboardUpdater();
		leaderboardUpdater.runTaskTimerAsynchronously(this, 0L, 30 * 20L);
		reflectionManager = new ReflectionManager();
		commandManager = new CommandManager();
		KitAPI.init(this);
		try {
			KitAPI.getKitManager().loadKits();
			new ListenerBase().registerListeners(this, "net.frozenorb.KitPVP.ListenerSystem.Listeners");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		Bukkit.getScheduler().runTaskTimer(this, KitAPI.getBossBarManager(), 20L, 240L);

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {

			@Override
			public void run() {
				Basic.get().getEconomyManager().registerListener(new EconomyListener() {

					@Override
					public void onWithdraw(String name, double withdrawn) {}

					@Override
					public void onDeposit(String name, double deposited) {}

					@Override
					public void onBalanceChanged(String name, double newBalance) {
						if (Bukkit.getPlayerExact(name) != null) {
							KitAPI.getScoreboardManager().updateScoreboard(Bukkit.getPlayerExact(name));
						}
					}
				});
				for (Player p : Bukkit.getOnlinePlayers()) {
					System.out.println("Reloading " + p.getName());
					KitAPI.getStatManager().loadPlayerData(p.getName().toLowerCase());
					PlayerListener.get().onPlayerJoin(new PlayerJoinEvent(p, null));
				}
			}
		}, 20L);
		if (Bukkit.getPluginManager().getPlugin("mArcade") != null)
			ArcadeAPI.get().setMinigameManager(new KitMinigameManager());
		KitAPI.getKitManager().loadFromFile();
		Loadout.init();
		RegionManager.register(this);
	}

	/**
	 * Called when plugin is disabled
	 */
	@Override
	public void onDisable() {
		KitAPI.getKitManager().saveToFile();
		for (Entity e : Bukkit.getWorlds().get(0).getEntities()) {
			if (!(e instanceof Player))
				e.remove();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			KitAPI.getStatManager().getPlayerData(p.getName()).saveSync();
			KitAPI.getStatManager().getStat(p.getName()).saveStat();
		}
	}

	/*
	 * --------------DEBUG------------
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 7000; i++) {
			KitAPI.getPlayerManager().registerProfile(i + "player" + (i * 2), new GamerProfile(new BasicDBObject("test", true), i + "player" + (i * 2)));
		}
		long t = System.currentTimeMillis();
		GamerProfile profile = KitAPI.getPlayerManager().getProfile("10player20");
		profile.isObject("test");
		System.out.println("profile loading took " + (System.currentTimeMillis() - t) + "ms");
	}

}
