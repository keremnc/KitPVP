package net.frozenorb.KitPVP.RegionSysten;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.frozenorb.KitPVP.API.KitAPI;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class Region {
	public static Region SPAWN = new Region(DefaultFlag.PISTONS, new RegionMeta() {

		@Override
		public void onWarp(Player p) {
			for (Material m : KitAPI.getMatchManager().getMatchItems())
				p.getInventory().remove(m);
		}
	});
	public static Region DONOR_SHOP = new Region(DefaultFlag.EXP_DROPS);
	public static Region DUEL_SPAWN = new Region(DefaultFlag.MUSHROOMS, new RegionMeta() {

		@Override
		public void onWarp(Player p) {
			KitAPI.getMatchManager().applyArenaInventory(p);
		}
	});
	public static Region EARLY_HG = new Region(DefaultFlag.MYCELIUM_SPREAD, new RegionMeta() {

		@Override
		public void onWarp(Player p) {
			KitAPI.getServerManager().applyHGInventory(p);
		}
	});
	private static ArrayList<Region> regions = new ArrayList<Region>();
	static {
		for (Field f : Region.class.getFields()) {
			if (Modifier.isStatic(f.getModifiers())) {
				try {
					regions.add((Region) f.get(null));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private StateFlag flag;
	private RegionMeta meta;

	private Region(StateFlag flag, RegionMeta rgMeta) {
		this.flag = flag;
		this.meta = rgMeta;
	}

	private Region(StateFlag flag) {
		this.flag = flag;
		this.meta = new RegionMeta() {

			@Override
			public void onWarp(Player p) {

			}
		};
	}

	public RegionMeta getMeta() {
		return meta;
	}

	public StateFlag getFlag() {
		return flag;
	}

	public static ArrayList<Region> getRegions() {
		return regions;
	}
}
