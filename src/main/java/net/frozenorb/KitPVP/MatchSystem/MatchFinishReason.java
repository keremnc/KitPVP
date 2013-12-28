package net.frozenorb.KitPVP.MatchSystem;

/**
 * Represents all of the possible ways a match can end
 * 
 * @author Kerem Celik
 * 
 */
public enum MatchFinishReason {

	/**
	 * Player death during a 1v1
	 */
	PLAYER_DEATH,

	/**
	 * Player logs out in the 1v1
	 */
	PLAYER_LOGOUT,

	/**
	 * Player teleports away from the 1v1 arena
	 */
	PLAYER_TELEPORT;
}
