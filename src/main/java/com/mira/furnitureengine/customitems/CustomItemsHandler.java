package com.mira.furnitureengine.customitems;

import com.mira.furnitureengine.FurnitureEngine;
import org.bukkit.Bukkit;
import uk.co.notnull.CustomItems.api.CustomItems;

public final class CustomItemsHandler {;
	private final FurnitureEngineItemProvider provider;
	private final CustomItems customItems = (CustomItems) Bukkit.getPluginManager().getPlugin("CustomItems");

	public CustomItemsHandler(FurnitureEngine plugin) {
		provider = new FurnitureEngineItemProvider(plugin);
		assert customItems != null;
		customItems.getItemManager().registerProvider(provider);
	}

	public void unregisterProvider() {
		assert customItems != null;
		customItems.getItemManager().unregisterProvider(provider);
	}
}
