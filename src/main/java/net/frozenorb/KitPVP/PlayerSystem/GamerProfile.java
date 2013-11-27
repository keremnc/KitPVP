package net.frozenorb.KitPVP.PlayerSystem;

import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.KitPVP.Types.Kill;

import com.mongodb.BasicDBObject;

public class GamerProfile {
	private String name;
	private BasicDBObject JSON = new BasicDBObject();
	private Kit lastUsedKit = null;
	private Kill combo;

	public GamerProfile(BasicDBObject obj, String name) {
		this.name = name;
		this.JSON = obj;
	}

	/**
	 * The current combo the player is on
	 * 
	 * @return combo
	 */
	public Kill getCombo() {
		return combo;
	}

	/**
	 * Sets the current kill combo the player is on
	 * 
	 * @param combo
	 *            the combo to set to
	 */
	public void setCombo(Kill combo) {
		this.combo = combo;
	}

	/**
	 * Gets the JSON data attached to the player
	 * 
	 * @return json data
	 */
	public BasicDBObject getJSON() {
		return JSON;
	}

	/**
	 * Sets the player's last used kit
	 * 
	 * @param lastUsedKit
	 *            the kit last used
	 */
	public void setLastUsedKit(Kit lastUsedKit) {
		this.lastUsedKit = lastUsedKit;
	}

	/**
	 * Gets the player's last used kit
	 * 
	 * @return kit
	 */
	public Kit getLastUsedKit() {
		return lastUsedKit;
	}

	/**
	 * Gets the name of the player
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Whether or not an object is set, with a true value
	 * 
	 * @param obj
	 *            the object to check for
	 * @return if it's set and false
	 */
	public boolean isObject(String obj) {
		if (JSON.containsField(obj))
			return JSON.getBoolean(obj);
		return false;
	}
}
