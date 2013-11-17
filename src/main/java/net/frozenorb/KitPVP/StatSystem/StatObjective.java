package net.frozenorb.KitPVP.StatSystem;

/**
 * Stat enum to change values of JSON in profiles, while keeping frontend enum values the same
 * 
 * @author Kerem
 * 
 */
public enum StatObjective {
	KILLS("kills", false), DEATHS("deaths", false), DUEL_WINS("1v1wins", false), DUEL_LOSSES("1v1losses", false), HIGHEST_KILLSTREAK("highestKillstreak", false), KD_RATIO("kd",
			false), KILLSTREAK("LOCAL", true), ELO("elo", true), KIT_DATA("kitData", true);
	private String name;
	private boolean local;

	private StatObjective(String name, boolean local) {
		this.name = name;
		this.local = local;

	}

	public boolean isLocal() {
		return local;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Gets a {@link StatObjective} from a String
	 * 
	 * @param name
	 *            the object to parse
	 * @return null if not found, otherwise parsed
	 */
	public static StatObjective parse(String name) {
		for (StatObjective so : values()) {
			if (so.getName().equalsIgnoreCase(name))
				return so;
			if (so.toString().equalsIgnoreCase(name))
				return so;
		}
		return null;
	}
}
