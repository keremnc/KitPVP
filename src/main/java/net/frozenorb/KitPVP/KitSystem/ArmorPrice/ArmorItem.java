package net.frozenorb.KitPVP.KitSystem.ArmorPrice;

public class ArmorItem {
	private String name;
	private int price;
	private ArmorType type;

	public ArmorItem(String name, int price, ArmorType type) {
		this.type = type;
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public ArmorType getType() {
		return type;
	}

}
