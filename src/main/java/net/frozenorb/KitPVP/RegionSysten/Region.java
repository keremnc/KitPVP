package net.frozenorb.KitPVP.RegionSysten;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.Kit;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.mBasic.Basic;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Represents a cuboid of some sort, usually used for giving special items on a warp
 * 
 * @author Kerem Celik
 * @since 1.0.0
 * 
 */
public class Region {
	private static ArrayList<Region> regions = new ArrayList<Region>();

	public static Region DONOR_SHOP = new Region("donorShop");

	public static Region SPAWN = new Region("spawn", new RegionMeta() {

		@Override
		public void onWarp(Player p) {
			for (Material m : KitAPI.getMatchManager().getMatchItems())
				p.getInventory().remove(m);
		}

		@Override
		public Class<? super Kit>[] getBlockedKits() {
			return null;
		}

		@Override
		public Class<? super Kit>[] getUsableKits() {
			return null;
		}

	});

	public static Region DUEL_SPAWN = new Region("1v1", new RegionMeta() {

		@Override
		public void onWarp(Player p) {
			if (!Basic.get().isInAdminMode(p))
				KitAPI.getMatchManager().applyArenaInventory(p);
		}

		@Override
		public Class<? super Kit>[] getBlockedKits() {
			return null;
		}

		@Override
		public java.lang.Class<? super Kit>[] getUsableKits() {
			return RegionMeta.EMPTY_KIT_ARRAY; // block all kits
		}
	});

	public static Region EARLY_HG = new Region("hg", new RegionMeta() {

		@Override
		public void onWarp(Player p) {
			if (!Basic.get().isInAdminMode(p))
				KitAPI.getServerManager().applyHGInventory(p);
		}

		@Override
		public Class<? super Kit>[] getBlockedKits() {
			return null;
		}

		@Override
		public Class<? super Kit>[] getUsableKits() {
			return RegionMeta.EMPTY_KIT_ARRAY;
		}

	});

	private String tag;
	private RegionMeta meta;

	private Region(String tag, RegionMeta rgMeta) {
		this.tag = tag;
		this.meta = rgMeta;
	}

	private Region(String tag) {
		this.tag = tag;
		this.meta = new RegionMeta() {

			@Override
			public void onWarp(Player p) {

			}

			@Override
			public Class<? super Kit>[] getBlockedKits() {
				return null;
			}

			@Override
			public Class<? super Kit>[] getUsableKits() {
				return null;
			}
		};
	}

	/**
	 * Gets the RegionMeta instance attached to the Region
	 * 
	 * @return meta
	 */
	public RegionMeta getMeta() {
		return meta;
	}

	/**
	 * The tag that defnes a {@link CuboidRegion} to a {@link Region}
	 * 
	 * @return tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Gets a list of all regions that are loaded
	 * 
	 * @return region
	 */
	public static ArrayList<Region> getRegions() {
		return regions;
	}

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
}
