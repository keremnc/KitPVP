package net.frozenorb.KitPVP.RegionSysten;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public enum Region {
	SPAWN(DefaultFlag.PISTONS), DONOR_SHOP(DefaultFlag.EXP_DROPS), DUEL_SPAWN(DefaultFlag.MUSHROOMS);
	private StateFlag flag;

	private Region(StateFlag flag) {
		this.flag = flag;
	}

	public StateFlag getFlag() {
		return flag;
	}
}
