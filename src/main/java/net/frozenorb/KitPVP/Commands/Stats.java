package net.frozenorb.KitPVP.Commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;

public class Stats extends BaseCommand {
	public static final String GRAY_HEADER = "§7=====================================================";

	@Override
	public List<String> tabComplete() {
		LinkedList<String> params = new LinkedList<String>(Arrays.asList(args));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;
		if (params.size() >= 1) {
			action = params.pop().toLowerCase();
		} else {
			return results;
		}

		if (params.size() == 0) {
			if ("top".toLowerCase().startsWith(action)) {
				results.add("top");
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(action.toLowerCase())) {
					results.add(p.getName());
				}
			}
		} else if (params.size() == 1) {
			if (action.equals("top")) {
				for (StatObjective sb : StatObjective.values()) {
					if (!sb.isLocal())
						if (sb.getName().toLowerCase().startsWith(params.getFirst().toLowerCase())) {
							results.add(sb.getName());
						}
				}
			}
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				results.add(p.getName());
			}
		}
		return results;
	}

	public void getStatsAsync(final CommandSender sender, final String[] args, final String name) {
		while (KitAPI.getStatManager().getStat(name) == null)
			if (KitAPI.getStatManager().exists(name)) {
				sender.sendMessage(ChatColor.RED + "Player '" + name + "' not found.");
				return;
			}
		Stat st = KitAPI.getStatManager().getStat(name);
		String header = String.format("§6Showing stats for §f%s§6 | Rank: §f%s", st.getPlayerName(), 0);
		String kills = String.format("§6Kills:§f %s", st.get(StatObjective.KILLS));
		String deaths = String.format("§6Deaths:§f %s", st.get(StatObjective.DEATHS));
		String kd = String.format("§6KD:§f %s", st.getDouble(StatObjective.KD_RATIO));
		String cks = String.format("§6Current Killstreak:§f %s", st.get(StatObjective.KILLSTREAK));
		String tks = String.format("§6Highest Killstreak:§f %s", st.get(StatObjective.HIGHEST_KILLSTREAK));
		String wins = String.format("§61v1 Wins:§f %s", st.get(StatObjective.DUEL_WINS));
		String losses = String.format("§61v1 Losses:§f %s", st.get(StatObjective.DUEL_LOSSES));
		String elo = String.format("§6Rating:§f %s", KitAPI.getEloManager().getElo(name.toLowerCase()));
		sender.sendMessage(new String[] { GRAY_HEADER, header, GRAY_HEADER, kills, deaths, kd, cks, tks, wins, losses, elo, GRAY_HEADER });

	}

	@Override
	public void syncExecute() {
		String player = sender.getName();
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("top")) {
				String objective = "kills";
				if (args.length > 1)
					objective = args[1];
				final StatObjective sb = StatObjective.parse(objective);
				if (sb == null || (sb != null && sb.isLocal())) {
					sender.sendMessage(ChatColor.RED + "Unrecognized objective '" + objective + "'.");
					return;
				}
				sender.sendMessage(KitAPI.getKitPVP().getLeaderboardUpdater().getData().get(sb));
				return;
			}
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
		final String name = player;
		if (s == null) {
			Bukkit.getScheduler().runTaskAsynchronously(KitPVP.get(), new Runnable() {
				public void run() {
					getStatsAsync(sender, args, name);
				}
			});
			return;
		} else {

			s = KitAPI.getStatManager().getStat(player);
			String header = String.format("§6Showing stats for §f%s§6 | Rank: §f%s", s.getPlayerName(), 0);
			String kills = String.format("§6Kills:§f %s", s.get(StatObjective.KILLS));
			String deaths = String.format("§6Deaths:§f %s", s.get(StatObjective.DEATHS));
			String kd = String.format("§6KD:§f %s", s.getDouble(StatObjective.KD_RATIO));
			String cks = String.format("§6Current Killstreak:§f %s", s.get(StatObjective.KILLSTREAK));
			String tks = String.format("§6Highest Killstreak:§f %s", s.get(StatObjective.HIGHEST_KILLSTREAK));
			String wins = String.format("§61v1 Wins:§f %s", s.get(StatObjective.DUEL_WINS));
			String losses = String.format("§61v1 Losses:§f %s", s.get(StatObjective.DUEL_LOSSES));
			String elo = String.format("§6Rating:§f %s", KitAPI.getEloManager().getElo(player.toLowerCase()));
			sender.sendMessage(new String[] { GRAY_HEADER, header, GRAY_HEADER, kills, deaths, kd, cks, tks, wins, losses, elo, GRAY_HEADER });
		}
	}
}
