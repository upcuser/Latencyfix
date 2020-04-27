package com.cryptymdr;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.cryptymdr.listeners.HitListener;

public class Main extends JavaPlugin implements Listener {

	private HitListener hitListener = null;
	private AsyncListenerHandler asyncListenerHandler;
	private ProtocolManager protocolManager;
	
    public int ms = 80;

	@Override
	public void onEnable() {
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.reload();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		if (command.getName().equalsIgnoreCase("ms")) {
			if (!player.isOp()) {
				player.sendMessage("§cYou don't have the permission to use this command!");
				return false;
			}
			if (args.length == 0) {
				player.sendMessage("§3Usage: §9/ms <ms>");
				player.sendMessage("§3Limit currently set: §9" + this.ms + " §3ms");
				return false;
			}
			int ms 	= Integer.valueOf(args[0]);
			this.ms = ms;
			player.sendMessage("§3You've set the limit at §9" + this.ms + " §3ms.");
		} else if (command.getName().equalsIgnoreCase("ping")) {
			int ping = ((CraftPlayer) player).getHandle().ping;
			player.sendMessage("§3Your ping: §9" + ping);
		}
		
		return false; 
	}

	@Override
	public void onDisable() {
		if (this.hitListener != null)
			this.unregisterHitListener();

		this.protocolManager = null;
	}

	void registerHitListener() {
		if (this.hitListener == null) {
			new BukkitRunnable() {
				@Override
				public void run() {
					hitListener = new HitListener(Main.this);
					asyncListenerHandler = (protocolManager.getAsynchronousManager().registerAsyncHandler(hitListener));
					asyncListenerHandler.start();
				}
			}.runTaskAsynchronously(this);
		}
	}

	void unregisterHitListener() {
		if (this.hitListener != null) {
			this.protocolManager.getAsynchronousManager().unregisterAsyncHandler(this.asyncListenerHandler);
			this.hitListener = null;
		}
	}

	void reload() {
		if (this.hitListener != null)
			this.hitListener.stop();
		unregisterHitListener();
		registerHitListener();
	}
}
