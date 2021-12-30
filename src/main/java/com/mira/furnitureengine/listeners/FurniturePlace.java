package com.mira.furnitureengine.listeners;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureManager;
import com.mira.furnitureengine.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Rotation;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.mira.furnitureengine.FurnitureEngine;

import org.bukkit.inventory.ItemStack;


public class FurniturePlace implements Listener {
	private final FurnitureEngine plugin;
	private final FurnitureManager furnitureManager;

	public FurniturePlace(FurnitureEngine plugin) {
		this.plugin = plugin;
		furnitureManager = plugin.getFurnitureManager();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block blockPlaced = event.getBlockPlaced();
		ItemStack item = player.getInventory().getItemInMainHand();

		if (item.getType() == Material.AIR) {
			item = player.getInventory().getItemInOffHand();
		}

		if (!furnitureManager.isFurnitureItem(item)) {
			return;
		}

		if(!Utils.checkPlacePermission(blockPlaced, player)) {
			event.setCancelled(true);
			return;
		}

		Furniture furniture = furnitureManager.getFurnitureFromItem(item);
		Rotation rotation = Utils.getRotationFromYaw(player.getLocation().getYaw(), furniture.isFullRotate());

		if(!furnitureManager.placeFurniture(furniture, blockPlaced, rotation, player)) {
			event.setCancelled(true);
		}
	}
}