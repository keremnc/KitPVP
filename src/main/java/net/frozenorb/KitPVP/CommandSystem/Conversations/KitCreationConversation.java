package net.frozenorb.KitPVP.CommandSystem.Conversations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.DataSystem.KitSerializer;
import net.frozenorb.KitPVP.KitSystem.KitFactory;
import net.frozenorb.KitPVP.KitSystem.SerializableKit;
import net.frozenorb.Utilities.Core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public abstract class KitCreationConversation {
	String name, permission, description;
	Material mat;
	SelectionStage stage = SelectionStage.NAME;

	private enum SelectionStage {
		NAME(0), DESCRIPTION(1), PERMISSION(2), MATERIAL(3);
		private int value;

		private SelectionStage(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static SelectionStage byId(int id) {
			for (SelectionStage s : values())
				if (s.getValue() == id)
					return s;
			return null;
		}
	}

	public KitCreationConversation(final Player sender) {
		sender.sendMessage(ChatColor.YELLOW + "Welcome to the Kit creator!");
		sender.sendMessage(ChatColor.YELLOW + "Type '§cquit§e' at any time to quit.");
		sender.sendMessage(ChatColor.YELLOW + "Please enter a name for the kit!");
		ConversationFactory factory = new ConversationFactory(KitAPI.getKitPVP()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
			@Override
			public Prompt acceptInput(ConversationContext cc, String s) {
				switch (stage) {
				case NAME:
					name = s.replace("&", "§").replace(" ", "");
					break;
				case DESCRIPTION:
					description = s.replace("&", "§").replace(" ", "");
					break;
				case PERMISSION:
					permission = s.replace("&", "§").replace(" ", "");
					break;
				case MATERIAL:
					if (Material.getMaterial(s.toUpperCase().replace(" ", "_")) != null) {
						mat = Material.getMaterial(s);
						break;
					}
					cc.getForWhom().sendRawMessage(ChatColor.RED + "Invalid material '" + s + "'!");
					return this;
				default:
					break;
				}
				stage = SelectionStage.byId(stage.getValue() + 1);
				if (stage == null) {
					SerializableKit k = KitFactory.createKit(name, permission, description, sender.getInventory().getArmorContents(), sender.getInventory().getContents(), sender.getActivePotionEffects().toArray(new PotionEffect[] {}), mat);
					onFinish(k);
					try {
						write(k);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return Prompt.END_OF_CONVERSATION;
				}
				cc.getForWhom().sendRawMessage("§ePlease enter a " + stage.toString().toLowerCase() + " for the kit!");
				return this;
			}

			@Override
			public String getPromptText(ConversationContext arg0) {
				return "§dKit Creator>";
			}

		}).withEscapeSequence("quit").withLocalEcho(false).thatExcludesNonPlayersWithMessage("Go away evil console!");
		Conversation con = factory.buildConversation((Player) sender);
		((Player) sender).beginConversation(con);
	}

	public void write(SerializableKit kit) throws IOException {
		new File("data" + File.separator + "kits").mkdir();
		File f = new File("data" + File.separator + "kits" + File.separator + kit.getName() + ".json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(Core.get().formatDBObject(new KitSerializer().serialize(kit)));
		writer.flush();
		writer.close();
	}

	public abstract void onFinish(SerializableKit k);
}
