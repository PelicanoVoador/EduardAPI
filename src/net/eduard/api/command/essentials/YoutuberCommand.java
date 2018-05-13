
package net.eduard.api.command.essentials;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.eduard.api.lib.storage.manager.CommandManager;

public class YoutuberCommand extends CommandManager {
	public List<String> messages = new ArrayList<>();
	public YoutuberCommand() {
		messages.add(" - Requisitos para ser Youtuber - ");

	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		for (String line : messages) {
			sender.sendMessage(line);
		}
		return true;
	}

}
