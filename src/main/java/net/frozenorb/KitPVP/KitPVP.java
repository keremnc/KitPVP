package net.frozenorb.KitPVP;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.KitPVP.ListenerSystem.ListenerBase;
import net.frozenorb.KitPVP.ListenerSystem.Listeners.PlayerListener;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.Reflection.CommandManager;
import net.frozenorb.KitPVP.Reflection.ReflectionManager;
import net.frozenorb.KitPVP.StatSystem.LeaderboardUpdater;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
		for (Entity e : Bukkit.getWorlds().get(0).getEntities()) {
			if (!(e instanceof Player))
				e.remove();
		}
		instance = this;
		leaderboardUpdater = new LeaderboardUpdater();
		leaderboardUpdater.runTaskTimerAsynchronously(this, 0L, 30 * 20L);
		reflectionManager = new ReflectionManager();
		commandManager = new CommandManager();
		KitAPI.getStatManager().loadLocalData();
		KitAPI.init(this);
		try {
			KitAPI.getKitManager().loadKits();
			new ListenerBase().registerListeners(this, "net.frozenorb.KitPVP.ListenerSystem.Listeners");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		Bukkit.getScheduler().runTaskTimer(this, KitAPI.getScoreboardManager(), 20L, 20L);
		/*
		 * We do the following to fix the server after a restart
		 */
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {

			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					System.out.println("Reloading " + p.getName());
					PlayerListener.get().onPlayerJoin(new PlayerJoinEvent(p, null));
				}
			}
		}, 20L);
		KitAPI.getKitManager().loadFromFile();
		Loadout.init();

	}

	/**
	 * called when plugin is disabled
	 */
	@Override
	public void onDisable() {
		KitAPI.getKitManager().saveToFile();
		for (Entity e : Bukkit.getWorlds().get(0).getEntities()) {
			if (!(e instanceof Player))
				e.remove();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			KitAPI.getStatManager().getLocalData(p.getName()).saveToFile();
			KitAPI.getStatManager().getStat(p.getName()).saveStat();
		}
	}

	/*
	 * ------------KIT REGISTRATION----------
	 */
	/**
	 * Registers a kit
	 * 
	 * @param k
	 *            the kit to register
	 * @return whether registration was a duplicate one
	 */
	public boolean registerKit(Kit k) {
		boolean done = kits.add(k);
		if (k.getListener() != null && done)
			Bukkit.getPluginManager().registerEvents(k.getListener(), this);
		return done;
	}
}
