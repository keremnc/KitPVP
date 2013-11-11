package net.frozenorb.KitPVP.StatSystem.Elo;

import com.mongodb.BasicDBObject;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.StatSystem.LocalPlayerData;
import net.frozenorb.KitPVP.StatSystem.StatObjective;

/**
 * Elo manager class to handle rankings in ranked 1v1
 * 
 * @since 11/6/2013
 * @author Kerem
 * 
 */
public class EloManager {
	public static int STARTING_ELO = 1500;

	/**
	 * Gets the estimation
	 * 
	 * @param eloA
	 *            the first elo
	 * @param eloB
	 *            the second elo
	 * @return [eloA, eloB]
	 */
	public double[] getEstimations(double eloA, double eloB) {
		double[] ret = new double[2];
		double estA = 1.0D / (1.0D + Math.pow(10.0D, (eloB - eloA) / 400.0D));
		double estB = 1.0D / (1.0D + Math.pow(10.0D, (eloA - eloB) / 400.0D));
		ret[0] = estA;
		ret[1] = estB;
		return ret;
	}

	/**
	 * Gets the K(erem)-Factor of the current elo
	 * 
	 * @param elo
	 *            the elo
	 * @return K(erem)-Factor
	 */
	public int getKeremFactor(int elo) {
		if (elo < 2100)
			return 32;
		if (elo < 2399)
			return 24;
		return 16;
	}

	/**
	 * Gets the calculated elo of winner and loser
	 * 
	 * @param winner
	 *            winner elo
	 * @param loser
	 *            loser elo
	 * @return array [winner, loser]
	 */
	public int[] getNewElo(int winner, int loser) {
		double[] ests = new double[2];
		int[] ret = new int[2];
		ests = getEstimations(winner, loser);
		int newRankA = (int) (winner + getKeremFactor(winner) * (1 - ests[0]));
		int newRankB = (int) (loser + getKeremFactor(loser) * -ests[1]);
		ret[0] = Math.round(newRankA);
		ret[1] = Math.round(newRankB);
		return ret;
	}

	/**
	 * Gets the ELO of the player
	 * 
	 * @param name
	 *            the name of the player
	 * @return elo
	 */
	public int getElo(String name) {
		if (KitAPI.getStatManager().getLocalData(name.toLowerCase()) != null)
			return KitAPI.getStatManager().getLocalData(name.toLowerCase()).get(StatObjective.ELO);
		return STARTING_ELO;
	}

	/**
	 * Gets the color to display for player1
	 * 
	 * @param player1
	 *            the recipient of the color
	 * @param player2
	 *            the other player
	 * @return color
	 */
	public String getColor(int player1, int player2) {
		if (Math.abs(player1 - player2) < 76) {
			return "§6";
		}
		if (player1 > player2 + 75) {
			return "§a";
		}
		if (player2 > player1 + 75) {
			return "§c";
		}
		return "§6";
	}

	/**
	 * Sets the elo of the name
	 * 
	 * @param name
	 *            the name to set
	 * @param elo
	 *            elo to set to
	 */
	public void setElo(String name, int elo) {
		if (KitAPI.getStatManager().getLocalData(name.toLowerCase()) != null)
			KitAPI.getStatManager().getLocalData(name.toLowerCase()).set(StatObjective.ELO, elo);
		else
			KitAPI.getStatManager().setLocalData(name.toLowerCase(), new LocalPlayerData(name.toLowerCase(), new BasicDBObject("elo", elo)));
	}
}
