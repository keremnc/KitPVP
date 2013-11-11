package net.frozenorb.KitPVP.DataSystem.Serialization.Transformers;

import java.util.ArrayList;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.frozenorb.KitPVP.KitSystem.CustomKit;
import net.frozenorb.Utilities.Serialization.Serializer;
import net.frozenorb.Utilities.Serialization.Serializers.InventorySerializer;
import net.frozenorb.Utilities.Serialization.Serializers.ItemStackSerializer;
import net.frozenorb.Utilities.Serialization.Serializers.PotionEffectSerializer;

public class CustomKitSerializer implements Serializer<CustomKit> {

	@Override
	public BasicDBObject serialize(CustomKit o) {
		BasicDBObject kit = new BasicDBObject("name", o.getKitName()).append("inventory", new InventorySerializer().serialize(o.getInventory()));
		BasicDBList armor = new BasicDBList();
		for (ItemStack itemArmor : o.getArmorContents()) {
			armor.add(new ItemStackSerializer().serialize(itemArmor));
		}
		kit.append("armor", armor);
		BasicDBList potions = new BasicDBList();
		for (PotionEffect pot : o.getKitPotionEffects()) {
			potions.add(new PotionEffectSerializer().serialize(pot));
		}
		kit.append("potions", potions);
		return kit;
	}

	@Override
	public CustomKit deserialize(final BasicDBObject dbobj) {

		ArrayList<PotionEffect> pots = new ArrayList<PotionEffect>();
		BasicDBList potionEffets = (BasicDBList) dbobj.get("potions");
		for (Object o : potionEffets) {
			BasicDBObject preDes = (BasicDBObject) o;
			pots.add(new PotionEffectSerializer().deserialize(preDes));
		}
		String name = dbobj.getString("name");
		PotionEffect[] potValues = pots.toArray(new PotionEffect[] {});
		Inventory inv = new InventorySerializer().deserialize((BasicDBObject) dbobj.get("inventory"));
		ItemStack[] armour = new ItemStack[4];
		int current = 0;
		BasicDBList armor = (BasicDBList) dbobj.get("armor");
		for (Object o : armor) {
			BasicDBObject preDes = (BasicDBObject) o;
			armour[current] = new ItemStackSerializer().deserialize(preDes);
			current += 1;
		}
		return new CustomKit(name, inv, armour, potValues);
	}
}
