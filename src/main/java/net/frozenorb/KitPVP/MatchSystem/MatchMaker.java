package net.frozenorb.KitPVP.MatchSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.InventorySystem.Inventories.MatchTypeInventory;
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
			for (MatchQueue queue : KitAPI.getMatchManager().getMatches()) {
				if (queue.getQueueType() == QueueType.RANKED) {
					list.add(queue);
				}
			}
		}
	}

	public int getSecondsUntilSelect() {
		return secondsUntilSelect;
	}

	public static void main(String[] args) {
		new MatchMaker().new MatchFinder(null).find(new HashMap<String, Integer>() {
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
		});
	}

	class MatchFinder {
		private MatchList list;

		public MatchFinder(MatchList list) {
			this.list = list;
		}

		public MatchList getList() {
			return list;
		}

		public void sort() {
			HashMap<String, Integer> preSortedEloMap = new HashMap<String, Integer>();
			for (MatchQueue queue : list) {
				preSortedEloMap.put(queue.getPlayer().getName(), KitAPI.getEloManager().getElo(queue.getPlayer().getName()));
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

		public void find(HashMap<String, Integer> elo) {
			Map<String, Integer> sortedEloMap = Core.get().sortByValue(elo);
			TreeMap<String, Integer> maps = new TreeMap<String, Integer>(sortedEloMap);
			for (int i = 0; i < Math.floor(elo.size() / 2); i++) {
				String p1 = maps.firstKey();
				String nearest = nearestKey(maps, p1);
				maps.remove(nearest);
				maps.remove(p1);
				System.out.println(p1 + ":" + nearest);
			}
			if (maps.firstEntry() != null) {
				System.out.println("left out: " + maps.firstKey());
			}
		}
	}

}
