package net.frozenorb.KitPVP.Minigames;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.Arcade.ArcadeAPI;
import net.frozenorb.Arcade.GameSystem.Minigame;
import net.frozenorb.Arcade.GameSystem.MinigameManager;
import net.frozenorb.KitPVP.API.KitAPI;

public class KitMinigameManager extends MinigameManager {

	public KitMinigameManager() {
		Minigame lms = new LMSMinigame();
		Minigame spleef = new SpleefMinigame();
		registerMinigame(lms);
		registerMinigame(spleef);
		loadData(spleef);
		loadData(lms);
	}

	@Override
	public void preparePlayer(Player p) {
		if (KitAPI.getPlayerManager().hasSpawnProtection(p) || (ArcadeAPI.get().getMinigameManager().getCurrentMinigame() != null && ArcadeAPI.get().getMinigameManager().getCurrentMinigame().getTime() < -6))
			return;
		p.sendMessage(ChatColor.YELLOW + "You were slain for not having spawn protection.");
		p.setHealth(0D);
		KitAPI.getServerManager().handleRespawn(p);

	}

}
