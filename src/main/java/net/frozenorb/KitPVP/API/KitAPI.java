package net.frozenorb.KitPVP.API;

import java.io.File;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.DataSystem.WarpDataManager;
import net.frozenorb.KitPVP.ItemSystem.ToggleableItemManager;
import net.frozenorb.KitPVP.KitSystem.KitManager;
import net.frozenorb.KitPVP.MatchSystem.MatchManager;
import net.frozenorb.KitPVP.MatchSystem.ArenaSystem.ArenaManager;
import net.frozenorb.KitPVP.PlayerSystem.PlayerManager;
import net.frozenorb.KitPVP.RegionSysten.RegionChecker;
import net.frozenorb.KitPVP.Server.ServerManager;
import net.frozenorb.KitPVP.StatSystem.StatManager;
import net.frozenorb.KitPVP.StatSystem.Elo.EloManager;
import net.frozenorb.KitPVP.VisualSystem.BossBarManager;
import net.frozenorb.KitPVP.VisualSystem.ScoreboardManager;

/**
 * API class used to access managers and instances
 * <p>
 * Do <b>NOT</b> instantiate any new managers, access from here instead
 * 
 * @author Kerem
 * @since 10/20/2013
 * 
 */
public class KitAPI {
	private static KitPVP kitpvp;
	private static PlayerManager playerManager = null;
	private static MatchManager matchManager = null;
	private static RegionChecker regionChecker = null;
	private static ScoreboardManager scoreboardManager = null;
	private static KitManager kitManager = null;
	private static StatManager statManager = null;
	private static WarpDataManager warpDataManager = null;
	private static ArenaManager arenaManager = null;
	private static ServerManager serverManager = null;
	private static EloManager eloManager = null;
	private static ToggleableItemManager itemManager = null;
	private static BossBarManager bossBarManager = null;;

	public static KitPVP getKitPVP() {
		return kitpvp;
	}

	public static KitManager getKitManager() {
		if (kitManager == null)
			kitManager = new KitManager(kitpvp, "net.frozenorb.KitPVP.KitSystem.Kits");
		return kitManager;
	}

	public static EloManager getEloManager() {
		if (eloManager == null)
			eloManager = new EloManager();
		return eloManager;
	}

	public static PlayerManager getPlayerManager() {
		if (playerManager == null)
			playerManager = new PlayerManager();
		return playerManager;
	}

	public static MatchManager getMatchManager() {
		if (matchManager == null)
			matchManager = new MatchManager();
		return matchManager;
	}

	public static RegionChecker getRegionChecker() {
		if (regionChecker == null)
			regionChecker = new RegionChecker();
		return regionChecker;
	}

	public static ScoreboardManager getScoreboardManager() {
		if (scoreboardManager == null)
			scoreboardManager = new ScoreboardManager();
		return scoreboardManager;
	}

	public static BossBarManager getBossBarManager() {
		if (bossBarManager == null)
			bossBarManager = new BossBarManager();
		return bossBarManager;
	}

	public static StatManager getStatManager() {
		if (statManager == null)
			statManager = new StatManager();
		return statManager;
	}

	public static WarpDataManager getWarpDataManager() {
		if (warpDataManager == null)
			warpDataManager = new WarpDataManager(new File(kitpvp.getDataFolder() + File.separator + "warps.json"));
		return warpDataManager;
	}

	public static ArenaManager getArenaManager() {
		if (arenaManager == null)
			arenaManager = new ArenaManager(new File(kitpvp.getDataFolder() + File.separator + "arenas.json"));
		return arenaManager;
	}

	public static ServerManager getServerManager() {
		if (serverManager == null)
			serverManager = new ServerManager(kitpvp);
		return serverManager;
	}

	public static ToggleableItemManager getItemManager() {
		if (itemManager == null)
			itemManager = new ToggleableItemManager();
		return itemManager;
	}

	public static void init(KitPVP kitpvp) {
		KitAPI.kitpvp = kitpvp;
		warpDataManager = new WarpDataManager(new File(kitpvp.getDataFolder() + File.separator + "warps.json"));
		arenaManager = new ArenaManager(new File(kitpvp.getDataFolder() + File.separator + "arenas.json"));
	}

}
