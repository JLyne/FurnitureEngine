package com.mira.furnitureengine.creativeitemfilter;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.FurnitureManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;
import org.hurricanegames.creativeitemfilter.CreativeItemFilterConfiguration;
import org.hurricanegames.creativeitemfilter.handler.component.ItemComponentPopulator;
import org.hurricanegames.creativeitemfilter.utils.ItemComponentUtils;
import org.jetbrains.annotations.NotNull;

public class FurnitureComponentPopulator implements ItemComponentPopulator {
	private final FurnitureManager furnitureManager;
	private final FurnitureEngine plugin;

	public FurnitureComponentPopulator(FurnitureEngine plugin) {
		this.plugin = plugin;
		this.furnitureManager = plugin.getFurnitureManager();
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void populateComponents(@NotNull ItemStack oldItem, @NotNull ItemStack newItem,
								   CreativeItemFilterConfiguration creativeItemFilterConfiguration) {
		Furniture furniture = furnitureManager.getFurnitureFromItem(oldItem);

		if(furniture == null) {
			return;
		}

		newItem.editPersistentDataContainer(
				pdc -> pdc.set(plugin.furnitureKey, plugin.furnitureTagType, furniture));

		if(furniture.getItemModel() != null) {
			ItemComponentUtils.copyComponent(oldItem, newItem, DataComponentTypes.ITEM_MODEL);
		}

		if(furniture.getCustomModelData() != null) {
			ItemComponentUtils.copyComponent(oldItem, newItem, DataComponentTypes.CUSTOM_MODEL_DATA);
		}

		ItemComponentUtils.copyComponent(oldItem, newItem, DataComponentTypes.RARITY);
		ItemComponentUtils.copyComponent(oldItem, newItem, DataComponentTypes.LORE);
		ItemComponentUtils.copyComponent(oldItem, newItem, DataComponentTypes.ITEM_NAME);
	}
}
