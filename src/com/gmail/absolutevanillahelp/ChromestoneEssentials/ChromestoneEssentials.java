package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import java.util.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;

public class ChromestoneEssentials extends JavaPlugin {

	private PigZombieHandler pzHandler;
	private Timer timer;
	private ArrayList<Material> ironList;
	private ArrayList<Material> goldList;
	private CEConfiguration dataConfig;
	private Location spawnLocation;
	private Location shopLocation;
	private Location marketLocation;
	private HashMap<String, Boolean> goddess;
	private HashMap<String, Boolean> god;
	private HashMap<String, Boolean> elder;
	private HashMap<String, Timer> activeFW;
	private ArrayList<String> shopTPDelay;
	private ArrayList<String> explodeDelay;
	private HashMap<String, Boolean> securedPlayers;
//	private HashMap<String, Boolean> pvpList;
	
	private ItemStack diamondSword;
	private CombatTagApi combatApi;

	public ChromestoneEssentials() {
		goddess = new HashMap<String, Boolean>();
		god = new HashMap<String, Boolean>();
		elder = new HashMap<String, Boolean>();
		activeFW = new HashMap<String, Timer>();
		shopTPDelay = new ArrayList<String>();
		timer = new Timer();
		explodeDelay = new ArrayList<String>();
		securedPlayers = new HashMap<String, Boolean>();
		pzHandler = new PigZombieHandler(this);
		diamondSword = new ItemStack(Material.DIAMOND_SWORD, 1, (short) 1062);
		diamondSword.addEnchantment(Enchantment.DURABILITY, 1);
//		pvpList = new HashMap<String, Boolean>();
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		if (getConfig().getBoolean("disabled")) {
			return;
		}
		if (getConfig().isSet("Goddess")) {
			for (String name : getConfig().getStringList("Goddess")) {
				goddess.put(name, false);
			}
		}
		if (getConfig().isSet("God")) {
			for (String name : getConfig().getStringList("God")) {
				god.put(name, false);
			}
		}
		if (getConfig().isSet("Elder")) {
			for (String name : getConfig().getStringList("Elder")) {
				elder.put(name, false);
			}
		}
		if (getConfig().isSet("Secure")) {
			Set<String> playerList =  getConfig().getConfigurationSection("Secure").getKeys(false);
			for (String name : playerList) {
				securedPlayers.put(name, false);
			}
		}
		dataConfig = new CEConfiguration(this, "data.yml");
		initSpawn();
		initShop();
		initMarket();
		initIronList();
		initGoldList();
		initPZHandler();
		timer.scheduleAtFixedRate(pzHandler, 60*1000, 5*60*1000);
		scheduleSaving();
		PluginManager plManager = getServer().getPluginManager();
		plManager.registerEvents(new PlayerEventListener(this), this);
		if(plManager.getPlugin("CombatTag") != null){
			combatApi = new CombatTagApi((CombatTag) plManager.getPlugin("CombatTag"));
		}
	}
	
	private void initSpawn() {
		if (dataConfig.getConfig().isSet("spawn.x") && dataConfig.getConfig().isSet("spawn.y") && dataConfig.getConfig().isSet("spawn.z")) {
			double x = dataConfig.getConfig().getDouble("spawn.x");
			double y = dataConfig.getConfig().getDouble("spawn.y");
			double z = dataConfig.getConfig().getDouble("spawn.z");
			World world = getServer().getWorlds().get(0);
			world.setSpawnLocation((int) x, (int) y, (int) z);
			spawnLocation = new Location(world, x, y, z);
		}
	}

	private void initShop() {
		if (dataConfig.getConfig().isSet("shop.x") && dataConfig.getConfig().isSet("shop.y") && dataConfig.getConfig().isSet("shop.z")) {
			shopLocation = new Location(getServer().getWorlds().get(0),
					dataConfig.getConfig().getDouble("shop.x"),
					dataConfig.getConfig().getDouble("shop.y"),
					dataConfig.getConfig().getDouble("shop.z"));
		}
	}

