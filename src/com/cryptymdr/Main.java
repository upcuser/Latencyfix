package com.cryptymdr;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.async.AsyncListenerHandler;
import com.cryptymdr.commands.MSCommand;
import com.cryptymdr.listeners.HitListener;

public class Main extends JavaPlugin implements Listener {

	private HitListener 			hitListener = null;
	private AsyncListenerHandler 	asyncListenerHandler;
	private ProtocolManager 		protocolManager;
	
	private int ms = 80;

	@Override
	public void onEnable() {
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		this.getCommand("ms").setExecutor(new MSCommand(this));
		this.reload();
	}

	@Override
	public void onDisable() {
		if (this.hitListener != null)
			this.unregisterHitListener();

		this.protocolManager = null;
	}
	
	public void setMS(int ms) { this.ms = ms; }
	public int 	getMS() 	  { return this.ms; }

	private void registerHitListener() {
		if (this.hitListener == null) {
			this.hitListener = new HitListener(Main.this);
			this.asyncListenerHandler = (protocolManager.getAsynchronousManager().registerAsyncHandler(hitListener));
			this.asyncListenerHandler.start();
		}
	}

	private void unregisterHitListener() {
		if (this.hitListener != null) {
			this.protocolManager.getAsynchronousManager().unregisterAsyncHandler(this.asyncListenerHandler);
			this.hitListener = null;
		}
	}

	private void reload() {
		this.unregisterHitListener();
		this.registerHitListener();
	}
}
