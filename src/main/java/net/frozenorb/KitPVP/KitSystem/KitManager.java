package net.frozenorb.KitPVP.KitSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.DataSystem.Serialization.Transformers.CustomKitSerializer;
import net.frozenorb.KitPVP.Reflection.ClassGetter;
import net.frozenorb.mShared.Shared;

public class KitManager {
	private KitPVP plugin;
	private String packageName;
	private HashMap<String, Kit> playerKits = new HashMap<String, Kit>();
	private HashSet<String> packages = new HashSet<String>();
	private HashMap<String, HashSet<CustomKit>> customKits = new HashMap<String, HashSet<CustomKit>>();

	public KitManager(KitPVP plugin, String packageName) {
		this.plugin = plugin;
		this.packageName = packageName;
	}

	public boolean hasCustomKit(String name, String kit) {
		if (getCustomKits(name) != null) {
			for (CustomKit ckit : getCustomKits(name))
				if (ckit.getKitName().equalsIgnoreCase(kit))
					return true;
		}
		return false;
	}

	public CustomKit getCustomKitByName(String player, String name) {
		if (getCustomKits(player) != null) {
			for (CustomKit kit : getCustomKits(player))
				if (kit.getKitName().equalsIgnoreCase(name))
					return kit;
		}
		return null;
	}

	public void saveCustomKits(String name) {
		HashSet<CustomKit> kits = getCustomKits(name);
		if (kits != null) {
			try {
				File dataFolder = new File("data" + File.separator + "customKits");
				dataFolder.mkdirs();
				File f = new File(dataFolder + File.separator + name + ".json");
				if (!f.exists())
					f.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));
				BasicDBObject kitObjects = new BasicDBObject();
				BasicDBList kitsList = new BasicDBList();
				for (CustomKit k : kits) {
					kitsList.add(new CustomKitSerializer().serialize(k));
				}
				kitObjects.append("kits", kitsList);
				writer.write(Shared.get().getUtilities().formatDBObject(kitObjects));
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadCustomKits(String name) {
		File dataFolder = new File("data" + File.separator + "customKits");
		dataFolder.mkdirs();
		File f = new File(dataFolder + File.separator + name + ".json");
		if (f.exists()) {

			BufferedReader br = null;

			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(f));
				StringBuilder json = new StringBuilder();
				while ((sCurrentLine = br.readLine()) != null) {
					json.append(sCurrentLine);
				}
				BasicDBObject data = (BasicDBObject) JSON.parse(json.toString());
				if (data == null)
					return;
				BasicDBList kitList = (BasicDBList) data.get("kits");
				for (Object o : kitList) {
					CustomKit k = new CustomKitSerializer().deserialize((BasicDBObject) o);
					addCustomKit(k, name);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

		}
	}

	public HashMap<String, HashSet<CustomKit>> getCustomKits() {
		return customKits;
	}

	public void addCustomKit(CustomKit kitSaved, String name) {
		HashSet<CustomKit> kits;
		if (getCustomKits(name) != null)
			kits = (getCustomKits(name));
		else
			kits = new HashSet<CustomKit>();

		Iterator<CustomKit> iter = kits.iterator();
		while (iter.hasNext()) {
			CustomKit kit = iter.next();
			if (kit.getKitName().equalsIgnoreCase(kitSaved.getKitName())) {
				iter.remove();

			}
		}
		kits.add(kitSaved);
		customKits.put(name, kits);
	}

	public HashSet<CustomKit> getCustomKits(String name) {
		if (customKits.containsKey(name))
			return customKits.get(name);
		return new HashSet<CustomKit>();
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
