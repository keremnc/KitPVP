package net.frozenorb.KitPVP.StatSystem.Elo;

public class DefaultEloCalculator implements EloCalculator {

	private KFactor kFactor;

	public DefaultEloCalculator() {
		this.kFactor = new SimpleKFactor();
	}

	@Override
	public Matchup calculate(Result res, Matchup match) {

		double QA = Math.pow(10, match.playerOne / 400);
		double QB = Math.pow(10, match.playerTwo / 400);

		double kA = kFactor.getKValue(match.playerOne);
		double kB = kFactor.getKValue(match.playerTwo);

		double EA = QA / (QA + QB);
		double EB = QB / (QA + QB);

		double SA = 0.5;
		double SB = 0.5;
		if (res == Result.PLAYER_ONE) {
			SA = 1;
			SB = 0;
		} else if (res == Result.PLAYER_TWO) {
			SA = 0;
			SB = 1;
		}

		double s1 = match.playerOne + (kA * (SA - EA));
		double s2 = match.playerTwo + (kB * (SB - EB));

		return new Matchup(s1, s2);
	}

	public static void main(String[] args) {
		DefaultEloCalculator d = new DefaultEloCalculator();
		System.out.println(d.calculate(Result.PLAYER_ONE, new Matchup(1500, 1500)));
	}
}
