package com.gmail.absolutevanillahelp.ChromestoneEssentials;

public class WarpDelay implements Runnable {

	private ChromestoneEssentials plugin;
	private final String name;
	private final int index;
	
	public WarpDelay(ChromestoneEssentials instance, String name, int index) {
		plugin = instance;
		this.name = name;
		this.index = index;
	}

	@Override
	public void run() {
		switch (index) {
		case 1:
			plugin.getShopTPDelay().remove(name);
		    break;
		case 2:
			plugin.getExplodeDelay().remove(name);
		    break;
		}
	}
	
}
