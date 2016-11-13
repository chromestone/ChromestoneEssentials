package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.BlockIterator;

public final class LightningPlayer {

	public static void strikeLighting(Player player, int range) {
		Player target = getTarget(player, range);
		if (target != null) {
			player.getWorld().strikeLightning(target.getLocation());
		}
		else {
			directionallyStrikeLightning(player, range);
		}
	}
	
	private static Player getTarget(Player player, int range) {
		List<Entity> nearbyE = player.getNearbyEntities(range,
				range, range);
		ArrayList<Player> playerList = new ArrayList<Player>();

		for (Entity e : nearbyE) {
			if (e instanceof Player) {
				playerList.add((Player) e);
			}
		}

		BlockIterator bItr = new BlockIterator(player, range);
		Block block;
		Location loc;
		int bx, bz;
		double ex, ez;
		// loop through player's line of sight
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for (Player e : playerList) {
				loc = e.getLocation();
				ex = loc.getX();
				ez = loc.getZ();
				if ((bx-2 <= ex && ex <= bx+2) && (bz-2 <= ez && ez <= bz+2)) { //&& (by-1 <= ey && ey <= by+2.5)
					// entity is close enough, set target and stop
					return e;
				}
			}
		}
		return null;
	}
	
	private static void directionallyStrikeLightning(Player player, int distance) {
		player.getWorld().strikeLightning(
				player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(distance/5))
				.toLocation(player.getWorld(),
						player.getLocation().getYaw(),
						player.getLocation().getPitch()));
	}
	
}
