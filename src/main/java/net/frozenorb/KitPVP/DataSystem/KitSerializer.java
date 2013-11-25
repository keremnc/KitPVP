package net.frozenorb.KitPVP.DataSystem;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.frozenorb.KitPVP.KitSystem.KitFactory;
import net.frozenorb.KitPVP.KitSystem.SerializableKit;
import net.frozenorb.Utilities.Serialization.Serializer;
import net.frozenorb.Utilities.Serialization.Serializers.InventorySerializer;
import net.frozenorb.Utilities.Serialization.Serializers.ItemStackSerializer;
import net.frozenorb.Utilities.Serialization.Serializers.PotionEffectSerializer;

public class KitSerializer implements Serializer<SerializableKit> {
	@Override
	public SerializableKit deserialize(BasicDBObject dbObject) {
		ArrayList<PotionEffect> pots = new ArrayList<PotionEffect>();
		BasicDBList potionEffets = (BasicDBList) dbObject.get("potions");
		for (Object o : potionEffets) {
			BasicDBObject preDes = (BasicDBObject) o;
			pots.add(new PotionEffectSerializer().deserialize(preDes));
		}
		String name = dbObject.getString("name");
		String permission = dbObject.getString("permission");
		String desc = dbObject.getString("description");
		String icon = dbObject.getString("icon");
		PotionEffect[] potValues = pots.toArray(new PotionEffect[] {});
		Inventory inv = new InventorySerializer().deserialize((BasicDBObject) dbObject.get("inventory"));
		ItemStack[] armour = new ItemStack[4];
		int current = 0;
		BasicDBList armor = (BasicDBList) dbObject.get("armor");
		for (Object o : armor) {
			BasicDBObject preDes = (BasicDBObject) o;
			armour[current] = new ItemStackSerializer().deserialize(preDes);
			current += 1;
		}
		return KitFactory.createKit(name, permission, desc, armour, inv.getContents(), potValues, Material.getMaterial(icon));
	}

	@Override
	public BasicDBObject serialize(SerializableKit o) {
		BasicDBObject kit = new BasicDBObject("name", o.getName()).append("description", o.getDescription()).append("icon", o.getKitIcon().getType().toString());
		kit.append("permission", o.getPermission());
		BasicDBList potions = new BasicDBList();
		for (PotionEffect pe : o.getPotionEffects()) {
			potions.add(new PotionEffectSerializer().serialize(pe));
		}
		kit.append("potions", potions);
		BasicDBList armor = new BasicDBList();
		for (ItemStack i : o.getArmorContents()) {
			armor.add(new ItemStackSerializer().serialize(i));
		}
		kit.append("armor", armor);
		Inventory i = Bukkit.createInventory(null, InventoryType.PLAYER);
		i.setContents(o.getInventoryContents());
		kit.append("inventory", new InventorySerializer().serialize(i));
		return kit;
	}

}
