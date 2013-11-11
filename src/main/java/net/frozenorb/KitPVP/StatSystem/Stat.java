package net.frozenorb.KitPVP.StatSystem;

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
	}

	/**
	 * Gets the given {@link StatObjective} from the stat JSON object
	 * 
	 * @param sb
	 *            the objective to get it from
	 * @return value
	 */
	public int get(StatObjective sb) {
		if (statJson.containsField(sb.getName()))
			return statJson.getInt(sb.getName());
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

	}
}
