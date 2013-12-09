package net.frozenorb.KitPVP.DataSystem.Managers;

import java.io.File;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.Utilities.DataSystem.AbstractDataLoader;

public class WarpDataManager extends AbstractDataLoader {

	public WarpDataManager(File f) {
		super(f);
	}

	@Override
	public void onLoad() {
		KitAPI.getKitPVP().getCommandManager().loadCommandsFromJson(getData());
	}

}
