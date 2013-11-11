package net.frozenorb.KitPVP.MatchSystem.Queue;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class exists so we can remove duplicates on insert
 * 
 * @since 11/7/2013
 * @author Kerem
 * 
 */
public class MatchList extends ArrayList<MatchQueue> {
	private static final long serialVersionUID = 4373374789269488697L;

	@Override
	public boolean add(MatchQueue e) {
		Iterator<MatchQueue> iter = iterator();
		while (iter.hasNext()) {
			if (iter.next().getPlayer().getName().equalsIgnoreCase(e.getPlayer().getName()))
				iter.remove();
		}
		return super.add(e);
	}

	/**
	 * Removes a player from the list
	 * 
	 * @param name
	 *            playerName
	 */
	public void remove(String name) {
		Iterator<MatchQueue> iter = iterator();
		while (iter.hasNext()) {
			if (iter.next().getPlayer().getName().equalsIgnoreCase(name))
				iter.remove();
		}

	}
}
