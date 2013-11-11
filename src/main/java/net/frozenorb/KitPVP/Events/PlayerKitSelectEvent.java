package net.frozenorb.KitPVP.Events;

import net.frozenorb.KitPVP.KitSystem.Kit;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerKitSelectEvent extends PlayerEvent {

	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private Kit kit;

	public PlayerKitSelectEvent(Player who, Kit kit) {
		super(who);
		this.kit = kit;
	}

	public Kit getKit() {
		return kit;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
