package net.frozenorb.KitPVP.VisualSystem;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_7_R1.DataWatcher;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityEnderDragon;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class BossBarManager implements Runnable {
	private static final int ENTITY_ID_MODIFIER = 1236912369;

	private HashMap<String, String[]> messages = new HashMap<String, String[]>();
	private HashMap<String, Integer> currentValue = new HashMap<String, Integer>();

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers())
			setBar(p);
	}

	/**
	 * Updates the boss bar of a player, and sets to the current string
	 * 
	 * @param p
	 *            the player to set to
	 */
	public void setBar(Player p) {
		if (messages.containsKey(p.getName())) {
			if (currentValue.get(p.getName()) >= messages.get(p.getName()).length)
				currentValue.put(p.getName(), 0);
			String display = messages.get(p.getName())[currentValue.get(p.getName())];
			spawnNewPlate(p, display);
			currentValue.put(p.getName(), currentValue.get(p.getName()) + 1);
		} else
			currentValue.put(p.getName(), 0);
	}

	/**
	 * Adds the array of String to the player, and sets their bar to the first string
	 * 
	 * @param player
	 *            the player to register String to
	 * @param strings
	 *            the array of String
	 */
	public void registerStrings(Player player, String[] strings) {
		messages.put(player.getName(), strings);
		currentValue.put(player.getName(), 0);
		PacketPlayOutEntityDestroy pac = new PacketPlayOutEntityDestroy(player.getEntityId() + ENTITY_ID_MODIFIER);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(pac);
		setBar(player);
	}

	/**
	 * Removes a player from the bar map, and removes the bar
	 * 
	 * @param player
	 *            the player to remove the bar on
	 */
	public void unregisterPlayer(Player player) {
		messages.remove(player.getName());
		PacketPlayOutEntityDestroy pac = new PacketPlayOutEntityDestroy(player.getEntityId() + ENTITY_ID_MODIFIER);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(pac);
	}

	/*
	 * ------------PRIVATE PACKET METHODS---------------
	 */
	private void spawnNewPlate(Player player, String display) {
		displayTextBar(display, player);
	}

	private void sendPacket(Player player, Packet packet) {
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		entityPlayer.playerConnection.sendPacket(packet);
	}

	private PacketPlayOutSpawnEntityLiving getMobPacket(Player p, String text, Location loc) {
		PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving();
		final EntityEnderDragon dragon = new EntityEnderDragon(((CraftWorld) p.getWorld()).getHandle());
		int x = (int) Math.floor(loc.getBlockX() * 32.0D);
		int y = (int) Math.floor(0);
		int z = (int) Math.floor(loc.getBlockZ() * 32.0D);
		try {
			/* id */
			Field cID = mobPacket.getClass().getDeclaredField("a");
			cID.setAccessible(true);
			cID.set(mobPacket, (int) p.getEntityId() + ENTITY_ID_MODIFIER);
			cID.setAccessible(false);
			/* name */
			Field cName = mobPacket.getClass().getDeclaredField("b");
			cName.setAccessible(true);
			cName.set(mobPacket, EntityType.ENDER_DRAGON.getTypeId());
			cName.setAccessible(false);
			/* x */
			Field cF = mobPacket.getClass().getDeclaredField("c");
			cF.setAccessible(true);
			cF.set(mobPacket, x);
			cF.setAccessible(false);
			/* y */
			Field cY = mobPacket.getClass().getDeclaredField("d");
			cY.setAccessible(true);
			cY.set(mobPacket, y);
			cY.setAccessible(false);
			/* z */
			Field cZ = mobPacket.getClass().getDeclaredField("e");
			cZ.setAccessible(true);
			cZ.set(mobPacket, z);
			cZ.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		DataWatcher watcher = getWatcher(text, dragon);
		try {
			Field t = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
			t.setAccessible(true);
			t.set(mobPacket, watcher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mobPacket;
	}

	private DataWatcher getWatcher(String text, Entity e) {
		DataWatcher watcher = new DataWatcher(e);
		watcher.a(0, (Byte) (byte) 0x20);
		watcher.a(10, (String) text);
		watcher.a(11, (Byte) (byte) 1);
		return watcher;
	}

	private void displayTextBar(String text, final Player player) {
		PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(player, text, player.getLocation());
		sendPacket(player, mobPacket);
	}
}
