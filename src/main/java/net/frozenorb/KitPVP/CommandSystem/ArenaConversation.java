package net.frozenorb.KitPVP.CommandSystem;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena;
import net.frozenorb.Utilities.Core;
import net.frozenorb.Utilities.Serialization.Serializers.LocationSerializer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public abstract class ArenaConversation {
	private Location loc1 = null;
	private Location loc2 = null;

	public ArenaConversation(final Player sender, final int id) {

		loc1 = sender.getLocation();
		sender.sendMessage(ChatColor.YELLOW + "Welcome to the ArenaCreata 3000 by LazyLemons!");
		sender.sendMessage("§7=======================================");
		sender.sendMessage("§eType §a'2'§e to set the second location.");
		sender.sendMessage("To change the first location, type §a'1'§e.");
		sender.sendMessage("Type §a'quit'§e at any time to quit.");
		sender.sendMessage("Type §a'save§e to save the arena!");
		sender.sendMessage("Typing §a'info'§e will display the arena info!");
		ConversationFactory factory = new ConversationFactory(KitAPI.getKitPVP()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

			public String getPromptText(ConversationContext context) {
				return "";
			}

			@Override
			public Prompt acceptInput(ConversationContext cc, String s) {
				if (s.equalsIgnoreCase("1")) {
					loc1 = ((Player) cc.getForWhom()).getLocation();
					cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Loc1 Set");
					return this;
				}
				if (s.equalsIgnoreCase("2")) {
					loc2 = ((Player) cc.getForWhom()).getLocation();
					cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Loc2 Set!");
					return this;
				}
				if (s.equalsIgnoreCase("save")) {
					if (loc1 == null || loc2 == null) {
						cc.getForWhom().sendRawMessage(ChatColor.RED + "Verify that the locations are set!");
						return this;
					} else {
						if (id == -1) {
							cc.getForWhom().sendRawMessage(ChatColor.YELLOW + "Saved arena.");
							onCreate(loc1, loc2);
						} else {
							Arena a = KitAPI.getArenaManager().getById(id);
							if (a != null) {
								cc.getForWhom().sendRawMessage(ChatColor.YELLOW + "Overriding §a#" + id + "§e.");
								KitAPI.getArenaManager().overrideArena(KitAPI.getArenaManager().getById(id), loc1, loc2);
							} else
								cc.getForWhom().sendRawMessage(ChatColor.RED + "Could find no such arena.");
						}
						return Prompt.END_OF_CONVERSATION;
					}
				}
				if (s.equalsIgnoreCase("info")) {
					cc.getForWhom().sendRawMessage(ChatColor.YELLOW + "§ePrimary Loc: " + Core.get().formatDBObject(new LocationSerializer().serialize(loc1)) + "" + "     " + "§eSecondary Loc: " + Core.get().formatDBObject(new LocationSerializer().serialize(loc2)));
					return this;
				}
				cc.getForWhom().sendRawMessage("§cIncorrect usage!");
				return this;
			}

		}).withEscapeSequence("quit").withLocalEcho(false).thatExcludesNonPlayersWithMessage("Go away evil console!");
		Conversation con = factory.buildConversation((Player) sender);
		((Player) sender).beginConversation(con);
	}

	public ArenaConversation(final Player sender) {
		this(sender, -1);
	}

	public abstract void onCreate(Location l1, Location l2);
}
