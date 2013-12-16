package net.frozenorb.KitPVP.StatSystem.Elo;

public interface EloCalculator {

	/**
	 * Represents two player's ratings
	 * 
	 * @author Kerem Celik
	 * 
	 */
	public static class Matchup {
		public final double playerOne;
		public final double playerTwo;

		public Matchup(double playerOne, double playerTwo) {
			this.playerOne = playerOne;
			this.playerTwo = playerTwo;
		}

		@Override
		public String toString() {
			return playerOne + ":" + playerTwo;
		}

	}

	/**
	 * Represents the winner of the Matchup
	 * 
	 * @author Kerem Celik
	 * 
	 */
	public static enum Result {
		PLAYER_ONE, PLAYER_TWO
	}

	/**
	 * Gets the Matchup of ratings that are returned after a match
	 * 
	 * @param res
	 *            the result of the match
	 * @param match
	 *            the match that was played
	 * @return matchup
	 */
	public Matchup calculate(Result res, Matchup match);

}
