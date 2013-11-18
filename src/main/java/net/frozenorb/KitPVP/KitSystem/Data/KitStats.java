package net.frozenorb.KitPVP.KitSystem.Data;

import com.mongodb.BasicDBObject;

public class KitStats extends BasicDBObject {

	private static final long serialVersionUID = -68795705218379355L;

	public KitStats(BasicDBObject dbobj) {
		for (java.util.Map.Entry<String, Object> entry : dbobj.entrySet())
			append(entry.getKey(), entry.getValue());

	}

	public KitStats() {
	}

	public int getKills() {
		if (containsField("kills"))
			return getInt("kills");
		put("kills", 0);
		return 0;
	}

	public int getDeaths() {
		if (containsField("deaths"))
			return getInt("deaths");
		put("deaths", 0);
		return 0;
	}

	public int getUses() {
		if (containsField("uses"))
			return getInt("uses");
		put("uses", 0);
		return 0;
	}

	public int getAbility() {
		if (containsField("ability"))
			return getInt("ability");
		put("ability", 0);
		return 0;
	}

	public void setKills(int i) {
		put("kills", i);
	}

	public void setDeaths(int i) {
		put("deaths", i);
	}

	public void setUses(int i) {
		put("uses", i);
	}

	public void setAbility(int i) {
		put("ability", i);
	}

	public void incrementKills(int i) {
		put("kills", getKills() + i);
	}

	public void incrementDeaths(int i) {
		put("deaths", getDeaths() + i);
	}

	public void incrementUses(int i) {
		put("uses", getUses() + i);
	}

	public void incrementAbility(int i) {
		put("ability", getAbility() + i);
	}
}
