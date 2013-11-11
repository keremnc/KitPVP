package net.frozenorb.KitPVP.ListenerSystem;

import java.util.ArrayList;

import net.frozenorb.KitPVP.Reflection.ClassGetter;
import net.frozenorb.mShared.Shared;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerBase implements Listener {

	public void registerListeners(JavaPlugin owningPlugin, String packagename) throws InstantiationException, IllegalAccessException {
		ArrayList<String> pk = new ArrayList<String>();

		for (Class<?> c : ClassGetter.getClassesForPackage(owningPlugin, packagename)) {
			if (Listener.class.isAssignableFrom(c)) {
				pk.add(c.getCanonicalName());
				Listener l = (Listener) c.newInstance();
				Bukkit.getPluginManager().registerEvents(l, owningPlugin);
			}

		}
		Shared.get().getUtilities().box(pk.toArray(new String[] {}), "Loading listeners");

	}
}
