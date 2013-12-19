package net.frozenorb.KitPVP.StatSystem.Elo;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.KitPVP.StatSystem.Elo.EloCalculator.Matchup;
import net.frozenorb.KitPVP.StatSystem.Elo.EloCalculator.Result;

/**
 * Elo manager class to handle rankings in ranked 1v1
 * 
 * @since 11/6/2013
 * @author Kerem
 * 
 */
public class EloManager {
	public static int STARTING_ELO = 1500; // the starting elo for new players
	public static int MAX_CHANGE = 20; // max gain/loss a player can get in one match
	public static int PROVISIONAL = 6;
	private EloCalculator eloCalculator;

	public EloManager() {
		this.eloCalculator = new DefaultEloCalculator();
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
	public int[] getNewElo(int winner, int loser, int winnerPlayed, int loserPlayed) {
		Matchup result = eloCalculator.calculate(Result.PLAYER_ONE, new Matchup(winner, loser));
		int winnerGain = (int) (result.playerOne - winner);
		int loserGain = (int) (result.playerTwo - loser);
		if (loserGain < -MAX_CHANGE)
			loserGain = -MAX_CHANGE;
		if (winnerGain > MAX_CHANGE + 5)
			winnerGain = MAX_CHANGE + 5;
		if (loserPlayed < PROVISIONAL)
			loserGain /= 1.3;
		if (winnerPlayed < PROVISIONAL)
			winnerGain *= 1.3;
		int[] ret = new int[2];
		ret[0] = winner + winnerGain;
		ret[1] = loser + loserGain;
		return ret;
	}

	/**
	 * Gets the rating of the player
	 * 
	 * @param name
	 *            the name of the player
	 * @return elo
	 */
	public int getElo(String name) {
		if (KitAPI.getStatManager().getStat(name.toLowerCase()) != null)
			return KitAPI.getStatManager().getStat(name.toLowerCase()).get(StatObjective.ELO);
		return STARTING_ELO;
	}

	/**
	 * Sets the rating of the name
	 * 
	 * @param name
	 *            the name to set
	 * @param elo
	 *            elo to set to
	 */
	public void setElo(String name, int elo) {
		if (KitAPI.getStatManager().getStat(name.toLowerCase()) != null)
			KitAPI.getStatManager().getStat(name.toLowerCase()).set(StatObjective.ELO, elo);
		else
			KitAPI.getStatManager().loadStats(name);
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
}
