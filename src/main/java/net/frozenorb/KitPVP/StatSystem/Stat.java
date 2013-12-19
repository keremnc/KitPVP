package net.frozenorb.KitPVP.StatSystem;

import org.bukkit.Bukkit;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.StatSystem.Elo.EloManager;
import net.frozenorb.mShared.Shared;

import com.mongodb.BasicDBObject;

public class Stat {
	private BasicDBObject statJson = new BasicDBObject();
	private String playerName;

	public Stat(String name, BasicDBObject o) {
		this.statJson = o;
		this.playerName = name;
	}

	/**
	 * Gets the name of the player
	 * 
	 * @return name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Increments the given {@link StatObjective} by 1
	 * 
	 * @param obj
	 *            the objective to increment
	 */
	public void increment(StatObjective obj) {
		increment(obj, 1);
	}

	/**
	 * Increment the given {@link StatObjective} by the value
	 * 
	 * @param obj
	 *            the objective to increment
	 * @param value
	 *            value to increment by
	 */
	public void increment(StatObjective obj, int value) {
		int increment = value;
		if (statJson.containsField(obj.getName())) {
			increment += statJson.getInt(obj.getName());
		}
		statJson.put(obj.getName(), increment);
		if (Bukkit.getPlayerExact(playerName) != null) {
			KitAPI.getScoreboardManager().updateScoreboard(Bukkit.getPlayerExact(playerName));
		}
	}

	/**
	 * Gets the given {@link StatObjective} from the stat JSON object
	 * 
	 * @param sb
	 *            the objective to get it from
	 * @return value
	 */
	public int get(StatObjective sb) {
		if (sb == StatObjective.ELO && !statJson.containsField(sb.getName())) {
			statJson.put(StatObjective.ELO.getName(), EloManager.STARTING_ELO);
		}
		if (statJson.containsField(sb.getName())) {
			return statJson.getInt(sb.getName());
		}
		return 0;
	}

	/**
	 * Gets the given {@link StatObjective} from the stat JSON object
	 * 
	 * @param sb
	 *            the objective to get it from
	 * @return value
	 */
	public double getDouble(StatObjective sb) {
		if (statJson.containsField(sb.getName())) {
			return statJson.getDouble(sb.getName());
		}
		return 0;
	}

	/**
	 * Sets the given {@link StatObjective} to the value
	 * 
	 * @param obj
	 *            the objective to set
	 * @param value
	 *            the value to set to
	 */
	public void set(StatObjective obj, int value) {
		statJson.put(obj.getName(), value);
		if (Bukkit.getPlayerExact(playerName) != null) {
			KitAPI.getScoreboardManager().updateScoreboard(Bukkit.getPlayerExact(playerName));
		}
	}

	/**
	 * Saves the stat to the API.
	 * <p>
	 * Does <b>NOT</b> save to the API on method call, instead it registers a new Event with the mShared Event Manager.
	 * <p>
	 * Try not to fuck up the outbound request queue, limit sending as much as possible.
	 */
	public void saveStat() {
		BasicDBObject obj = new BasicDBObject("type", "stats").append("when", Shared.get().getUtilities().getTime(System.currentTimeMillis())).append("player", getPlayerName());
		BasicDBObject pls = new BasicDBObject();
		for (StatObjective sbobj : StatObjective.values()) {
			if (!sbobj.isLocal()) {
				pls.append(sbobj.getName(), get(sbobj));
			}
		}
		obj.append("stats", pls);
		Shared.get().getEventManager().registerNewEvent(obj);
	}
}
