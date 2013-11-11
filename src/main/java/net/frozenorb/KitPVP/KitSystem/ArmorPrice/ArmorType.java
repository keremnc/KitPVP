package net.frozenorb.KitPVP.KitSystem.ArmorPrice;

public enum ArmorType {
	ITEM("item"), ENCHANTMENT("enchantment"), POTION("potion");
	private String name;

	private ArmorType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ArmorType parse(String str) {
		for (ArmorType armor : values())
			if (armor.getName().equalsIgnoreCase(str))
				return armor;
		for (ArmorType armor : values())
			if (armor.toString().equalsIgnoreCase(str))
				return armor;
		return null;
	}
}
