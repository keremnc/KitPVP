package net.frozenorb.KitPVP.StatSystem.Elo;

public class SimpleKFactor implements KFactor {

	@Override
	public int getKValue(double rating) {
		if (rating < 2100) {
			return 32;
		} else if (rating < 2400) {
			return 24;
		} else {
			return 16;
		}
	}
}
