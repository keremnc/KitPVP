package net.frozenorb.KitPVP.MatchSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.InventorySystem.Inventories.MatchTypeInventory;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.MatchSystem.Queue.MatchList;
import net.frozenorb.KitPVP.MatchSystem.Queue.MatchQueue;
import net.frozenorb.KitPVP.MatchSystem.Queue.QueueType;
import net.frozenorb.Utilities.Core;

public class MatchMaker implements Runnable {
	int secondsUntilSelect = 30;

	@Override
	public void run() {
		MatchTypeInventory.updateAllOpenInventories();
		secondsUntilSelect--;
		if (secondsUntilSelect == -1) {
			secondsUntilSelect = 30;
			MatchList list = new MatchList();
			Iterator<MatchQueue> iter = KitAPI.getMatchManager().getMatches().iterator();
			while (iter.hasNext()) {
				MatchQueue queue = iter.next();

				if (queue.getQueueType() == QueueType.RANKED) {
					list.add(queue);
				}
				iter.remove();
			}
			findMatches(list);
		}
	}

	public int getSecondsUntilSelect() {
		return secondsUntilSelect;
	}

	public static void main(String[] args) {
		HashMap<String, Integer> m = new HashMap<String, Integer>() {
			private static final long serialVersionUID = 1L;

			{
				put("k", 1500);
				put("c", 1600);
				put("a", 1700);
				put("b", 1900);
				put("z", 1101);
				put("y", 1100);
				put("randy", 1200);
			}
		};
		MatchMaker maker = new MatchMaker();
		maker.find(m);
	}

	public void findMatches(MatchList list) {
		HashMap<String, Integer> preSortedEloMap = new HashMap<String, Integer>();
		for (MatchQueue queue : list) {
			preSortedEloMap.put(queue.getPlayer().getName(), KitAPI.getEloManager().getElo(queue.getPlayer().getName()));
		}
		ArrayList<Matchup> ms = find(preSortedEloMap);
		for (Matchup matchup : ms) {
			MatchQueue queue = new MatchQueue(matchup.getPlayer1(), Loadout.getByName("Buffed w/ Speed II"), QueueType.RANKED);
			KitAPI.getMatchManager().matchFound(matchup.getPlayer1(), matchup.getPlayer2(), Loadout.getByName("Buffed w/ Speed II"), queue, true);
			list.remove(matchup.getPlayer1().getName());
			list.remove(matchup.getPlayer2().getName());
		}
	}

	public String nearestKey(Map<String, Integer> map, String target) {
		double minDiff = Double.MAX_VALUE;
		String nearest = null;
		for (String key : map.keySet()) {
			if (!key.equalsIgnoreCase(target)) {
				double diff = Math.abs(map.get(target) - map.get(key));
				if (diff < minDiff) {
					nearest = key;
					minDiff = diff;
				}
			}
		}
		return nearest;
	}

	public ArrayList<Matchup> find(HashMap<String, Integer> elo) {
		ArrayList<Matchup> mups = new ArrayList<MatchMaker.Matchup>();
		Map<String, Integer> sortedEloMap = Core.get().sortByValue(elo);
		TreeMap<String, Integer> maps = new TreeMap<String, Integer>(sortedEloMap);
		for (int i = 0; i < Math.floor(elo.size() / 2); i++) {
			String p1 = maps.firstKey();
			String nearest = nearestKey(maps, p1);
			maps.remove(nearest);
			maps.remove(p1);
			if (Bukkit.getPlayerExact(p1) != null && Bukkit.getPlayerExact(nearest) != null) {
				Matchup m = new Matchup(Bukkit.getPlayerExact(p1), Bukkit.getPlayerExact(nearest));
				mups.add(m);
			}
		}
		if (maps.firstEntry() != null) {
			Player p = Bukkit.getPlayerExact(maps.firstKey());
			if (p != null) {
				p.sendMessage(ChatColor.RED + "A suitable match wasn't found, so you have been added back into the queue.");
				KitAPI.getMatchManager().addToQueue(new MatchQueue(p, Loadout.getByName("Buffed w/ Speed II"), QueueType.RANKED));
			}
		}
		return mups;
	}

	class Matchup {
		Player player1, player2;

		public Matchup(Player player1, Player player2) {
			this.player1 = player1;
			this.player2 = player2;
		}

		public Player getPlayer1() {
			return player1;
		}

		public Player getPlayer2() {
			return player2;
		}

		@Override
		public String toString() {
			return player1.getName() + ":" + player2.getName();
		}
	}
}
