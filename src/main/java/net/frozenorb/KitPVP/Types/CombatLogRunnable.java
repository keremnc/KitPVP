package net.frozenorb.KitPVP.Types;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatLogRunnable extends BukkitRunnable {

	private Player player;
	private int time;

	public CombatLogRunnable(Player p, int time) {
		this.time = time;
		player = p;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public void run() {
		time--;
		if (time == 0) {
			player.sendMessage(ChatColor.GREEN + "You are now able to log out!");
			cancel();

		}
	}

}
