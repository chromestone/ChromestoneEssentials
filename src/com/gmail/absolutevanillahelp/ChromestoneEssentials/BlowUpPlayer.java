package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BlowUpPlayer implements Runnable {

	ChromestoneEssentials plugin;
	private final String name;
	
	public BlowUpPlayer(ChromestoneEssentials instance, String name) {
		plugin = instance;
		this.name = name;
	}
	
	@Override
	public void run() {
		Player player = plugin.getServer().getPlayer(name);
		if (player != null) {
			player.setHealth(1D);
			Location location = player.getLocation();
			player.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 8F, false, false);
			if (!player.isDead()) {
				player.setHealth(0D);
			}
		}
	}
}
