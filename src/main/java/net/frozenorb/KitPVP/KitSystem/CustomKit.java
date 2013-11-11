package net.frozenorb.KitPVP.KitSystem;

import java.util.Arrays;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Events.PlayerKitSelectEvent;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class CustomKit implements Kit {

	private String name;
	private Inventory inventory;
	private ItemStack[] armor;
	private PotionEffect[] potionEffects;
	private int price;

	public CustomKit(String name, Inventory inventory, ItemStack[] armor, PotionEffect[] potions) {
		this.name = name;
		this.inventory = inventory;
		this.armor = armor;
		this.potionEffects = potions;
		this.price = 0;
	}

	public ItemStack[] getArmorContents() {
		return armor;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public String getKitName() {
		return name;
	}

	public PotionEffect[] getKitPotionEffects() {
		return potionEffects;
	}

	public int getPrice() {
		return price;
	}

	public CustomKit setPrice(int price) {
		this.price = price;
		return this;
	}

	@SuppressWarnings("deprecation")
	public void equip(Player p) {
		if (KitAPI.getKitManager().hasKitOn(p.getName())) {
			p.sendMessage(ChatColor.RED + "You may only use one kit per life!");
			return;
		}
		PlayerKitSelectEvent e = new PlayerKitSelectEvent(p, this);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCancelled())
			return;
		Core.get().clearPlayer(p);
		p.addPotionEffects(Arrays.asList(getKitPotionEffects()));
		p.getInventory().setArmorContents(getArmorContents());
		p.getInventory().setContents(getInventory().getContents());
		for (int i = 1; i < 36; i += 1)
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		KitAPI.getKitManager().getKitsOnPlayers().put(p.getName(), this);
		p.sendMessage("§6You have equipped the kit §b" + getKitName() + "§6 for §f" + getPrice() + "§6 credits.");
		p.updateInventory();
	}

	@Override
	public String getDescription() {
		return "Price: " + getPrice() + " credits";
	}

	@Override
	public Listener getListener() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPermission() {
		return "kitpvp.default";
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] {};
	}

	@Override
	public int getWeight() {
		return Integer.MAX_VALUE;
	}

	@Override
	public PlayerInventory transformInventory(PlayerInventory inv) {
		return inv;
	}

	@Override
	public ItemStack getKitIcon() {
		return new ItemStack(Material.BEDROCK);
	}

	@Override
	public boolean hasKit(Player p) {
		return true;
	}

}
