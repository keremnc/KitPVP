package net.frozenorb.KitPVP.CommandSystem;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public abstract class BaseCommand implements CommandExecutor, TabExecutor {
	protected CommandSender sender;
	protected Command cmd;
	protected String label;
	protected String[] args;

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		this.sender = arg0;
		this.cmd = arg1;
		this.args = arg3;
		this.label = arg2;
		execute();
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		this.sender = arg0;
		this.cmd = arg1;
		this.args = arg3;
		this.label = arg2;
		return tabComplete();
	}

	/**
	 * Called when the command is executed
	 */
	public abstract void execute();

	public List<String> tabComplete() {
		return null;
	}

}
