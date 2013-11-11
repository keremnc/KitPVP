package net.frozenorb.KitPVP.Types;

public class Attack {
	private int kills;
	private long time;

	public Attack(int kills, long time) {
		this.kills = kills;
		this.time = time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getKills() {
		return kills;
	}

	public long getTime() {
		return time;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getAge() {
		return (int) ((System.currentTimeMillis() - time) / 1000);
	}
}
