package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;

public class Stats extends BaseCommand {

	@Override
	public void execute() {
		String player = sender.getName();
		if (args.length > 0) {
			Player other = Bukkit.getPlayer(args[0]);
			if (other != null)
				player = other.getName();
			else
				player = args[0];
		}
		Stat s = KitAPI.getStatManager().getStat(player);
		if (s == null) {
			KitAPI.getStatManager().loadStats(player);
			s = KitAPI.getStatManager().getStat(player);
		}
		if (s == null) {
			sender.sendMessage(ChatColor.RED + "Player '" + player + "' could not be found.");
			return;
		}
		String gray = "§7=====================================================";
		String header = String.format("§6Showing stats for §f%s§6 | Rank: §f%s", s.getPlayerName(), 0);
		String kills = String.format("§6Kills:§f %s", s.get(StatObjective.KILLS));
		String deaths = String.format("§6Deaths:§f %s", s.get(StatObjective.DEATHS));
		String kd = String.format("§6KD:§f %s", s.get(StatObjective.KD_RATIO));
		String cks = String.format("§6Current Killstreak:§f %s", s.get(StatObjective.KILLSTREAK));
		String tks = String.format("§6Highest Killstreak:§f %s", s.get(StatObjective.HIGHEST_KILLSTREAK));
		String wins = String.format("§61v1 Wins:§f %s", s.get(StatObjective.DUEL_WINS));
		String losses = String.format("§61v1 Losses:§f %s", s.get(StatObjective.DUEL_LOSSES));
		String elo = String.format("§6Rating:§f %s", KitAPI.getEloManager().getElo(player.toLowerCase()));
		sender.sendMessage(new String[] { gray, header, gray, kills, deaths, kd, cks, tks, wins, losses, elo, gray });
	}
}
