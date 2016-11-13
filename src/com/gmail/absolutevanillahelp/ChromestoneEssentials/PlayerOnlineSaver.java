package com.gmail.absolutevanillahelp.ChromestoneEssentials;

import java.util.TimerTask;

public class PlayerOnlineSaver extends TimerTask {

	ChromestoneEssentials plugin;
	Handler handler;
	
	public PlayerOnlineSaver(ChromestoneEssentials instance) {
		plugin = instance;
		handler = new Handler(plugin);
	}
	
	@Override
	public void run() {
		if (plugin.getServer().getOnlinePlayers().length > 0) {
			plugin.getServer().broadcastMessage("Saving World...");
			plugin.getServer().getScheduler().runTask(plugin, handler);
		}
	}

	class Handler implements Runnable {
		
		ChromestoneEssentials plugin;
		
		public Handler(ChromestoneEssentials instance) {
			plugin = instance;
		}

		@Override
		public void run() {
			plugin.getServer().getWorlds().get(0).save();
		}
	}

}