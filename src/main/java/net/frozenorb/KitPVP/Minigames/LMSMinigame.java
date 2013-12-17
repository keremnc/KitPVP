package net.frozenorb.KitPVP.Minigames;

import java.util.HashMap;
import java.util.Random;

import net.frozenorb.Arcade.ArcadeAPI;
import net.frozenorb.Arcade.GameSystem.BaseMinigame;
import net.frozenorb.Arcade.Types.Cuboid;
import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.Basic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import com.mongodb.BasicDBObject;

public class LMSMinigame extends BaseMinigame {

	public HashMap<String, Integer> kills = new HashMap<String, Integer>();
	public Cuboid region;
	public boolean CAN_FIGHT = false;
	public double price = 5000;

	@Override
	public void refresh() {
		BasicDBObject jsondata = ArcadeAPI.get().getMinigameManager().getJsonData().get(this);
		if (jsondata.containsField(getName())) {
			BasicDBObject json = (BasicDBObject) jsondata.get(getName());
			int x1 = 0, y1 = 0, z1 = 0;
			if (json.containsField("x1"))
				x1 = json.getInt("x1");
			if (json.containsField("y1"))
				y1 = json.getInt("y1");
			if (json.containsField("z1"))
				z1 = json.getInt("z1");
			int x2 = 0, y2 = 0, z2 = 0;
			if (json.containsField("x2"))
				x2 = json.getInt("x2");
			if (json.containsField("y2"))
				y2 = json.getInt("y2");
			if (json.containsField("z2"))
				z2 = json.getInt("z2");
			Cuboid c = new Cuboid(Bukkit.getWorld("world"), x1, y1, z1, x2, y2, z2);
			if (c.getLowerX() == 0) {
				System.out.println("Error finding region. Didn't define the " + getName() + " cuboid? (data.json didn't have cuboid)");
				return;
			}
			this.region = c;
		} else
			System.out.println("Error finding region. Didn't define the " + getName() + " cuboid? (data.json didn't have cuboid)");
	}

	public void teleportToRandom(Player p) {
		int randX = randomBetween(region.getLowerX(), region.getUpperX());
		int randZ = randomBetween(region.getLowerZ(), region.getUpperZ());
		int yVal = p.getWorld().getHighestBlockYAt(randX, randZ);
		Block b = p.getWorld().getBlockAt(randX, yVal - 1, randZ);
		Location loc = new Location(p.getWorld(), randX, yVal - 1, randZ);
		if (b.getType() == Material.AIR || b.getType() == Material.LEAVES || b.getType() == Material.TNT || b.isLiquid() || b.getRelative(BlockFace.UP).getType() == Material.FIRE) {

			teleportToRandom(p);
		} else {
			p.teleport(loc.add(new Vector(0, 2, 0)));
		}
	}

	int randomBetween(int min, int max) {
		int range = max - min;
		return min + (int) (Math.random() * (range));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void startGame() {
		this.price = getPlayers().size() * 100;

		Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

			@Override
			public void run() {
				CAN_FIGHT = true;
				for (String n : getPlayers()) {
					if (Bukkit.getPlayerExact(n) != null) {
						Player p = Bukkit.getPlayerExact(n);
						p.setGameMode(GameMode.SURVIVAL);
						p.chat("/vis");
						KitAPI.getKitManager().getKitsOnPlayers().remove(p.getName());
						p.sendMessage(ChatColor.GREEN + "You may now start fighting.");
						p.updateInventory();
					}
				}
			}
		}, 100L);

