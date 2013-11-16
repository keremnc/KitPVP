package net.frozenorb.KitPVP.ScoreboardSystem;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.RegionSysten.Region;
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

public class ScoreboardManager implements Runnable {

	public OfflinePlayer generateName(String str) {
		return Bukkit.getOfflinePlayer(str);
	}

	public void updateScoreboard(Player p) {
		Scoreboard sb = p.getScoreboard();
		Objective nameTag = sb.getObjective("nergger");
		if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, p.getLocation()) || (KitAPI.getMatchManager().isInMatch(p.getName()) && KitAPI.getMatchManager().getCurrentMatches().get(p.getName()).isInProgress())) {
			nameTag.setDisplaySlot(DisplaySlot.BELOW_NAME);
			nameTag.setDisplayName("Rating");
			for (Player lp : Bukkit.getOnlinePlayers()) {
				Score elo = nameTag.getScore(lp);
				elo.setScore(KitAPI.getEloManager().getElo(lp.getName().toLowerCase()));
			}
		} else {
			nameTag.setDisplaySlot(null);
		}
		Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
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
		rating.setScore(KitAPI.getEloManager().getElo(p.getName().toLowerCase()));
	}

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateScoreboard(p);
		}
	}

	public void loadScoreboard(Player player) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective nameTag = sb.registerNewObjective("nergger", "nirger");
		nameTag.setDisplaySlot(DisplaySlot.BELOW_NAME);
		nameTag.setDisplayName("Rating");
		for (Player p : Bukkit.getOnlinePlayers()) {
			Score elo = nameTag.getScore(p);
			elo.setScore(KitAPI.getEloManager().getElo(p.getName().toLowerCase()));

		}
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
		rating.setScore(KitAPI.getEloManager().getElo(player.getName().toLowerCase()));
		player.setScoreboard(sb);
	}
}
