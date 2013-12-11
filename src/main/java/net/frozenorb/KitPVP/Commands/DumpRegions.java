package net.frozenorb.KitPVP.Commands;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class DumpRegions extends BaseCommand {

	@Override
	public void syncExecute() {
		// if (sender.isOp()) {
		// final BasicDBObject db = new BasicDBObject("rgs", new BasicDBList() {
		// private static final long serialVersionUID = 1L;
		// World w = ((Player) sender).getWorld();
		// {
		// for (ProtectedRegion r : ((WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard")).getRegionManager(w).getRegions().values()) {
		// CuboidRegion rg = new CuboidRegion(r.getId(), new Location(w, r.getMinimumPoint().getBlockX(), r.getMinimumPoint().getBlockY(), r.getMinimumPoint().getBlockZ()), new Location(w, r.getMaximumPoint().getBlockX(), r.getMaximumPoint().getBlockY(), r.getMaximumPoint().getBlockZ()));
		// WorldGuardPlugin wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		// if (!wgp.getRegionManager(w).getApplicableRegions(r.getMinimumPoint()).allows(Region.SPAWN.getFlag()))
		// rg.addTag("spawn");
		// if (!wgp.getRegionManager(w).getApplicableRegions(r.getMinimumPoint()).allows(Region.EARLY_HG.getFlag()))
		// rg.addTag("hg");
		// if (!wgp.getRegionManager(w).getApplicableRegions(r.getMinimumPoint()).allows(Region.DUEL_SPAWN.getFlag()))
		// rg.addTag("1v1");
		// if (!wgp.getRegionManager(w).getApplicableRegions(r.getMinimumPoint()).allows(DefaultFlag.PVP))
		// rg.addTag("no-pvp");
		// if (!wgp.getRegionManager(w).getApplicableRegions(r.getMinimumPoint()).allows(DefaultFlag.FIRE_SPREAD))
		// rg.addTag("no-firespread");
		// add(new CuboidSerializer().serialize(rg));
		// }
		// }
		// });
		// try {
		// new BufferedWriter(new FileWriter(new File("RegionDump.JSON") {
		// private static final long serialVersionUID = 1L;
		//
		// {
		// createNewFile();
		// }
		// })) {
		// {
		// write(Core.get().formatDBObject(db));
		// flush();
		// close();
		// }
		// };
		// } catch (Exception ex) {
		// }
		// Core.get().sendFormattedDBOBject(sender, db, "§e");
		//
		// }
	}
}