		for (String n : getPlayers()) {
			if (Bukkit.getPlayerExact(n) != null) {

				Player p = Bukkit.getPlayerExact(n);
				KitAPI.getPlayerManager().removeSpawnProtection(p);
				p.getInventory().setHeldItemSlot(0);
				teleportToRandom(p);
				Core.get().clearPlayer(p);
				p.sendMessage(ChatColor.YELLOW + "You may start to fight after the 5 second grace period.");
				KitAPI.getKitManager().getByName("PVP").applyKit(p);

			}
		}
	}

	@Override
	public String getName() {
		return "LMS";
	}

	public void removePlayer(final Player p, String info, boolean isOnline, String name) {
		boolean should = isOnline;
		if (should) {
			p.setGameMode(GameMode.SURVIVAL);
		}
		KitAPI.getPlayerManager().removeSpawnProtection(p);
		getPlayers().remove(should ? p.getName() : name);
		if (should) {
			p.sendMessage(ChatColor.DARK_AQUA + p.getDisplayName() + ChatColor.GREEN + " " + info);

			for (Player pla : Bukkit.getOnlinePlayers()) {
				if (getPlayers().contains(pla.getName())) {
					pla.sendMessage(ChatColor.DARK_AQUA + p.getDisplayName() + ChatColor.GREEN + " " + info);
				}
			}
		} else {
			for (Player pla : Bukkit.getOnlinePlayers()) {
				if (getPlayers().contains(pla.getName())) {
					pla.sendMessage(ChatColor.DARK_AQUA + name + ChatColor.GREEN + " logged out.");
				}
			}
		}
		if (getPlayers().size() == 1) {

			final Player winner = Bukkit.getPlayerExact(getPlayers().toArray(new String[1])[0]);

			for (int i = 0; i < 7; i += 1) {
				Firework fw = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
				Random r = new Random();
				FireworkMeta fwm = fw.getFireworkMeta();
				int rt = r.nextInt(4) + 1;
				Type type = Type.BALL;
				if (rt == 1)
					type = Type.BALL;
				if (rt == 2)
					type = Type.BALL_LARGE;
				if (rt == 3)
					type = Type.BURST;
				if (rt == 4)
					type = Type.CREEPER;
				if (rt == 5)
					type = Type.STAR;
				int r1i = r.nextInt(17) + 1;
				int r2i = r.nextInt(17) + 1;
				Color c1 = getColor(r1i);
				Color c2 = getColor(r2i);
				FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
				fwm.addEffect(effect);
				int rp = r.nextInt(2) + 1;
				fwm.setPower(rp);
				fw.setFireworkMeta(fwm);
			}
			Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

				@Override
				public void run() {
					winner.teleport(KitAPI.getServerManager().getSpawn());
					winner.getInventory().setArmorContents(null);
					KitAPI.getPlayerManager().giveSpawnProtection(winner);
					Core.get().clearPlayer(winner);

				}
			}, 60L);
			Basic.get().getEconomyManager().depositPlayer(winner.getName(), (int) price);
			Bukkit.broadcastMessage(ChatColor.RED + getPlayers().toArray(new String[1])[0] + " wins the LMS event!");
			end();
		}
		if (getPlayers().size() == 0)
			end();

	}

	public HashMap<String, Integer> getStats() {
		return kills;
	}

	@EventHandler
	public void onl(PlayerQuitEvent e) {
		if (getPlayers().contains(e.getPlayer().getName()))

			removePlayer(e.getPlayer(), e.getPlayer().getName(), false, e.getPlayer().getName());
	}

	@EventHandler
	public void onsl(PlayerKickEvent e) {
		if (getPlayers().contains(e.getPlayer().getName()))
			removePlayer(e.getPlayer(), e.getPlayer().getName(), false, e.getPlayer().getName());
	}

	@EventHandler
	public void onEnt(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (getPlayers().contains(((Player) e.getEntity()).getName()))
				if (!CAN_FIGHT)
					e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (isInProgress())
			if (getPlayers().contains(p.getName())) {
				if (p.getKiller() != null && p.getKiller() instanceof Player) {
					Player killer = p.getKiller();
					String[] msgs = new String[] { "destroyed", "pummeled", "murdered", "struck down" };
					Random rand = new Random();
					if (kills.containsKey(killer.getName())) {
						kills.put(killer.getName(), kills.get(killer.getName()) + 1);
					} else {
						kills.put(killer.getName(), 1);
					}
					removePlayer(p, "was " + msgs[rand.nextInt(msgs.length)] + " by " + killer.getDisplayName() + ChatColor.GREEN + ".", true, p.getName());
				} else {
					removePlayer(p, "was killed.", true, p.getName());
				}
			}
	}

	Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}
		return c;
	}

	@Override
	public void onSecond() {
	}

	@Override
	public void handleDeath(Player p) {
		p.teleport(KitAPI.getServerManager().getSpawn());
	}

	@Override
	public int getRequiredPlayers() {
		return 2;
	}

	@Override
	public boolean canStart() {
		return true;
	}

	@Override
	public void addPlayerToGame(Player p) {
		kills.put(p.getName(), 0);
	}

	@Override
	public void onWin(Player p) {

		Bukkit.broadcastMessage(ChatColor.GRAY + "========================================");
		Bukkit.broadcastMessage("§l§a" + p.getDisplayName() + "§l§a has won the LMS event!");
		Bukkit.broadcastMessage(ChatColor.GRAY + "========================================");

	}

}