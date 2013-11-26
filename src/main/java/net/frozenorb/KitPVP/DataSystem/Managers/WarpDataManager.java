package net.frozenorb.KitPVP.DataSystem.Managers;

import java.io.File;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.Utilities.DataSystem.DataManager;

public class WarpDataManager extends DataManager {

	public WarpDataManager(File f) {
		super(f);
	}

	@Override
	public void onLoad() {
		KitAPI.getKitPVP().getCommandManager().loadCommandsFromJson(getData());
	}

}
