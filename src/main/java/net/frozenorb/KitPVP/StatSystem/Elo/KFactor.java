package net.frozenorb.KitPVP.StatSystem.Elo;

public interface KFactor {

	/**
	 * Gets the K-Factor of the current rating
	 * 
	 * @param rating
	 *            the rating to get the factor of
	 * @return kfactor
	 */
	public int getKValue(double rating);

}
