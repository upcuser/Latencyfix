package com.cryptymdr.listeners;

import java.util.Collections;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.cryptymdr.Main;
import com.cryptymdr.util.DamageResolver;

public class HitListener extends PacketAdapter {
	private Main main;
	private DamageResolver damageResolver;

	public HitListener(Main main) {
		super(main, ListenerPriority.HIGHEST, Collections.singletonList(PacketType.Play.Client.USE_ENTITY));
		this.main = main;
		this.damageResolver = new DamageResolver();
		if (damageResolver == null)
			throw new NullPointerException("Damage resolver is null, unsupported Spigot version?");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPacketReceiving(PacketEvent e) {

		PacketContainer packet = e.getPacket();
		Player attacker = e.getPlayer();
		Entity entity = packet.getEntityModifier(e).read(0);
		Damageable target = entity instanceof Damageable ? (Damageable) entity : null;
		World world = attacker.getWorld();

		if (e.getPacketType() == PacketType.Play.Client.USE_ENTITY
				&& packet.getEntityUseActions().read(0) == EntityUseAction.ATTACK && target != null && !target.isDead()
				&& world == target.getWorld() && world.getPVP()
				&& (!(target instanceof Player) || ((Player) target).getGameMode() != GameMode.CREATIVE)) {

			e.setCancelled(true);

			try {
				double damage = this.damageResolver.getDamage(attacker, target);
				int attackerPing = ((CraftPlayer) attacker).getHandle().ping;

				EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(attacker, target,
						DamageCause.ENTITY_ATTACK, damage);
				main.getServer().getPluginManager().callEvent(entityDamageByEntityEvent);

				if (attackerPing > this.main.ms) {
					if (!entityDamageByEntityEvent.isCancelled()) {
						((Damageable) entityDamageByEntityEvent.getEntity()).damage(
								entityDamageByEntityEvent.getFinalDamage(), entityDamageByEntityEvent.getDamager());
					}
					return;
				}

				new BukkitRunnable() {
					public void run() {
						if (!entityDamageByEntityEvent.isCancelled()) {
							((Damageable) entityDamageByEntityEvent.getEntity()).damage(
									entityDamageByEntityEvent.getFinalDamage(), entityDamageByEntityEvent.getDamager());
						}
					}
				}.runTaskLater(this.main, this.main.ms / 20);
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
	}

	public void stop() {
		this.damageResolver = null;
	}
}
