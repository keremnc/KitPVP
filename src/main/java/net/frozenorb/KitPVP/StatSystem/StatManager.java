package net.frozenorb.KitPVP.StatSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.Types.Kill;
import net.frozenorb.mShared.Shared;
import net.frozenorb.mShared.API.Profile.PlayerProfile;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class StatManager {
	private Map<String, Stat> stats = new HashMap<String, Stat>();
	private ReadWriteLock statsLock = new ReentrantReadWriteLock();
	private HashMap<String, PlayerData> playerData = new HashMap<String, PlayerData>();

	/**
	 * Get the Stat object of the name
	 * 
	 * @param name
	 *            the name of the player
	 * @return the Stat object
	 */
	public Stat getStat(String name) {
		statsLock.readLock().lock();
		try {
			return stats.get(name.toLowerCase());
		} finally {
			statsLock.readLock().unlock();
		}
	}

	/**
	 * Gets whether a name has a value in the list
	 * <p>
	 * We do this because stats that aren't available to be loaded are set to null
	 * 
	 * @param name
	 *            the name to check
	 * @return if it exists as a key or not
	 */
	public boolean exists(String name) {
		statsLock.readLock().lock();
		try {
			return stats.containsKey(name.toLowerCase());
		} finally {
			statsLock.readLock().unlock();
		}
	}

	/**
	 * Gets the local data of the name
	 * 
	 * @param name
	 *            the name of the player
	 * @return local data
	 */
	public PlayerData getPlayerData(String name) {
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
	public void setPlayerData(String name, PlayerData data) {
		playerData.put(name.toLowerCase(), data);
		data.save();
	}

	/**
	 * Gets all of the local data files
	 * 
	 * @return local data
	 */
	public Collection<PlayerData> getAllPlayerData() {
		return playerData.values();
	}

	/**
	 * Loads a player's stats from the API server
	 * 
	 * @param name
	 *            the name of the player to load from
	 */
	public void loadPlayerData(final String name) {
		final long now = System.currentTimeMillis();
		BasicDBObject response = Shared.get().getConnectionManager().sendGet(Shared.get().getConnectionManager().getApiRoot() + "/servercmd/usermeta/" + name);
		if (response != null) {
			PlayerData data = new PlayerData(name.toLowerCase(), response);
			playerData.put(name.toLowerCase(), data);
		} else {
			playerData.put(name.toLowerCase(), new PlayerData(name.toLowerCase(), new BasicDBObject()));
		}
		System.out.println("Successfully player data for " + name + " via " + Shared.get().getConnectionManager().getApiRoot() + ". Login API call time: " + (System.currentTimeMillis() - now) + "ms");
	}

	/**
	 * Gets a leaderboard for a certain {@link StatObjective} from the API
	 * 
	 * @param objective
	 *            the {@link StatObjective} to request
	 * @return the top 10 players
	 */
	public ArrayList<BasicDBObject> getLeaderboards(StatObjective objective) {
		try {
			if (objective.isLocal())
				return null;
			URL obj = new URL(Shared.get().getConnectionManager().getApiRoot() + "/rankings?sort=" + objective.getName().replace("highestKillstreak", "killstreak"));
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
			ArrayList<BasicDBObject> leaderboards = new ArrayList<BasicDBObject>();
			BasicDBObject db = (BasicDBObject) JSON.parse("{ \"data\": " + response + "}");
			for (Object o : (BasicDBList) db.get("data")) {
				BasicDBObject profile = (BasicDBObject) o;
				String name = profile.getString("name");
				BasicDBObject stats = (BasicDBObject) profile.get("stats");
				Stat s = new Stat(name, stats);
				BasicDBObject ldr = new BasicDBObject(name, s.get(objective));
				leaderboards.add(ldr);
			}
			return leaderboards;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads the stats of the name
	 * 
	 * @param name
	 *            the name of the player
	 */
	public void loadStats(final String name) {
		PlayerProfile pp = Shared.get().getProfileManager().getProfile(name);
		if (pp != null) {
			BasicDBObject object = pp.getJson();
			Stat stat = new Stat(((BasicDBObject) (object.get("user"))).getString("name"), new BasicDBObject());
			if (((BasicDBObject) (object.get("user"))).containsField("stats")) {
				stat = new Stat(((BasicDBObject) (object.get("user"))).getString("name"), (BasicDBObject) ((BasicDBObject) (object.get("user"))).get("stats"));
			}
			statsLock.writeLock().lock();
			try {
				stats.put(name.toLowerCase(), stat);
			} finally {
				statsLock.writeLock().unlock();
			}
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(KitAPI.getKitPVP(), new Runnable() {

				@Override
				public void run() {
					BasicDBObject db = Shared.get().getProfileManager().getOfflinePlayerProfile(name);
					if (db != null) {
						statsLock.writeLock().lock();
						try {
							stats.put(name.toLowerCase(), new Stat(db.getString("name"), (BasicDBObject) db.get("stats")));
						} finally {
							statsLock.writeLock().unlock();
						}
					}
				}
			});
		}
	}

	public void loadStatsSync(final String name) {
		PlayerProfile pp = Shared.get().getProfileManager().getProfile(name);
		if (pp != null) {
			BasicDBObject object = pp.getJson();
			Stat stat = new Stat(((BasicDBObject) (object.get("user"))).getString("name"), new BasicDBObject());
			if (((BasicDBObject) (object.get("user"))).containsField("stats")) {
				stat = new Stat(((BasicDBObject) (object.get("user"))).getString("name"), (BasicDBObject) ((BasicDBObject) (object.get("user"))).get("stats"));
			}
			statsLock.writeLock().lock();
			try {
				stats.put(name.toLowerCase(), stat);
			} finally {
				statsLock.writeLock().unlock();
			}
		} else {
			BasicDBObject db = Shared.get().getProfileManager().getOfflinePlayerProfile(name);
			if (db != null) {
				statsLock.writeLock().lock();
				try {
					stats.put(name.toLowerCase(), new Stat(db.getString("name"), (BasicDBObject) db.get("stats")));
				} finally {
					statsLock.writeLock().unlock();
				}
			}
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
			Kill a = profile.getCombo();
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
				profile.setCombo(new Kill(a.getKills() + 1, System.currentTimeMillis()));

			} else
				profile.setCombo(new Kill(1, System.currentTimeMillis()));

		} else
			profile.setCombo(new Kill(1, System.currentTimeMillis()));
	}
}
