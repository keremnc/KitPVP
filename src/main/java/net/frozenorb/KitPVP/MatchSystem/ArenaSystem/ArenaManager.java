package net.frozenorb.KitPVP.MatchSystem.ArenaSystem;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;

import com.mongodb.BasicDBObject;

import net.frozenorb.Utilities.DataSystem.AbstractDataLoader;
import net.frozenorb.Utilities.Serialization.Serializers.LocationSerializer;

public class ArenaManager extends AbstractDataLoader {
	private static HashSet<Integer> arenasInUse = new HashSet<Integer>();
	private static HashSet<Arena> arenas = new HashSet<Arena>();
	private static int arenaAmount = 0;

	public void unregisterArena(Arena arena) {
		arenasInUse.remove(arena.getId());
	}

	public HashSet<Arena> getArenas() {
		return arenas;
	}

	public int getArenaAmount() {
		return arenaAmount;
	}

	public HashSet<Integer> getArenasInUse() {
		return arenasInUse;
	}

	public Arena requestArena() {
		for (Arena a : arenas) {
			if ((!arenasInUse.contains(a.getId()))) {
				arenasInUse.add(a.getId());
				return a;
			}
		}
		System.out.println("\n\n\n\nERROR! NOT ENOUGH ARENAS!!\n\n\n");
		return null;
	}

	public int getValidID() {
		arenaAmount += 1;
		if (getById(arenaAmount) != null)
			return getValidID();
		return arenaAmount;
	}

	public ArenaManager(File f) {
		super(f);
	}

	public Arena getById(int id) {
		for (Arena a : arenas)
			if (a.getId() == id)
				return a;
		return null;
	}

	public void clearDuplicates(int id) {
		Iterator<Arena> iter = arenas.iterator();
		while (iter.hasNext())
			if (iter.next().getId() == id)
				iter.remove();
	}

	public void overrideArena(Arena override, Location l1, Location l2) {
		int id = override.getId();
		BasicDBObject arena = new BasicDBObject();
		arena.append("1", new LocationSerializer().serialize(l1)).append("2", new LocationSerializer().serialize(l2));
		clearDuplicates(override.getId());
		getData().append(id + "", arena);
		arenas.add(new Arena(id, l1, l2));
		saveData();
	}

	public void addArena(Location l1, Location l2) {
		int id = getValidID();
		System.out.println(id + ", attempting to save.");
		BasicDBObject arena = new BasicDBObject();
		arena.append("1", new LocationSerializer().serialize(l1)).append("2", new LocationSerializer().serialize(l2));
		getData().append(id + "", arena);
		clearDuplicates(id);
		arenas.add(new Arena(id, l1, l2));
		saveData();

	}

	@Override
	public void onLoad() {
		if (getData() != null && getData().entrySet() != null && getData().size() > 0) {
			for (Entry<String, Object> entry : getData().entrySet()) {
				int id = (Integer.parseInt(entry.getKey()));
				BasicDBObject locs = (BasicDBObject) entry.getValue();
				Location l1 = new LocationSerializer().deserialize((BasicDBObject) locs.get("1"));
				Location l2 = new LocationSerializer().deserialize((BasicDBObject) locs.get("2"));
				Arena a = new Arena(id, l1, l2);
				System.out.println("arena " + id + " loaded.");
				arenas.add(a);

			}
			arenaAmount = arenas.size();
			System.out.println(arenaAmount + " arenas are loaded.");

		} else {
			System.out.println("error loading arenas.");
		}

	}
}
