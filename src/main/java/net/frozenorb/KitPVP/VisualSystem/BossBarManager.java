package net.frozenorb.KitPVP.VisualSystem;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_6_R3.DataWatcher;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet24MobSpawn;
import net.minecraft.server.v1_6_R3.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BossBarManager implements Runnable {
	private HashMap<String, String[]> messages = new HashMap<String, String[]>();
	private HashMap<String, Integer> currentValue = new HashMap<String, Integer>();
	public static final int ENTITY_ID_MODIFIER = 1236912369;

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers())
			setBar(p);
	}

	public void setBar(Player p) {
		if (messages.containsKey(p.getName())) {
			if (currentValue.get(p.getName()) >= messages.get(p.getName()).length) {
				currentValue.put(p.getName(), 0);
			}
			String display = messages.get(p.getName())[currentValue.get(p.getName())];
			spawnNewPlate(p, display);
			currentValue.put(p.getName(), currentValue.get(p.getName()) + 1);
		} else
			currentValue.put(p.getName(), 0);
	}

	public void registerStrings(Player player, String[] strings) {
		messages.put(player.getName(), strings);
		currentValue.put(player.getName(), 0);
		Packet29DestroyEntity pac = new Packet29DestroyEntity();
		pac.a = new int[] { player.getEntityId() + ENTITY_ID_MODIFIER };
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(pac);
		setBar(player);
	}

	public void unregisterPlayer(Player player) {
		messages.remove(player.getName());
		Packet29DestroyEntity pac = new Packet29DestroyEntity();
		pac.a = new int[] { player.getEntityId() + ENTITY_ID_MODIFIER };
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(pac);
	}

	private void spawnNewPlate(Player player, String display) {
		new BossBarSetter(player).displayTextBar(display, player);
	}

	private static class BossBarSetter {
		Player p;

		public BossBarSetter(Player p) {
			this.p = p;
		}

		public void sendPacket(Player player, Packet packet) {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			entityPlayer.playerConnection.sendPacket(packet);
		}

		public Packet24MobSpawn getMobPacket(String text, Location loc) {
			Packet24MobSpawn mobPacket = new Packet24MobSpawn();
			mobPacket.a = (int) p.getEntityId() + ENTITY_ID_MODIFIER; // Entity ID
			mobPacket.b = (byte) EntityType.ENDER_DRAGON.getTypeId(); // Mob type (ID: 64)
			mobPacket.c = (int) Math.floor(loc.getBlockX() * 32.0D); // X position
			mobPacket.d = (int) Math.floor(0); // Y position
			mobPacket.e = (int) Math.floor(loc.getBlockZ() * 32.0D); // Z position
			DataWatcher watcher = getWatcher(text);
			try {
				Field t = Packet24MobSpawn.class.getDeclaredField("t");
				t.setAccessible(true);
				t.set(mobPacket, watcher);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mobPacket;
		}

		public DataWatcher getWatcher(String text) {
			DataWatcher watcher = new DataWatcher();
			watcher.a(0, (Byte) (byte) 0x20);
			watcher.a(10, (String) text);
			watcher.a(11, (Byte) (byte) 1);
			return watcher;
		}

		public void displayTextBar(String text, final Player player) {
			Packet24MobSpawn mobPacket = getMobPacket(text, player.getLocation());
			sendPacket(player, mobPacket);
		}
	}
}
