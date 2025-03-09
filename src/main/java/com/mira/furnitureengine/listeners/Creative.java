package com.mira.furnitureengine.listeners;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.FurnitureManager;
import com.mira.furnitureengine.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;


public final class Creative implements Listener {
	private final FurnitureManager furnitureManager;

	public Creative(FurnitureEngine plugin) {
		furnitureManager = plugin.getFurnitureManager();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onCreative(InventoryCreativeEvent event) {
		ItemStack item = event.getCursor();
		Furniture furniture = furnitureManager.getFurnitureFromItem(item);

		if (furniture == null) {
			return;
		}

		event.setCursor(ItemUtils.createFurnitureItem(furniture, item.getAmount()));
		((Player) event.getWhoClicked()).updateInventory();
	}
}