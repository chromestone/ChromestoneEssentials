package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class PigZombieHandler extends TimerTask {

	private ChromestoneEssentials plugin;
	private Handler handler;
	private Random random;

	public PigZombieHandler(ChromestoneEssentials instance) {
		plugin = instance;
		handler = new Handler(plugin);
		random = new Random();
	}
	
	@Override
	public void run() {
		plugin.getServer().getScheduler().runTask(plugin, handler);
	}
	
	public void setLocation(Location location) {
		handler.setLocation(location);
	}

	public Location getLocation() {
		return handler.getLocation();
	}

	class Handler implements Runnable {

		private ChromestoneEssentials plugin;
		private Location spawnLocation;
		private final int maxPigZombies;

		public Handler(ChromestoneEssentials instance) {
			plugin = instance;
			spawnLocation = null;
			maxPigZombies = 200;
		}

		@Override
		public void run() {
			World world = plugin.getServer().getWorlds().get(0);

			List<LivingEntity> entityList = world.getLivingEntities();

			ArrayList<PigZombie> pigZombieList = new ArrayList<PigZombie>();
			int pigZombies = 0;

			for(LivingEntity e : entityList){
				if(e instanceof PigZombie){
					if (e.getCustomName() == null) {
						if(e.getTicksLived() >= 24000){
							e.remove();
						}
						else {
							((PigZombie) e).setAngry(false);
							pigZombieList.add((PigZombie) e);
							e.getUniqueId();
							pigZombies++;
						}
					}
				}
			}

			for (int i = pigZombies - maxPigZombies; i > 0; i--) {
				PigZombie pigZombie = pigZombieList.iterator().next();
				if (pigZombie.getCustomName() == null) {
					pigZombie.remove();
				}
			}

			if (plugin.getServer().getOnlinePlayers().length > 0) {
					if (spawnLocation != null) {
						for (int i = 0; i < 5; i++) {
							double x = spawnLocation.getX() + random.nextInt(99);
							double z = spawnLocation.getZ() + random.nextInt(99);
							for (int p = 0; p < 5; p++) {
								PigZombie pigZombie = world.spawn(new Location(plugin.getServer().getWorlds().get(0),
										x, spawnLocation.getY(), z),
										PigZombie.class);
								pigZombie.setRemoveWhenFarAway(false);
								EntityEquipment equipment = pigZombie.getEquipment();
								equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
								equipment.setLeggingsDropChance(0);
								equipment.setItemInHand(new ItemStack(Material.GOLD_SWORD));
								equipment.setItemInHandDropChance(0);
							}
						}
						for (int i = 0; i < 5; i++) {
							double x = spawnLocation.getX() - random.nextInt(99);
							double z = spawnLocation.getZ() - random.nextInt(99);
							for (int p = 0; p < 5; p++) {
								PigZombie pigZombie = world.spawn(new Location(plugin.getServer().getWorlds().get(0),
										x, spawnLocation.getY(), z),
										PigZombie.class);
								pigZombie.setRemoveWhenFarAway(false);
								EntityEquipment equipment = pigZombie.getEquipment();
								equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
								equipment.setLeggingsDropChance(0);
								equipment.setItemInHand(new ItemStack(Material.GOLD_SWORD));
								equipment.setItemInHandDropChance(0);
							}
						}
					}
			}
		}

		public void setLocation(Location location) {
			spawnLocation = location;
		}

		public Location getLocation() {
			return spawnLocation;
		}
	}
}
