package net.frozenorb.KitPVP.ListenerSystem.Listeners;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.ListenerSystem.ListenerBase;
import net.frozenorb.KitPVP.RegionSysten.Region;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class GeneralListener extends ListenerBase {

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, e.getPlayer().getLocation())) {
			if (e.getItem().getItemStack().getType() != Material.MUSHROOM_SOUP) {
				e.getItem().remove();
				e.setCancelled(true);
			}
		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHitEvent(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.ARROW) {
			e.getEntity().remove();
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent e) {
		if ((e.getEntity() instanceof Player)) {
			Player p = (Player) e.getEntity();
			if (KitAPI.getPlayerManager().getProfile(p.getName()).isObject("adminMode"))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		if (e.getRegainReason() != RegainReason.MAGIC && e.getRegainReason() != RegainReason.MAGIC_REGEN && e.getRegainReason() != RegainReason.REGEN)
			e.setCancelled(true);
	}

}
