package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.*;

public class PlayerRespawnEffects implements Runnable {

	String playerName;
	
	public PlayerRespawnEffects(Player player) {
		playerName = player.getName();
	}
	
	@Override
	public void run() {
		Player player = Bukkit.getPlayer(playerName);
		if (player != null) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000*20, 4));
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 50*20, 255));
			player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 45*20, 255));
		}
	}	
}
