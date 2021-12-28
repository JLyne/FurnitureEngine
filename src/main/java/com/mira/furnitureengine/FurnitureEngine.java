package com.mira.furnitureengine;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import com.mira.furnitureengine.commands.*;
import com.mira.furnitureengine.listeners.*;

@SuppressWarnings("unused")
public final class FurnitureEngine extends JavaPlugin {
	// WorldGuard Support
	public WorldGuardPlugin wg;

	// Update Checker
	public boolean versionChecked = false;
	public String versionOld = "";
	public String versionNew = "";
	
	public void onEnable() {	
		getLogger().info(ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.GRAY + "Furniture Engine enabled!");
		
		loadConfig();

		wg = getWorldGuard();

		// default
		getCommand("furnitureengine").setExecutor(new CoreCommand());
		getCommand("furnitureengine").setTabCompleter(new CommandTabCompleter());

		// Event Handlers
		new RightClick(this);
		new FurniturePlace(this);
		new FurnitureBreak(this);
	}
	
	public void onDisable() {
		 getLogger().info(ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.GRAY + "Furniture Engine disabled!");
	 }
	  
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

	    // WorldGuard may not be loaded
	    if (!(plugin instanceof WorldGuardPlugin)) {
	    	getLogger().info(ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.RED + "WorldGuard not found. Skipping!");
	        return null; // Maybe you want throw an exception instead
	    }

		return (WorldGuardPlugin) plugin;
	}
}
