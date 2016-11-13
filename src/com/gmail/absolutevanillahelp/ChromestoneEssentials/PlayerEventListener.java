package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.*;
//import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffectType;

public class PlayerEventListener implements Listener {

	private HashMap<String, ArrayList<ItemStack>> emeraldTracker;
	private final ChromestoneEssentials plugin;
	private final ArrayList<EntityDamageEvent.DamageCause> dmgCauses;
	
	public PlayerEventListener(ChromestoneEssentials instance) {
		plugin = instance;
		emeraldTracker = new HashMap<String, ArrayList<ItemStack>>();
		dmgCauses = new ArrayList<EntityDamageEvent.DamageCause>();
		addDamageCauses();
	}

	private void addDamageCauses() {
		dmgCauses.add(EntityDamageEvent.DamageCause.FALL);
		dmgCauses.add(EntityDamageEvent.DamageCause.LIGHTNING);
		dmgCauses.add(EntityDamageEvent.DamageCause.PROJECTILE);
		dmgCauses.add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
		dmgCauses.add(EntityDamageEvent.DamageCause.THORNS);
		dmgCauses.add(EntityDamageEvent.DamageCause.SUFFOCATION);
		dmgCauses.add(EntityDamageEvent.DamageCause.MAGIC);
		dmgCauses.add(EntityDamageEvent.DamageCause.WITHER);
		dmgCauses.add(EntityDamageEvent.DamageCause.POISON);
		dmgCauses.add(EntityDamageEvent.DamageCause.DROWNING);
		dmgCauses.add(EntityDamageEvent.DamageCause.CONTACT);
		dmgCauses.add(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION);
		dmgCauses.add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName())) {
			if (!plugin.getSecuredPlayers().get(event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String baseCmdName = event.getMessage().split(" ")[0];
		if (!baseCmdName.equalsIgnoreCase("/UnlockC") && !baseCmdName.equalsIgnoreCase("/Shopc")) {
			if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName())) {
				if (!plugin.getSecuredPlayers().get(event.getPlayer().getName())) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootBow(EntityShootBowEvent event) {
		if (event.getEntity().hasPotionEffect(PotionEffectType.WEAKNESS)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity defender = (LivingEntity) event.getEntity();
			if (defender.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
				event.setCancelled(true);
			}
			else if (event.getDamager() instanceof LivingEntity) {
				LivingEntity attacker = (LivingEntity) event.getDamager();
				if (attacker.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) || attacker.hasPotionEffect(PotionEffectType.WEAKNESS)) {
					event.setCancelled(true);
				}
				else if (defender instanceof PigZombie) {
					if (attacker.getLocation().getY() != defender.getLocation().getY()) {
						event.setCancelled(true);
					}
				}
				else if (attacker instanceof Player) {
					Player attackPlayer = (Player) attacker;
					if (plugin.getSecuredPlayers().containsKey(attackPlayer.getName())) {
						if (!plugin.getSecuredPlayers().containsKey(attackPlayer.getName())) {
							event.setCancelled(true);
						}
					}
					else if (defender instanceof Player) {
						Player defendPlayer = (Player) defender;
						 if (plugin.getGoddess().containsKey(attackPlayer.getName())
								&& attackPlayer.getItemInHand().getType() == Material.GOLD_SWORD
								&& !plugin.getGod().containsKey(defendPlayer.getName()))
						{
							plugin.getServer().getScheduler().runTaskLater(plugin, new GoddessLightning(plugin, defendPlayer.getName()), 25);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof PigZombie) {
			if (dmgCauses.contains(event.getCause()) || event.getDamage() == 1) { //|| event.getDamage() < .5) {
				event.setCancelled(true);
			}
		}
		else if (event.getEntity() instanceof Player) {
			if (plugin.getSecuredPlayers().containsKey(((Player) event.getEntity()).getName())) {
				if (!plugin.getSecuredPlayers().get(((Player) event.getEntity()).getName())) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName())) {
			if (!plugin.getSecuredPlayers().get(event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
		ItemStack item = event.getItem();
		if (item != null) {
			Player player = event.getPlayer();
			if (item.getType() == Material.SADDLE) {
				itemToLightning(player, 20);
			}
			else if (item.getType() == Material.IRON_BARDING) {
				itemToLightning(player, 50);
			}
			else if (item.getType() == Material.GOLD_BARDING) {
				itemToLightning(player, 80);
			}
			else if (item.getType() == Material.DIAMOND_BARDING) {
				itemToLightning(player, 100);
			}
//			else if (item.getType() == Material.STICK && plugin.getElder().containsKey(player.getName())) {
//				Inventory inv = player.getInventory();
//				if (event.getAction() == Action.LEFT_CLICK_AIR && inv.contains(Material.NETHER_STAR)) {
//					int index = inv.first(Material.NETHER_STAR);
//					ItemStack iron = inv.getItem(index);
//					int newAmount = iron.getAmount()-1;
//					if (newAmount > 0) {
//						iron.setAmount(newAmount);
//					}
//					else {
//						iron = null;
//					}
//					inv.setItem(index, iron);
//					player.getName();
//				}
//			}
		}
	}
	
	public void itemToLightning(Player player, int range) {
		LightningPlayer.strikeLighting(player, range);
		player.setItemInHand(null);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (!b.getType().equals(Material.DIRT) && !b.getType().equals(Material.COBBLESTONE)
				&& !b.getType().equals(Material.TORCH) && !b.getType().equals(Material.LEVER))
		{
			if (!event.getPlayer().hasPermission("ChromestoneEssentials.BreakAll")) {
				event.setCancelled(true);
				if (b.getType() == Material.GLASS) {
					event.getPlayer().getWorld().strikeLightning(event.getPlayer().getLocation());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.getGod().containsKey(event.getPlayer().getName())) {
			Player player = event.getPlayer();
			Location location = player.getLocation();
			player.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 8F, false, false);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName())) {
			plugin.getSecuredPlayers().put(event.getPlayer().getName(), false);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity().getKiller();
		if (player != null && player != event.getEntity()) {
			player.getInventory().addItem(new ItemStack(Material.EMERALD));
		}
		
		ArrayList<ItemStack> emeralds = new ArrayList<ItemStack>();
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>(event.getDrops());
		for (ItemStack i : drops) {
			if (i.getType() == Material.EMERALD) {
				emeralds.add(i);
				event.getDrops().remove(i);
			}
		}
		if (emeralds.size() > 0) {
			emeraldTracker.put(event.getEntity().getName(), emeralds);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new PlayerRespawnEffects(event.getPlayer()), 20);
		String playerName = event.getPlayer().getName();
		if (emeraldTracker.containsKey(playerName)) {
			Inventory inventory = event.getPlayer().getInventory();
			ArrayList<ItemStack> emeralds = new ArrayList<ItemStack>(emeraldTracker.get(playerName));
			emeraldTracker.remove(playerName);
			for (ItemStack i : emeralds) {
				inventory.addItem(i);
			}
		}
	}
}