	private void initMarket() {
		if (dataConfig.getConfig().isSet("market.x") && dataConfig.getConfig().isSet("market.y") && dataConfig.getConfig().isSet("market.z")) {
			marketLocation = new Location(getServer().getWorlds().get(0),
					dataConfig.getConfig().getDouble("market.x"),
					dataConfig.getConfig().getDouble("market.y"),
					dataConfig.getConfig().getDouble("market.z"));
		}
	}

	private void initPZHandler() {
		String s = "PZSpawns.location.";
		if (dataConfig.getConfig().isSet(s + "x") && dataConfig.getConfig().isSet(s + "y") && dataConfig.getConfig().isSet(s + "z")) {
			pzHandler.setLocation(new Location(getServer().getWorlds().get(0),
					dataConfig.getConfig().getDouble(s + "x"),
					dataConfig.getConfig().getDouble(s + "y"),
					dataConfig.getConfig().getDouble(s + "z")));
		}
	}

	private void scheduleSaving() {
		timer.scheduleAtFixedRate(new PlayerOfflineSaver(this), 60*1000, 2*60*60*1000);
		timer.scheduleAtFixedRate(new PlayerOnlineSaver(this), 60*1000, 10*60*1000);
	}

	private void initIronList() {
		ironList = new ArrayList<Material>();
		ironList.add(Material.IRON_SWORD);
		ironList.add(Material.IRON_HELMET);
		ironList.add(Material.IRON_CHESTPLATE);
		ironList.add(Material.IRON_LEGGINGS);
		ironList.add(Material.IRON_BOOTS);
	}

	private void initGoldList() {
		goldList = new ArrayList<Material>();
		goldList.add(Material.GOLD_SWORD);
		goldList.add(Material.GOLD_HELMET);
		goldList.add(Material.GOLD_CHESTPLATE);
		goldList.add(Material.GOLD_LEGGINGS);
		goldList.add(Material.GOLD_BOOTS);
	}

	public HashMap<String, Boolean> getGod() {
		return god;
	}

	public HashMap<String, Boolean> getGoddess() {
		return goddess;
	}

	public HashMap<String, Boolean> getElder() {
		return elder;
	}

	public ArrayList<String> getShopTPDelay() {
		return shopTPDelay;
	}

	public ArrayList<String> getExplodeDelay() {
		return explodeDelay;
	}
	
