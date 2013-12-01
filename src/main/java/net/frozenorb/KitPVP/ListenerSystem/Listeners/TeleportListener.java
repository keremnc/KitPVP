package net.frozenorb.KitPVP.ListenerSystem.Listeners;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.KitPVP.API.KitAPI;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityTracker;
import net.minecraft.server.v1_7_R1.EntityTrackerEntry;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

	private final int TELEPORT_FIX_DELAY = 9; // perf number, do not fuck wid it

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getTo().distance(event.getFrom()) > 10) {
			final Player player = event.getPlayer();
			if (KitAPI.getMatchManager().isInMatch(player.getName()) && KitAPI.getMatchManager().getCurrentMatches().get(player.getName()).isInProgress()) {
				final int visibleDistance = Bukkit.getViewDistance() * 16;

				Bukkit.getScheduler().scheduleSyncDelayedTask(KitAPI.getKitPVP(), new Runnable() {
					@Override
					public void run() {
						updateEntities(getPlayersWithinRange(player, visibleDistance));

					}
				}, TELEPORT_FIX_DELAY);
			}
		}
	}

	public void updateEntities(List<Player> observers) {

		for (Player player : observers) {
			updateEntity(player, observers);
		}
	}

	@SuppressWarnings("unchecked")
	public void updateEntity(Entity entity, List<Player> observers) {

		World world = entity.getWorld();
		WorldServer worldServer = ((CraftWorld) world).getHandle();

		EntityTracker tracker = worldServer.tracker;
		EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(entity.getEntityId());

		List<EntityPlayer> nmsPlayers = getNmsPlayers(observers);
		entry.trackedPlayers.removeAll(nmsPlayers);
		entry.scanPlayers(nmsPlayers);
	}

	private List<EntityPlayer> getNmsPlayers(List<Player> players) {
		List<EntityPlayer> nmsPlayers = new ArrayList<EntityPlayer>();

		for (Player bukkitPlayer : players) {
			CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
			nmsPlayers.add(craftPlayer.getHandle());
		}

		return nmsPlayers;
	}

	private List<Player> getPlayersWithinRange(Player player, int distance) {
		List<Player> res = new ArrayList<Player>();
		int d2 = distance * distance;

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld() == player.getWorld() && p.getLocation().distanceSquared(player.getLocation()) <= d2) {
				res.add(p);
			}
		}

		return res;
	}
}