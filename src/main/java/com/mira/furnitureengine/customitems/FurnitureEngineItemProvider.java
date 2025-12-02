package com.mira.furnitureengine.customitems;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.utils.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.items.provider.CustomItemProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FurnitureEngineItemProvider implements CustomItemProvider {
	private final FurnitureEngine plugin;
	private final Map<String, CustomItem> items = new HashMap<>();

	public FurnitureEngineItemProvider(FurnitureEngine plugin) {
		this.plugin = plugin;

		plugin.getFurnitureManager().getAllFurniture().forEach((id, furniture) -> {
			CustomItem customItem = CustomItem.builder()
					.id(new NamespacedKey(plugin, id))
					.displayName(furniture.getItemName())
					.generator((player, quantity) -> ItemUtils.createFurnitureItem(furniture, quantity))
					.build();

			items.put(id, customItem);
		});
	}

	@Override
	public @NotNull JavaPlugin getPlugin() {
		return plugin;
	}

	public @NotNull List<CustomItem> provideItems() {
		return items.values().stream().toList();
	}

	public CustomItem identifyItem(ItemStack itemStack) {
		Furniture furniture = plugin.getFurnitureManager().getFurnitureFromItem(itemStack);

		if(furniture != null) {
			return items.get(furniture.getId());
		}

		return null;
	}
}