	public HashMap<String, Boolean> getSecuredPlayers() {
		return securedPlayers;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("DestroyPigmanC")) {
			List<Entity> entityList = getServer().getWorlds().get(0).getEntities();

			for (Entity e : entityList) {
				if (e instanceof PigZombie) {
					if (((PigZombie) e).getCustomName() == null) {
						((PigZombie) e).remove();
					}
				}
			}
			return true;
		}
//		else if (cmd.getName().equals("ReloadCConfig")) {
//			reloadConfig();
//		}
		else if (cmd.getName().equals("BlowUpC") && args.length > 0) {
			String senderName = sender.getName();
			if (!explodeDelay.contains(senderName)) {
				Player player = getServer().getPlayer(args[0]);
				if (player != null) {
					String victimName = player.getName();
					if (sender instanceof Player) {
						Player attacker = (Player) sender;
						if (god.containsKey(victimName)) {
							attacker.sendRawMessage(ChatColor.RED + "Cannot blow up a god!");
							return true;
						}
						if (goddess.containsKey(victimName)) {
							attacker.sendRawMessage(ChatColor.RED + "Cannot blow up a goddess!");
							return true;
						}
						if (elder.containsKey(victimName)) {
							attacker.sendRawMessage(ChatColor.RED + "Cannot blow up an elder!");
							return true;
						}
						if (goddess.containsKey(senderName) || elder.containsKey(senderName)) {
							explodeDelay.add(senderName);
							getServer().getScheduler().runTaskLaterAsynchronously(this, new WarpDelay(this, senderName, 2), 30*60*20);
						}
						else if (!god.containsKey(senderName)) {
								return true;
						}
					}
					if (combatApi != null) {
						combatApi.tagPlayer(player);
					}
					getServer().broadcastMessage(ChatColor.RED + senderName + " has marked " + victimName + " to be blown up!");
					player.sendRawMessage(ChatColor.RED + "WARNING: You will be blown up t minus 5 seconds, prepare to die!");
					getServer().getScheduler().runTaskLater(this, new BlowUpPlayer(this, victimName), 5*20);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "You must wait at least 30 minutes since the last explosion!");
			}
			return true;
		}
		else if (sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equals("SpawnC")) {
				if (spawnLocation != null) {
					player.teleport(spawnLocation);
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Spawn is not specified!");
				}
				return true;
			}
			else if (cmd.getName().equals("SetSpawnC")) {
				spawnLocation = player.getLocation();
				getServer().getWorlds().get(0).setSpawnLocation(spawnLocation.getBlockX(),
						spawnLocation.getBlockY(),
						spawnLocation.getBlockZ());
				return true;
			}
			else if (cmd.getName().equals("ShopC")) {
				if (shopLocation != null) {
					if (!shopTPDelay.contains(player.getName())) {
						player.teleport(shopLocation);
						shopTPDelay.add(player.getName());
						getServer().getScheduler().runTaskLaterAsynchronously(this, new WarpDelay(this, player.getName(), 1), 5*60*20);
						player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
						player.removePotionEffect(PotionEffectType.WEAKNESS);
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 255));
						player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255));
					}
					else {
						player.sendRawMessage(ChatColor.RED + "You must wait at least 5 minutes since the last teleport!");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Shop is not specified!");
				}
				return true;
			}
			else if (cmd.getName().equals("SetShopC")) {
				shopLocation = player.getLocation();
				return true;
			}
			else if (cmd.getName().equals("RemoveC") && args.length > 0) {
				Material material = null;
				try {
					material = Material.valueOf(args[0]);
					World world = player.getWorld();
					Location location = player.getLocation();
					int radius = 10;
					if (args.length > 1) {
						try {
							radius = Integer.parseInt(args[1]);
						}
						catch (Exception e) {
							player.sendRawMessage(ChatColor.RED + args[1] + " is not a valid number!");
							radius = 10;
						}
					}
					for (int y = location.getBlockY()-radius; y <= location.getBlockY()+radius; y++) {
						for (int x = location.getBlockX()-radius; x <= location.getBlockX()+radius; x++) {
							for (int z = location.getBlockZ()-radius; z <= location.getBlockZ()+radius; z++) {
								if (world.getBlockAt(x, y, z).getType().equals(material)) {
									world.getBlockAt(x, y, z).setType(Material.AIR);
								}
							}
						}
					}
				}
				catch (IllegalArgumentException e) {
					player.sendRawMessage(ChatColor.RED + args[0] + " is not a valid block!");
				}
				return true;
			}
			else if (cmd.getName().equals("SetPZSpawnC")) {
				pzHandler.setLocation(player.getLocation());
				return true;
			}
			else if (cmd.getName().equals("RepairC") && args.length > 0) {
				if (args[0].equalsIgnoreCase("iron")) {
					Inventory inv = player.getInventory();
					if (inv.contains(Material.IRON_INGOT)) {
						ItemStack item = player.getItemInHand();
						if (item != null) {
							if (ironList.contains(item.getType())) {
								int index = inv.first(Material.IRON_INGOT);
								ItemStack iron = inv.getItem(index);
								int newAmount = iron.getAmount()-1;
								if (newAmount > 0) {
									iron.setAmount(newAmount);
								}
								else {
									iron = null;
								}
								inv.setItem(index, iron);
								int newDura = (short) (item.getDurability() - (item.getType().getMaxDurability() * .25));
								if (newDura > 0 && newDura < 32767) {
									item.setDurability((short) newDura);
								}
								else {
									item.setDurability((short) 0);
								}
							}
							else {
								player.sendRawMessage(ChatColor.RED + "Item must be iron!");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Must hold valid item!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Go to the shop and buy an iron ingot!");
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("gold")) {
					Inventory inv = player.getInventory();
					if (inv.contains(Material.GOLD_INGOT)) {
						ItemStack item = player.getItemInHand();
						if (item != null) {
							if (goldList.contains(item.getType())) {
								int index = inv.first(Material.GOLD_INGOT);
								ItemStack gold = inv.getItem(index);
								int newAmount = gold.getAmount()-1;
								if (newAmount > 0) {
									gold.setAmount(newAmount);
								}
								else {
									gold = null;
								}
								inv.setItem(index, gold);
								int newDura = (short) (item.getDurability() - (item.getType().getMaxDurability() * .25));
								if (newDura > 0 && newDura < 32767) {
									item.setDurability((short) newDura);
								}
								else {
									item.setDurability((short) 0);
								}
							}
							else {
								player.sendRawMessage(ChatColor.RED + "Item must be gold!");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Must hold valid item!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Go kill some pigman! If you lucky it'll drop an gold ingot.");
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("bow")) {
					Inventory inv = player.getInventory();
					if (inv.contains(Material.NETHER_STAR)) {
						ItemStack item = player.getItemInHand();
						if (item != null) {
							if (item.getType() == Material.BOW) {
								int index = inv.first(Material.NETHER_STAR);
								ItemStack netherStar = inv.getItem(index);
								int newAmount = netherStar.getAmount()-1;
								if (newAmount > 0) {
									netherStar.setAmount(newAmount);
								}
								else {
									netherStar = null;
								}
								inv.setItem(index, netherStar);
								int newDura = (short) (item.getDurability() - (Material.BOW.getMaxDurability() * .25));
								if (newDura > 0 && newDura < 32767) {
									item.setDurability((short) newDura);
								}
								else {
									item.setDurability((short) 0);
								}
							}
							else {
								player.sendRawMessage(ChatColor.RED + "Item must be a bow!");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Must hold a bow!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Go to the shop and buy a nether star!");
					}
					return true;
				}
				else if (args[0].equalsIgnoreCase("diamond")) {
					PlayerInventory inv = player.getInventory();
					ItemStack item = player.getItemInHand();
					if (item != null) {
						if (item.getType() == Material.DIAMOND_SWORD) {
							if (inv.contains(diamondSword)) {
								boolean found = false;
								Iterator<?> it = inv.all(diamondSword).entrySet().iterator();
								while (it.hasNext()) {
									Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) it.next();
									if (pairs.getKey() instanceof Integer) {
										Integer removeIndex = (Integer) pairs.getKey();
										if (!removeIndex.equals(inv.getHeldItemSlot())) {
											inv.setItem(removeIndex, null);
											found = true;
										}
									}
								}
								if (found) {
									int newDura = (short) (item.getDurability() - (500 + (Material.DIAMOND_SWORD.getMaxDurability()*.05)));
									if (newDura > 0 && newDura < 32767) {
										item.setDurability((short) newDura);
									}
									else {
										item.setDurability((short) 0);
									}
									return true;
								}
								else {
									player.sendRawMessage(ChatColor.RED + "Could not remove a diamond sword from inventory");
								}
							}
							else {
								player.sendRawMessage(ChatColor.RED + "Go to the shop and buy the diamond sword (from weapons villager)!");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Item must be a diamond sword!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Must hold a diamond sword!");
					}
				}
			}
			else if (cmd.getName().equals("FW")) {
				String name = player.getName();
				if (goddess.containsKey(name) || god.containsKey(name)) {
					if (activeFW.containsKey(name)) {
						Timer t = activeFW.get(name);
						t.cancel();
						t.purge();
						activeFW.remove(name);
					}
					else {
						Timer t2 = new Timer();
						t2.scheduleAtFixedRate(new CEFireWorks(this, name), 0, 2*1000);
						activeFW.put(name, t2);
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Must be a Goddess or God");
				}
				return true;
			}
			else if (cmd.getName().equals("ModestC")) {
				String name = player.getName();
				if (goddess.containsKey(name)) {
					if (goddess.get(name)) {
						player.setDisplayName(ChatColor.WHITE + name);
					}
					else {
						player.setDisplayName(ChatColor.GREEN + "[GODDESS]" + ChatColor.WHITE + name);
					}
					boolean prev = goddess.get(name);
					goddess.put(name, !prev);
				}
				else if (god.containsKey(name)) {
					if (god.get(name)) {
						player.setDisplayName(ChatColor.WHITE + name);
					}
					else {
						player.setDisplayName(ChatColor.GREEN + "[GOD]" + ChatColor.WHITE + name);
					}
					boolean prev = god.get(name);
					god.put(player.getName(), !prev);
				}
				else if (elder.containsKey(name)) {
					if (elder.get(name)) {
						player.setDisplayName(ChatColor.WHITE + name);
					}
					else {
						player.setDisplayName(ChatColor.GREEN + "[Elder]" + ChatColor.WHITE + name);
					}
					boolean prev = elder.get(name);
					elder.put(player.getName(), !prev);
				}
				return true;
			}
			else if (cmd.getName().equals("SuperEnchantC")) {
				ItemStack item = player.getItemInHand();
				item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1000);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1000);
				return true;
			}
			//			else if (cmd.getName().equals("PacifyPigman")) {
			//				List<Entity> entityList = player.getNearbyEntities(10, 10, 10);
			//				
			//				for (Entity e : entityList) {
			//					if (e instanceof PigZombie) {
			//						((PigZombie) e).setAngry(false);
			//					}
			//				}
			//			}
			else if (cmd.getName().equals("FillC") && args.length > 0) {
				Material material = null;
				try {
					material = Material.valueOf(args[0]);
					World world = player.getWorld();
					Location location = player.getLocation();
					int radius = 10;
					if (args.length > 1) {
						try {
							radius = Integer.parseInt(args[1]);
						}
						catch (Exception e) {
							player.sendRawMessage(ChatColor.RED + args[1] + " is not a valid number!");
							radius = 10;
						}
					}
					for (int y = location.getBlockY()-radius; y <= location.getBlockY()+radius; y++) {
						for (int x = location.getBlockX()-radius; x <= location.getBlockX()+radius; x++) {
							for (int z = location.getBlockZ()-radius; z <= location.getBlockZ()+radius; z++) {
								if (world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
									world.getBlockAt(x, y, z).setType(material);
								}
							}
						}
					}
				}
				catch (IllegalArgumentException e) {
					player.sendRawMessage(ChatColor.RED + args[0] + " is not a valid block!");
				}
				return true;
			}
			else if (cmd.getName().equals("SetMarketC")) {
				marketLocation = player.getLocation();
				return true;
			}
			else if (cmd.getName().equals("MarketC")) {
				if (marketLocation != null) {
					ItemStack item = player.getItemInHand();
					if (item != null) {
						if (item.getAmount() == 1) {
							ItemMeta itemMeta = item.getItemMeta();
							if (itemMeta != null) {
								if (itemMeta.hasDisplayName() && item.getType() == Material.PAPER) {
									if (itemMeta.getDisplayName().equals("PotionMarketTicket")) {
										player.setItemInHand(null);
										player.teleport(marketLocation);
										player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
										player.removePotionEffect(PotionEffectType.WEAKNESS);
										player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 255));
										player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255));
									}
									else {
										player.sendRawMessage(ChatColor.RED + "Invalid ticket");
									}
								}
								else {
									player.sendRawMessage(ChatColor.RED + "Invalid ticket!");
								}
							}
							else {
								player.sendRawMessage(ChatColor.RED + "Invalid ticket!");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Must hold one ticket!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Must hold valid item!");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Market is not specified!");
				}
				return true;
			}
			else if (cmd.getName().equals("UnEnchantC")) {
				Inventory inv = player.getInventory();
				if (inv.contains(Material.NETHER_STAR)) {
					ItemStack item = player.getItemInHand();
					if (item != null) {
						int index = inv.first(Material.NETHER_STAR);
						ItemStack netherStar = inv.getItem(index);
						if (netherStar.getAmount() >= 4) {
							int newAmount = netherStar.getAmount()-4;
							if (newAmount > 0) {
								netherStar.setAmount(newAmount);
							}
							else {
								netherStar = null;
							}
							inv.setItem(index, netherStar);
							for (Enchantment ench : item.getEnchantments().keySet()) {
								item.removeEnchantment(ench);
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Must have at lest 4 nether stars!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Must hold a valid item!");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Go to the shop and buy 4 nether stars!");
				}
				return true;
			}
			else if (cmd.getName().equals("SecureCheckC") && args.length > 0) {
				if (securedPlayers.containsKey(args[0])) {
					if (securedPlayers.get(args[0])) {
						player.sendRawMessage(ChatColor.GREEN + args[0] + "'s identity is valid.");
					}
					else {
						player.sendRawMessage(ChatColor.RED + args[0] + " hasn't confirmed their identity!");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + args[0] + " has not been listed to be secured!");
				}
				return true;
			}
			else if (cmd.getName().equals("UnlockC") && args.length > 0) {
				String playerName = player.getName();
				if (securedPlayers.containsKey(playerName)) {
					if (!securedPlayers.get(playerName)) {
						if (args[0].equals(getConfig().getString("Secure." + playerName))) {
							securedPlayers.put(playerName, true);
							player.sendRawMessage(ChatColor.GREEN + "Your account has been successfully unlocked, have a nice day.");
						}
						else {
							player.sendRawMessage(ChatColor.GREEN + "Password not recognized, try again.");
						}
					}
					else {
						player.sendRawMessage(ChatColor.GREEN + "Your account has already been unlocked until you log off, have a nice day.");
					}
				}
				else {
					player.sendRawMessage(ChatColor.GREEN + "Your account hasn't been listed in the Secured section, no worries! Have a nice day.");
				}
				return true;
			}
//			else if (cmd.getName().equals("PvPC") && args.length >= 2) {
//				if (args[0].equalsIgnoreCase("invite")) {
//					Player invitePlayer = getServer().getPlayer(args[1]);
//					if (invitePlayer != null) {
//						
//						invitePlayer.sendRawMessage(ChatColor.GREEN + player.getName() + " has invited you to a PvP match, accept?(expires in 5 minutes)");
//					}
//					else {
//						player.sendRawMessage(ChatColor.RED + "Could not find player: " + args[1]);
//					}
//				}
//				else if (args[0].equalsIgnoreCase("accept")) {
//					
//				}
//			}
		}
		else if (cmd.getName().equals("/say") && args.length > 0) {
			getServer().broadcastMessage(args[0]);
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		if (spawnLocation != null) {
			dataConfig.getConfig().set("spawn.x", spawnLocation.getX());
			dataConfig.getConfig().set("spawn.y", spawnLocation.getY());
			dataConfig.getConfig().set("spawn.z", spawnLocation.getZ());
		}
		if (shopLocation != null) {
			dataConfig.getConfig().set("shop.x", shopLocation.getX());
			dataConfig.getConfig().set("shop.y", shopLocation.getY());
			dataConfig.getConfig().set("shop.z", shopLocation.getZ());
		}
		if (marketLocation != null) {
			dataConfig.getConfig().set("market.x", marketLocation.getX());
			dataConfig.getConfig().set("market.y", marketLocation.getY());
			dataConfig.getConfig().set("market.z", marketLocation.getZ());
		}
		timer.cancel();
		String s = "PZSpawns.location.";
		Location location = pzHandler.getLocation();
		if (location!= null) {
			dataConfig.getConfig().set(s + "x", location.getX());
			dataConfig.getConfig().set(s + "y", location.getY());
			dataConfig.getConfig().set(s + "z", location.getZ());
		}
		timer.purge();
		Collection<Timer> tCollection = activeFW.values();
		for (Timer t : tCollection) {
			t.cancel();
			t.purge();
			activeFW.remove(t);
		}
		dataConfig.saveConfig();
	}
}