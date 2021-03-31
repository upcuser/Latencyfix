package com.cryptymdr.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cryptymdr.Main;

public class MSCommand implements CommandExecutor {
	
	private Main main;
	
	public MSCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.isOp()) {
			player.sendMessage("§cYou don't have the permission to use this command!");
			return false;
		}
		if (args.length == 0) {
			player.sendMessage("§3Usage: §9/ms <ms>");
			player.sendMessage("§3Limit currently set: §9" + this.main.getMS() + " §3ms");
			return false;
		}
		this.main.setMS(Integer.valueOf(args[0]));
		player.sendMessage("§3You've set the limit at §9" + this.main.getMS() + " §3ms.");
		return true;
	}
}
