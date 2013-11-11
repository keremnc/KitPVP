package net.frozenorb.KitPVP.PlayerSystem;

import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.KitPVP.Types.Attack;

import com.mongodb.BasicDBObject;

public class GamerProfile {
	private String name;
	private BasicDBObject JSON = new BasicDBObject();
	private Kit lastUsedKit = null;
	private Attack combo;

	public GamerProfile(BasicDBObject obj, String name) {
		this.name = name;
		this.JSON = obj;
	}

	public Attack getCombo() {
		return combo;
	}

	public void setCombo(Attack combo) {
		this.combo = combo;
	}

	public BasicDBObject getJSON() {
		return JSON;
	}

	public void setLastUsedKit(Kit lastUsedKit) {
		this.lastUsedKit = lastUsedKit;
	}

	public Kit getLastUsedKit() {
		return lastUsedKit;
	}

	public String getName() {
		return name;
	}

	public boolean isObject(String obj) {
		if (JSON.containsField(obj))
			return JSON.getBoolean(obj);
		return false;
	}
}
