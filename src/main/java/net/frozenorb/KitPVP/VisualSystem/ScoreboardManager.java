package net.frozenorb.KitPVP.VisualSystem;

import java.util.HashMap;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.mBasic.Basic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
	private HashMap<String, OfflinePlayer> offlinePlayerCache = new HashMap<String, OfflinePlayer>();

	/**
	 * Gets an offline player with the given name
	 * 
	 * @param str
	 *            the name of the offline player
	 * @return the player
	 */
	public OfflinePlayer generateName(String str) {
		if (offlinePlayerCache.containsKey(str)) {
			return offlinePlayerCache.get(str);
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(str);
		offlinePlayerCache.put(str, player);
		return player;
	}

	/**
	 * Updates a player's scoreboard
	 * 
	 * @param p
	 *            the player to update
	 */
	public void updateScoreboard(Player p) {
		Scoreboard sb = p.getScoreboard();
		Objective o = sb.getObjective("nigger");
		if (o == null) {
			o = sb.registerNewObjective("nigger", "nogger");
		}
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.RED + p.getName());
		Stat s = KitAPI.getStatManager().getStat(p.getName());
		Score kills = o.getScore(generateName("§6Kills"));
		kills.setScore(s == null ? -1 : s.get(StatObjective.KILLS));
		Score deaths = o.getScore(generateName("§6Deaths"));
		deaths.setScore(s == null ? -1 : s.get(StatObjective.DEATHS));
		Score killstreak = o.getScore(generateName("§6Killstreak"));
		killstreak.setScore(s == null ? -1 : s.get(StatObjective.KILLSTREAK));
		Score credits = o.getScore(generateName("§6Credits"));
		credits.setScore((int) Basic.get().getEconomyManager().getBalance(p.getName()));
		Score rating = o.getScore(generateName("§6Rating"));
		rating.setScore(s == null ? -1 : KitAPI.getEloManager().getElo(p.getName().toLowerCase()));
	}

	/**
	 * Loads a player's scoreboard for the first time
	 * 
	 * @param player
	 *            the player to load to
	 */
	public void loadScoreboard(Player player) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = sb.registerNewObjective("nigger", "nogger");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName("§c" + player.getName());
		Stat s = KitAPI.getStatManager().getStat(player.getName());
		Score kills = o.getScore(generateName("§6Kills"));
		kills.setScore(s == null ? -1 : s.get(StatObjective.KILLS));
		Score deaths = o.getScore(generateName("§6Deaths"));
		deaths.setScore(s == null ? -1 : s.get(StatObjective.DEATHS));
		Score killstreak = o.getScore(generateName("§6Killstreak"));
		killstreak.setScore(s == null ? -1 : s.get(StatObjective.KILLSTREAK));
		Score credits = o.getScore(generateName("§6Credits"));
		credits.setScore((int) Basic.get().getEconomyManager().getBalance(player.getName()));
		Score rating = o.getScore(generateName("§6Rating"));
		rating.setScore(s == null ? -1 : KitAPI.getEloManager().getElo(player.getName().toLowerCase()));
		player.setScoreboard(sb);
	}
}
