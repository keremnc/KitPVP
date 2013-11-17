package net.frozenorb.KitPVP.KitSystem.Data;

import net.frozenorb.KitPVP.KitSystem.Kit;

import com.mongodb.BasicDBObject;

@SuppressWarnings("serial")
public class PlayerKitData extends BasicDBObject {

	public PlayerKitData(BasicDBObject basicDBObject) {
		for (java.util.Map.Entry<String, Object> entry : basicDBObject.entrySet())
			append(entry.getKey(), entry.getValue());
	}

	public PlayerKitData() {
	}

	/**
	 * Gets the BasicDBObject mapped to a Kit
	 * 
	 * @param k
	 *            the Kit
	 * @return data
	 */
	public KitStats get(Kit k) {
		if (!containsField(k.getName()))
			append(k.getName(), new KitStats());
		append(k.getName(), new KitStats((BasicDBObject) get(k.getName())));
		return (KitStats) get(k.getName());
	}
}
