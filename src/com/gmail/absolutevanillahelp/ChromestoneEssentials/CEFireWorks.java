package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import java.util.TimerTask;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;

public class CEFireWorks extends TimerTask {

	private ChromestoneEssentials plugin;
	private Handler handler;
	
	public CEFireWorks(ChromestoneEssentials instance, String name) {
		plugin = instance;
		handler = new Handler(plugin, name);
	}
	
	@Override
	public void run() {
		plugin.getServer().getScheduler().runTask(plugin, handler);
	}
	
	class Handler implements Runnable {
		
		private ChromestoneEssentials plugin;
		private final String name;
		private Location prevLoc;
		
		public Handler(ChromestoneEssentials instance, String name) {
			plugin = instance;
			this.name = name;
		}
		
		@Override
		public void run() {
			Player player = plugin.getServer().getPlayer(name);
			if (player != null) {
				Location location = player.getLocation();
				if (location != prevLoc) {
					Firework fw = (Firework) plugin.getServer().getWorlds().get(0).spawnEntity(location, EntityType.FIREWORK);

					FireworkMeta fwm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder().withColor(Color.GREEN).with(Type.BALL_LARGE).build();
					fwm.addEffect(effect);
					fwm.setPower(0);

					fw.setFireworkMeta(fwm);
					prevLoc = location;
				}
			}
		}
	}
}
