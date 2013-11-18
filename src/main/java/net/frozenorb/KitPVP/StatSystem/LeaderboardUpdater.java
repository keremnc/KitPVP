package net.frozenorb.KitPVP.StatSystem;

import java.util.ArrayList;
import java.util.HashMap;

import net.frozenorb.KitPVP.API.KitAPI;

import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.BasicDBObject;

public class LeaderboardUpdater extends BukkitRunnable {
	private HashMap<StatObjective, String[]> data = new HashMap<StatObjective, String[]>();

	@Override
	public void run() {
		String gray = "§7=====================================================";
		for (StatObjective sb : StatObjective.values()) {
			if (sb.isLocal())
				continue;
			ArrayList<BasicDBObject> ldrbrds = KitAPI.getStatManager().getLeaderboards(sb);
			boolean first = true;
			ArrayList<String> statObjectives = new ArrayList<String>();
			int place = 1;
			statObjectives.add(gray);
			statObjectives.add("§6Showing Leaderboards: §f" + sb.getFriendlyName());
			statObjectives.add(gray);

			for (BasicDBObject db : ldrbrds) {
				statObjectives.add((first ? "§c§l1.§r" : "§7" + place + ".") + " §6" + db.keySet().iterator().next() + " : §f" + db.getInt(db.keySet().iterator().next()) + " " + sb.getFriendlyName().toLowerCase());
				first = false;
				place += 1;
			}
			statObjectives.add(gray);
			data.put(sb, statObjectives.toArray(new String[] {}));
		}
	}

	public HashMap<StatObjective, String[]> getData() {
		return data;
	}
}
