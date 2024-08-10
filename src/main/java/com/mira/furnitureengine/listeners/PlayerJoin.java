package com.mira.furnitureengine.listeners;

import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.RecipeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoin implements Listener {
	private final RecipeManager recipeManager;

	public PlayerJoin(FurnitureEngine plugin) {
		recipeManager = plugin.getRecipeManager();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(PlayerJoinEvent event) {
		recipeManager.discoverRecipes(event.getPlayer());
	}
}
