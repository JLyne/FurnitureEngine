package com.mira.furnitureengine.customitems;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
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
					.displayName(Component.text(furniture.getDisplayName()))
					.generator((player, quantity) -> ItemUtils.createFurnitureItem(furniture, quantity))
					.build();

			items.put(id, customItem);
		});
	}

	public List<CustomItem> provideItems() {
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
