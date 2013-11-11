package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class SetSpawn extends BaseCommand {

	@Override
	public void execute() {
		if (sender.isOp()) {
			ConversationFactory factory = new ConversationFactory(KitAPI.getKitPVP()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

				public String getPromptText(ConversationContext context) {
					return "§aAre you sure you want to set spawn here? Type §b/yes§a to confirm or §c/no§a to quit.";
				}

				@Override
				public Prompt acceptInput(ConversationContext cc, String s) {
					if (s.equalsIgnoreCase("/yes")) {
						Location l = ((Player) cc.getForWhom()).getLocation();
						cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Spawn set!");
						((Player) cc.getForWhom()).getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
						return Prompt.END_OF_CONVERSATION;
					}
					if (s.equalsIgnoreCase("/no")) {
						cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Spawn setting cancelled.");
						return Prompt.END_OF_CONVERSATION;

					}
					cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
					return Prompt.END_OF_CONVERSATION;
				}

			}).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
			Conversation con = factory.buildConversation((Player) sender);
			((Player) sender).beginConversation(con);
		}
	}
}
