package net.frozenorb.KitPVP.StatSystem;

/**
 * Stat enum to change values of JSON in profiles, while keeping frontend enum values the same
 * 
 * @author Kerem
 * 
 */
public enum StatObjective {
	KILLS("kills"), DEATHS("deaths"), DUEL_WINS("1v1wins"), DUEL_LOSSES("1v1losses"), HIGHEST_KILLSTREAK("highestKillstreak"), KD_RATIO("kd"), KILLSTREAK("LOCAL"), ELO("elo");
	private String name;

	private StatObjective(String name) {
		this.name = name;
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
