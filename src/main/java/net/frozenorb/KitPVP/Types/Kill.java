package net.frozenorb.KitPVP.Types;

/**
 * Class used to quickly check and see if a player is on a kill combo or not
 * 
 * @author Kerem Celik
 * 
 */
public class Kill {
	private int kills;
	private long time;

	/**
	 * Creates a new instance
	 * 
	 * @param kills
	 *            the kills a player has
	 * @param time
	 *            the timestamp the kill was made
	 */
	public Kill(int kills, long time) {
		this.kills = kills;
		this.time = time;
	}

	/**
	 * Sets the time that the Kill was set
	 * 
	 * @param time
	 *            the timestamp
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Gets the kill counter
	 * 
	 * @return kills
	 */
	public int getKills() {
		return kills;
	}

	/**
	 * Gets the timestamp the kill was made
	 * 
	 * @return timestamp
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the amount of kills done
	 * 
	 * @param kills
	 *            the amount of kills
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * Gets the length in seconds a kill was last made
	 * 
	 * @return seconds
	 */
	public int getAge() {
		return (int) ((System.currentTimeMillis() - time) / 1000);
	}
}
