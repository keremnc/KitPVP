package net.frozenorb.KitPVP.MatchSystem.Queue;

import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;

import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;

public class MatchQueue {
	private Loadout type;
	private QueueType qType;
	private Player player;

	public MatchQueue(Player player, Loadout type, QueueType qType) {
		this.qType = qType;
		this.player = player;
		this.type = type;

	}

	public Player getPlayer() {
		return player;
	}

	public Loadout getLoadout() {
		return type;
	}

	public QueueType getQueueType() {
		return qType;
	}

	public String getQueueTypeName() {
		switch (qType) {
		case QUICK:
			return "Unranked";
		case UNRANKED:
			return "Unranked";
		case RANKED:
			return "Ranked";
		default:
			return "Undetermined";
		}
	}

	@Override
	public String toString() {
		return new BasicDBObject("player", player.getName()).append("type", type.getName()).append("qtype", qType.toString()).toString();
	}
}
