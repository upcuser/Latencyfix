package com.cryptymdr.util;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R4.EnchantmentManager;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.GenericAttributes;

public class DamageResolver {

	public double getDamage(Player damager, Damageable entity) {
		EntityPlayer nmsp = ((CraftPlayer) damager).getHandle();
		EntityPlayer nmse = ((CraftPlayer) entity).getHandle();
		double damage = nmsp.getAttributeInstance(GenericAttributes.e).getValue();
		damage += EnchantmentManager.a((EntityLiving) nmsp, (EntityLiving) nmse);
		return damage;
	}
}