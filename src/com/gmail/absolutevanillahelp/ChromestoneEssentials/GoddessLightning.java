package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import org.bukkit.entity.Player;


public class GoddessLightning implements Runnable {

	private ChromestoneEssentials plugin;
	private final String name;

	public GoddessLightning(ChromestoneEssentials instance, String name) {
		plugin = instance;
		this.name = name;
	}

	@Override
	public void run() {
		Player player = plugin.getServer().getPlayer(name);
		if (player != null) {
			plugin.getServer().getWorlds().get(0).strikeLightning(player.getLocation());
		}
	}
}
