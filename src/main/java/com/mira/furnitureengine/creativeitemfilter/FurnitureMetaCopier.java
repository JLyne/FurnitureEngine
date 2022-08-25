package com.mira.furnitureengine.creativeitemfilter;

import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.FurnitureManager;
import org.bukkit.inventory.meta.ItemMeta;
import org.hurricanegames.creativeitemfilter.CreativeItemFilterConfiguration;
import org.hurricanegames.creativeitemfilter.handler.meta.MetaCopier;

public class FurnitureMetaCopier implements MetaCopier<ItemMeta> {
	private final FurnitureManager furnitureManager;
	private final FurnitureEngine plugin;

	public FurnitureMetaCopier(FurnitureEngine plugin) {
		this.plugin = plugin;
		this.furnitureManager = plugin.getFurnitureManager();
	}
	
	@Override
	public void copyValidMeta(CreativeItemFilterConfiguration creativeItemFilterConfiguration, ItemMeta oldMeta, ItemMeta newMeta) {
		if(furnitureManager.isFurnitureItem(oldMeta)) {
			newMeta.getPersistentDataContainer()
					.set(plugin.furnitureKey, plugin.furnitureTagType,
						 oldMeta.getPersistentDataContainer().get(plugin.furnitureKey, plugin.furnitureTagType));
		}
	}

	@Override
	public Class<ItemMeta> getMetaClass() {
		return ItemMeta.class;
	}
}
