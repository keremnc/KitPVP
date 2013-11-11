package net.frozenorb.KitPVP.StatSystem;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.Types.Attack;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mShared.Shared;
import net.frozenorb.mShared.API.Profile.PlayerProfile;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class StatManager {
	private HashMap<String, Stat> stats = new HashMap<String, Stat>();
	private HashMap<String, LocalPlayerData> playerData = new HashMap<String, LocalPlayerData>();

	/**
	 * Get the Stat object of the name
	 * 
	 * @param name
	 *            the name of the player
	 * @return the Stat object
	 */
	public Stat getStat(String name) {
		if (stats.containsKey(name.toLowerCase()))
			return stats.get(name.toLowerCase());
		return null;
	}

	/**
	 * Gets the local data of the name
	 * 
	 * @param name
	 *            the name of the player
	 * @return local data
	 */
	public LocalPlayerData getLocalData(String name) {
		if (playerData.containsKey(name.toLowerCase()))
			return playerData.get(name.toLowerCase());
		return null;
	}

	/**
	 * Sets the name's data
	 * 
	 * @param name
	 *            the name of the player
	 * @param data
	 *            the data to set to
	 */
	public void setLocalData(String name, LocalPlayerData data) {
		playerData.put(name.toLowerCase(), data);
		data.save();
	}

	/**
	 * Loads local playerData
	 */
	public void loadLocalData() {

		File dir = new File("data" + File.separator + "playerData");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		FilenameFilter statsFilter = new FilenameFilter() {
			public boolean accept(File paramFile, String paramString) {
				if (paramString.endsWith("json"))
					return true;
				return false;
			}
		};
		for (File playerFile : dir.listFiles(statsFilter)) {
			String str = Core.get().readFile(playerFile);
			if (JSON.parse(str) != null) {
				System.out.println("Loading file " + playerFile.getName());
				BasicDBObject db = (BasicDBObject) JSON.parse(str);
				LocalPlayerData data = new LocalPlayerData(playerFile.getName().substring(0, playerFile.getName().indexOf('.')), db);
				playerData.put(playerFile.getName().substring(0, playerFile.getName().indexOf('.')), data);
				System.out.println("Successfully loaded " + playerFile.getName());
			} else
				System.out.println("ERROR LOADING PLAYER DATA FILE: " + playerFile.getName());

		}

	}

	/**
	 * Loads the stats of the name
	 * 
	 * @param name
	 *            the name of the player
	 */
	public void loadStats(String name) {
		PlayerProfile pp = Shared.get().getProfileManager().getProfile(name);
		if (pp != null) {
			BasicDBObject object = pp.getJson();
			Stat stat = new Stat(((BasicDBObject) (object.get("user"))).getString("name"), new BasicDBObject());
			if (((BasicDBObject) (object.get("user"))).containsField("stats")) {
				stat = new Stat(((BasicDBObject) (object.get("user"))).getString("name"), (BasicDBObject) ((BasicDBObject) (object.get("user"))).get("stats"));
			}
			stats.put(name.toLowerCase(), stat);
		} else {
			BasicDBObject db = Shared.get().getProfileManager().getOfflinePlayerProfile(name);
			if (db != null)
				stats.put(name.toLowerCase(), new Stat(db.getString("name"), (BasicDBObject) db.get("stats")));

		}
	}

	/**
	 * Checks if the player is in a kill combo
	 * 
	 * @param killer
	 *            player to check
	 */
	public void checkCombo(Player killer) {
		GamerProfile profile = KitAPI.getPlayerManager().getProfile(killer.getName());
		if (profile.getCombo() != null) {
			Attack a = profile.getCombo();
			if (a.getAge() < 7) {
				int killCombo = a.getKills();
				if (killCombo == 2)
					Bukkit.broadcastMessage("§4" + killer.getDisplayName() + "§f got a §6§lTRIPLE KILL§r§f!");

				if (killCombo == 3)
					Bukkit.broadcastMessage(killer.getDisplayName() + "§f got a §6§lQUADRA KILL§r§f!");

				if (killCombo == 4) {
					Bukkit.broadcastMessage(killer.getDisplayName() + "§f got a §6§o§lPENTA KILL§r§f!!");
					killer.getLocation().getWorld().playEffect(killer.getLocation(), Effect.ENDER_SIGNAL, 500);
					killer.getWorld().playSound(killer.getLocation(), Sound.AMBIENCE_CAVE, 20F, 0.2F);
				}
				profile.setCombo(new Attack(a.getKills() + 1, System.currentTimeMillis()));

			} else
				profile.setCombo(new Attack(1, System.currentTimeMillis()));

		} else
			profile.setCombo(new Attack(1, System.currentTimeMillis()));
	}
}
