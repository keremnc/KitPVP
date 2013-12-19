package net.frozenorb.KitPVP.StatSystem;

import org.bukkit.Bukkit;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.KitSystem.Data.PlayerKitData;
import net.frozenorb.mShared.Shared;

import com.mongodb.BasicDBObject;

public class PlayerData {

	private String name;
	private BasicDBObject data;

	public PlayerData(String name, BasicDBObject db) {
		this.name = name;
		this.data = db;
	}

	public BasicDBObject getData() {
		return data;
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
		if (data.containsField(obj.getName())) {
			increment += data.getInt(obj.getName());
		}
		data.put(obj.getName(), increment);
	}

	/**
	 * Gets the given {@link StatObjective} from the stat JSON object
	 * 
	 * @param sb
	 *            the objective to get it from
	 * @return value
	 */
	public int get(StatObjective sb) {
		if (data.containsField(sb.getName()))
			return data.getInt(sb.getName());
		data.append(sb.getName(), 0);
		return 0;
	}

	/**
	 * Gets KitData for this player
	 * 
	 * @return KitData
	 */
	public PlayerKitData getPlayerKitData() {
		if (!data.containsField(StatObjective.KIT_DATA.getName()))
			data.append(StatObjective.KIT_DATA.getName(), new PlayerKitData());
		data.append(StatObjective.KIT_DATA.getName(), new PlayerKitData((BasicDBObject) data.get(StatObjective.KIT_DATA.getName())));
		return (PlayerKitData) data.get(StatObjective.KIT_DATA.getName());
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
		data.put(obj.getName(), value);

	}

	public void save() {
		Bukkit.getScheduler().runTaskAsynchronously(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				saveSync();
			}
		});
	}

	public void saveSync() {
		Shared.get().getConnectionManager().sendPut(Shared.get().getConnectionManager().getApiRoot() + "/servercmd/usermeta/" + name, getData().toString());
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
