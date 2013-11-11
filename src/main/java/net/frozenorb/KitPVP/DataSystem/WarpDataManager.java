package net.frozenorb.KitPVP.DataSystem;

import java.io.File;

import net.frozenorb.KitPVP.API.KitAPI;

public class WarpDataManager extends DataManager {

	public WarpDataManager(File f) {
		super(f);
	}

	@Override
	public void onLoad() {
		KitAPI.getKitPVP().getCommandManager().loadCommandsFromJson(getData());

	}

}
