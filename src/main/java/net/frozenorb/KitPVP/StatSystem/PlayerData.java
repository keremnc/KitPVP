package net.frozenorb.KitPVP.StatSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.Data.PlayerKitData;

import org.bukkit.Bukkit;

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

	public void delegateSave() {
		Bukkit.getScheduler().runTaskAsynchronously(KitAPI.getKitPVP(), new Runnable() {

			@Override
			public void run() {
				saveAsync();
			}
		});

	}

	public void saveAsync() {

		try {
			File saveTo = new File("data" + File.separator + "playerData" + File.separator + name.toLowerCase() + ".json");
			if (!saveTo.exists())
				saveTo.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveTo));
			writer.write(data.toString());
			writer.flush();
			writer.close();
		} catch (IOException ex) {
			System.out.println("Error on data file that follows \n" + name + ": " + toString());
			ex.printStackTrace();
		}

	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
