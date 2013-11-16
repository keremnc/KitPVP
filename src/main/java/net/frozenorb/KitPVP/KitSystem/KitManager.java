package net.frozenorb.KitPVP.KitSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.Reflection.ClassGetter;

public class KitManager {
	private KitPVP plugin;
	private String packageName;
	private HashMap<String, Kit> playerKits = new HashMap<String, Kit>();
	private HashSet<String> packages = new HashSet<String>();

	public KitManager(KitPVP plugin, String packageName) {
		this.plugin = plugin;
		this.packageName = packageName;
	}

	public HashMap<String, Kit> getKitsOnPlayers() {
		return playerKits;
	}

	public Kit getByName(String str) {
		for (Kit k : KitPVP.getKits()) {
			if (k.getName().equalsIgnoreCase(str))
				return k;
		}
		for (Kit k : KitPVP.getKits()) {
			if (k.getName().toLowerCase().startsWith(str.toLowerCase()))
				return k;
		}
		return null;
	}

	public Kit getKitOnPlayer(String str) {
		if (hasKitOn(str)) {
			return playerKits.get(str);
		}
		return null;
	}

	public boolean hasKitOn(String str) {
		return playerKits.containsKey(str);
	}

	@SuppressWarnings({ "rawtypes" })
	public void loadKits() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
		for (Class perkClass : ClassGetter.getClassesForPackage(plugin, packageName)) {
			if (Kit.class.isAssignableFrom(perkClass)) {
				if (!Modifier.isAbstract(perkClass.getModifiers())) {
					if ((perkClass.getCanonicalName() != null))
						System.out.println("Loading kits from class:" + "\n--------------------------------------------------------------------------------\n" + perkClass.getCanonicalName() + "\n--------------------------------------------------------------------------------\n");
					Constructor ctor = perkClass.getConstructors()[0];
					ctor.setAccessible(true);
					Kit kit = (Kit) ctor.newInstance();
					plugin.registerKit(kit);
				}
			}
		}
		for (String pack : packages) {
			for (Class perkClass : ClassGetter.getClassesForPackage(plugin, pack)) {
				if (Kit.class.isAssignableFrom(perkClass)) {
					if (!Modifier.isAbstract(perkClass.getModifiers())) {
						if ((perkClass.getCanonicalName() != null))
							System.out.println("Loading kits from class:" + "\n--------------------------------------------------------------------------------\n" + perkClass.getCanonicalName() + "\n--------------------------------------------------------------------------------\n");
						Constructor ctor = perkClass.getConstructors()[0];
						ctor.setAccessible(true);
						Kit kit = (Kit) ctor.newInstance();
						plugin.registerKit(kit);
					}
				}

			}
		}
		Collections.sort(KitPVP.getKits(), new Comparator<Kit>() {
			@Override
			public int compare(Kit o1, Kit o2) {
				return ((Integer) o1.getWeight()).compareTo(o2.getWeight());
			}
		});
	}

	/**
	 * Registers a package to be loaded via reflection. Use externally, or use in a seperate package
	 * 
	 * @param k
	 *            the kit whose package will be registered
	 */
	public void registerPackage(Kit k) {
		String pkg = k.getClass().getPackage().getName();
		packages.add(pkg);

	}
}
