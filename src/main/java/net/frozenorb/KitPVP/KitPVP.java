package net.frozenorb.KitPVP;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.KitPVP.ListenerSystem.ListenerBase;
import net.frozenorb.KitPVP.ListenerSystem.Listeners.PlayerListener;
import net.frozenorb.KitPVP.MatchSystem.MatchTypes.Loadout;
import net.frozenorb.KitPVP.Reflection.CommandManager;
import net.frozenorb.KitPVP.Reflection.ReflectionManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KitPVP extends JavaPlugin {
	private ReflectionManager reflectionManager;
	private CommandManager commandManager;
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
		instance = this;
		this.reflectionManager = new ReflectionManager();
		this.commandManager = new CommandManager();
		KitAPI.getStatManager().loadLocalData();
		KitAPI.init(this);
		Bukkit.getScheduler().runTaskTimer(this, KitAPI.getScoreboardManager(), 100L, 20L);
		try {
			KitAPI.getKitManager().loadKits();
			new ListenerBase().registerListeners(this, "net.frozenorb.KitPVP.ListenerSystem.Listeners");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
		}
		/*
		 * We do the following to fix the server after a restart
		 */
		for (Player p : Bukkit.getOnlinePlayers()) {
			System.out.println("Reloading " + p.getName());
			PlayerListener.get().onPlayerJoin(new PlayerJoinEvent(p, null));
		}
		Loadout.init();

	}

	/**
	 * called when plugin is disabled
	 */
	@Override
	public void onDisable() {
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
