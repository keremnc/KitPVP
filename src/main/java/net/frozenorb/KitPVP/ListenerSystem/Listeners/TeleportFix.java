package net.frozenorb.KitPVP.ListenerSystem.Listeners;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityTracker;
import net.minecraft.server.v1_7_R1.EntityTrackerEntry;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeleportFix {

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

}