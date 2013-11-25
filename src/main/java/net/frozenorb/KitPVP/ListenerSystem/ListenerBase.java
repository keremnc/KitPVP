package net.frozenorb.KitPVP.ListenerSystem;

import java.util.ArrayList;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerBase implements Listener {

	public void registerListeners(JavaPlugin owningPlugin, String packagename) throws InstantiationException, IllegalAccessException {
		ArrayList<String> pk = new ArrayList<String>();

		for (Class<?> c : KitPVP.get().getReflectionManager().getClassesInPackage(packagename)) {
			if (Listener.class.isAssignableFrom(c)) {
				pk.add(c.getCanonicalName());
				Listener l = (Listener) c.newInstance();
				Bukkit.getPluginManager().registerEvents(l, owningPlugin);
			}

		}
		Core.get().box(pk.toArray(new String[] {}), "Loading listeners");

	}
}
