package com.cryptymdr.listeners;

import java.util.Collections;

import org.bukkit.GameMode;
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

import net.minecraft.server.v1_7_R4.EnchantmentManager;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.GenericAttributes;

public class HitListener extends PacketAdapter {
	private Main main;

	public HitListener(Main main) {
		super(main, ListenerPriority.HIGHEST, Collections.singletonList(PacketType.Play.Client.USE_ENTITY));
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPacketReceiving(PacketEvent e) {

		PacketContainer packet = e.getPacket();
		Player attacker = e.getPlayer();
		Entity entity = packet.getEntityModifier(e).read(0);
		Damageable target = entity instanceof Damageable ? (Damageable) entity : null;

		if (e.getPacketType() == PacketType.Play.Client.USE_ENTITY
				&& packet.getEntityUseActions().read(0) == EntityUseAction.ATTACK && target != null && !target.isDead()
				&& (!(target instanceof Player) || ((Player) target).getGameMode() != GameMode.CREATIVE)) {

			e.setCancelled(true);

			try {
				EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(attacker, target,
						DamageCause.ENTITY_ATTACK, this.getDamager(attacker, target));
				main.getServer().getPluginManager().callEvent(entityDamageByEntityEvent);

				if (((CraftPlayer) attacker).getHandle().ping > this.main.getMS()) {
					this.registerAttack(entityDamageByEntityEvent);
					return;
				}

				new BukkitRunnable() {
					public void run() {
						registerAttack(entityDamageByEntityEvent);
					}
				}.runTaskLater(this.main, this.main.getMS() / 20);
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
	}
	
	private double getDamager(Player attacker, Damageable target) {
		EntityPlayer nmsp = ((CraftPlayer) attacker).getHandle();
		EntityPlayer nmse = ((CraftPlayer) target).getHandle();
		double damage = nmsp.getAttributeInstance(GenericAttributes.e).getValue();
		damage += EnchantmentManager.a((EntityLiving) nmsp, (EntityLiving) nmse);
		return damage;
	}
	
	private void registerAttack(EntityDamageByEntityEvent entityDamageByEntityEvent) {
		if (!entityDamageByEntityEvent.isCancelled()) {
			((Damageable) entityDamageByEntityEvent.getEntity()).damage(
					entityDamageByEntityEvent.getFinalDamage(), entityDamageByEntityEvent.getDamager());
		}
	}
}
