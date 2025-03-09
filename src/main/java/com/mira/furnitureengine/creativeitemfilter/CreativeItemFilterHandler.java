package com.mira.furnitureengine.creativeitemfilter;

import com.mira.furnitureengine.FurnitureEngine;
import org.bukkit.Bukkit;
import org.hurricanegames.creativeitemfilter.CreativeItemFilter;
import org.hurricanegames.creativeitemfilter.handler.component.ItemComponentPopulatorFactory;

public class CreativeItemFilterHandler {

	public CreativeItemFilterHandler(FurnitureEngine plugin) {
		boolean cifEnabled = Bukkit.getPluginManager().isPluginEnabled("CreativeItemFilter");

		if(!cifEnabled) {
			return;
		}

		ItemComponentPopulatorFactory factory = ((CreativeItemFilter) Bukkit.getPluginManager().getPlugin("CreativeItemFilter"))
				.getComponentPopulatorFactory();

		factory.addPopulator(new FurnitureComponentPopulator(plugin));
	}
}
