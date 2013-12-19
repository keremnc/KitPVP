package net.frozenorb.KitPVP.StatSystem;

/**
 * Stat enum to change values of JSON in profiles, while keeping frontend enum values the same
 * 
 * @author Kerem
 * 
 */
public enum StatObjective {
	KILLS("kills", false, "Kills"), DEATHS("deaths", false, "Deaths"), DUEL_WINS("1v1wins", false, "1v1 Wins"), DUEL_LOSSES("1v1losses", false, "1v1 Losses"), HIGHEST_KILLSTREAK(
			"highestKillstreak", false, "Highest Killstreak"), KD_RATIO("kd", false, "KD"), KILLSTREAK("LOCAL", true, "Killstreak"), ELO("elo", false, "Rating"), KIT_DATA(
			"kitData", true, "kitData", false), RANKED_MATCHES_PLAYED("rankedMatchesPlayed", true, "rankedMatchesPlayed", false);
	private String name;
	private boolean local;
	private String friendlyName;
	private boolean display;

	private StatObjective(String name, boolean local, String friendlyName, boolean display) {
		this.name = name;
		this.friendlyName = friendlyName;
		this.local = local;
		this.display = display;

	}

	private StatObjective(String name, boolean local, String friendlyName) {
		this(name, local, friendlyName, true);
	}

	public boolean isDisplay() {
		return display;
	}

	public String getFriendlyName() {
		return friendlyName;
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
