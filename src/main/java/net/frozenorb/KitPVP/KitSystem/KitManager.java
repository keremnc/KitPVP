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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.Utilities.Core;

public class KitManager {
	private String packageName;
	private HashMap<String, Kit> playerKits = new HashMap<String, Kit>();
	private HashSet<String> packages = new HashSet<String>();
	private HashMap<Kit, HashMap<String, Long>> cooldowns = new HashMap<Kit, HashMap<String, Long>>();

	public KitManager(String packageName) {
		this.packageName = packageName;
	}

	public HashMap<String, Kit> getKitsOnPlayers() {
		return playerKits;
	}

	public Kit getByName(String str) {
		for (Kit k : KitPVP.getKits()) {
			if (k.getName().equalsIgnoreCase(str)) {
				return k;
			}
		}
		for (Kit k : KitPVP.getKits()) {
			if (k.getName().toLowerCase().startsWith(str.toLowerCase())) {
				return k;
			}
		}
		return null;
	}

	public void saveToFile() {
		try {
			new File("data").mkdir();
			File f = new File("data" + File.separator + "persistentKits.json");
			f.createNewFile();
			BasicDBObject kits = new BasicDBObject();
			for (Entry<String, Kit> entry : playerKits.entrySet()) {
				kits.append(entry.getKey(), entry.getValue().getName());
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(kits.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFromFile() {
		new File("data").mkdir();
		StringBuilder builder = new StringBuilder();

		File f = new File("data" + File.separator + "persistentKits.json");
		if (!f.exists())
			return;
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				builder.append(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = builder.toString();
		BasicDBObject db = (BasicDBObject) JSON.parse(str);
		if (db != null) {
			for (Entry<String, Object> entry : db.entrySet()) {
				Kit k = getByName((String) entry.getValue());
				if (k != null) {
					playerKits.put(entry.getKey(), k);
				}
			}
		}
	}

	public Kit getKitOnPlayer(String str) {
		if (hasKitOn(str)) {
			return playerKits.get(str);
		}
		return KitFactory.createKit("null", "null", "null", new ItemStack[] {}, new ItemStack[] {}, new PotionEffect[] {}, Material.BEDROCK);
	}

	public boolean hasKitOn(String str) {
		return playerKits.containsKey(str);
	}

	public boolean canUseAbility(Player p, Kit k) {
		if (cooldowns.containsKey(k)) {
			HashMap<String, Long> cooldown = cooldowns.get(k);
			if (cooldown.containsKey(p.getName())) {
				if (cooldown.get(p.getName()) > System.currentTimeMillis()) {
					return false;
				}
			}
			return true;
		}
		return true;

	}

	public int getCooldownLeft(Player p, Kit k) {
		if (cooldowns.containsKey(k)) {
			HashMap<String, Long> cooldown = cooldowns.get(k);
			if (cooldown.containsKey(p.getName())) {
				return (int) (cooldown.get(p.getName()) - System.currentTimeMillis());
			}
			return 0;
		}
		return 0;

	}

	public void useAbility(Player p, Kit k, int millis) {
		if (cooldowns.containsKey(k)) {
			HashMap<String, Long> clds = cooldowns.get(k);
			clds.put(p.getName(), System.currentTimeMillis() + millis);
		} else {
			HashMap<String, Long> clds = new HashMap<String, Long>();
			clds.put(p.getName(), System.currentTimeMillis() + millis);
			cooldowns.put(k, clds);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void loadKits() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
		ArrayList<String> kitClasses = new ArrayList<String>();
		for (Class perkClass : KitPVP.get().getReflectionManager().getClassesInPackage(packageName)) {
			if (Kit.class.isAssignableFrom(perkClass)) {
				if (!Modifier.isAbstract(perkClass.getModifiers())) {
					if ((perkClass.getCanonicalName() != null))
						kitClasses.add(perkClass.getCanonicalName());
					Constructor ctor = perkClass.getConstructors()[0];
					ctor.setAccessible(true);
					Kit kit = (Kit) ctor.newInstance();
					registerKit(kit);
				}
			}
		}
		Core.get().box(packages.toArray(new String[] {}), "Loading kits from packages");

		for (String pack : packages) {
			for (Class perkClass : KitPVP.get().getReflectionManager().getClassesInPackage(pack)) {
				if (Kit.class.isAssignableFrom(perkClass)) {
					if (!Modifier.isAbstract(perkClass.getModifiers())) {
						if ((perkClass.getCanonicalName() != null))
							kitClasses.add(perkClass.getCanonicalName());
						Constructor ctor = perkClass.getConstructors()[0];
						ctor.setAccessible(true);
						Kit kit = (Kit) ctor.newInstance();
						registerKit(kit);
					}
				}

			}
		}
		Core.get().box(kitClasses.toArray(new String[] {}), "Kits loaded from classes");
		Collections.sort(KitPVP.getKits(), new Comparator<Kit>() {
			@Override
			public int compare(Kit o1, Kit o2) {
				return ((Integer) o1.getId()).compareTo(o2.getId());
			}
		});
	}

	/**
	 * Unregisters a kit
	 * 
	 * @param k
	 *            kit to unregister
	 */
	public void unregisterKit(final Kit k) {
		KitPVP.getKits().remove(k);
		KitAPI.getKitPVP().getCommandManager().unregisterCommand(k.getName().toLowerCase());
		if (k.getListener() != null) {
			HandlerList.unregisterAll(k.getListener());
		}
	}

	/**
	 * Used to register kits from an external plugin or an extension pack
	 * <p>
	 * Don't use this to register kits in the net.frozenorb.KitPVP.KitSystem.Kits package, that's plain retarded
	 * 
	 * @param k
	 *            the kit to register
	 */
	public void registerExternalKit(final Kit k) {
		registerKit(k);
		BaseCommand cmd = new BaseCommand() {

			@Override
			public void syncExecute() {
				k.commandRun((Player) sender);

			}
		};
		try {
			KitPVP.get().getCommandManager().registerCommand(k.getName().toLowerCase(), cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	/**
	 * Registers a kit
	 * 
	 * @param k
	 *            the kit to register
	 * @return whether registration was a duplicate one
	 */
	public boolean registerKit(Kit k) {
		boolean done = KitPVP.getKits().add(k);
		if (k.getListener() != null && done)
			Bukkit.getPluginManager().registerEvents(k.getListener(), KitPVP.get());
		return done;
	}
}
