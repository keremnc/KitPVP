package net.frozenorb.KitPVP.Minigames;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.mongodb.BasicDBObject;

import net.frozenorb.Arcade.ArcadeAPI;
import net.frozenorb.Arcade.GameSystem.BaseMinigame;
import net.frozenorb.Arcade.Types.Cuboid;
import net.frozenorb.KitPVP.KitPVP;

public class SpleefMinigame extends BaseMinigame {
	Cuboid region = null;
	private HashMap<Vector, String> minedBlock = new HashMap<Vector, String>();
	private HashMap<String, Integer> spleefs = new HashMap<String, Integer>();
	private int yLevel;

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
			for (Block b : c) {
				b.setType(Material.SNOW_BLOCK);
			}
			yLevel = y1;
			if (c.getLowerX() == 0) {
				System.out.println("Error finding region. Didn't define the " + getName() + " cuboid? (data.json didn't have cuboid)");
				return;
			}
			this.region = c;
		} else
			System.out.println("Error finding region. Didn't define the " + getName() + " cuboid? (data.json didn't have cuboid)");
	}

	@Override
	public String getName() {
		return "Spleef";
	}

	@Override
	public void onWin(Player p) {
		Bukkit.broadcastMessage(ChatColor.GRAY + "========================================");
		Bukkit.broadcastMessage("§l§a" + p.getDisplayName() + "§l§a has won the Spleef event!");
		Bukkit.broadcastMessage(ChatColor.GRAY + "========================================");

	}

	@Override
	public int getRequiredPlayers() {
		return 0;
	}

	@Override
	public void startGame() {
		Bukkit.broadcastMessage(ChatColor.AQUA + getName() + "§e has started. There are §b" + getPlayers().size() + "§e players playing!");
	}

	@Override
	public boolean canStart() {
		return true;
	}

	/*
	 * --------------LISTENERS-------------
	 */

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent e) {
		if (isInProgress() && getPlayers().contains(e.getPlayer().getName())) {
			if (e.getBlock().getType() == Material.SNOW_BLOCK) {
				e.getBlock().setType(Material.AIR);
				minedBlock.put(new Vector(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()), e.getPlayer().getName());
				Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

					@Override
					public void run() {
						e.getBlock().setType(Material.SNOW_BLOCK);
						e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, Material.SNOW_BLOCK);
					}
				}, 140L);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (!getPlayers().contains(e.getPlayer().getName()))
			return;

		if (e.getTo().distance(e.getFrom()) > 0.1 && e.getPlayer().getLocation().getY() < yLevel - 2) {
			Location loc = e.getPlayer().getLocation();
			if (loc.getWorld().getBlockAt(loc.getBlockX(), yLevel, loc.getBlockZ()).getType() != Material.SNOW_BLOCK) {
				if (minedBlock.containsKey(new Vector(loc.getBlockX(), yLevel, loc.getBlockZ()))) {
					sendToAllPlayers("§e" + e.getPlayer().getDisplayName() + "§e was just spleefed by §a" + minedBlock.get(new Vector(loc.getBlockX(), yLevel, loc.getBlockZ()) + "§e!"));
					spleefs.put(minedBlock.get(new Vector(loc.getBlockX(), yLevel, loc.getBlockZ())), spleefs.get(minedBlock.get(new Vector(loc.getBlockX(), yLevel, loc.getBlockZ()))) + 1);
					getPlayers().remove(e.getPlayer().getName());
				} else {
					sendToAllPlayers("§e" + e.getPlayer().getDisplayName() + "§e was spleefed!");
					getPlayers().remove(e.getPlayer().getName());
				}
				if (getPlayers().size() == 1) {

				}
			}
		}

	}

	@Override
	public void onSecond() {
	}
}
