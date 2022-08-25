package com.mira.furnitureengine.creativeitemfilter;

import com.mira.furnitureengine.FurnitureEngine;
import org.bukkit.Bukkit;
import org.hurricanegames.creativeitemfilter.CreativeItemFilter;
import org.hurricanegames.creativeitemfilter.handler.meta.MetaCopierFactory;

public class CreativeItemFilterHandler {

	public CreativeItemFilterHandler(FurnitureEngine plugin) {
		boolean cifEnabled = Bukkit.getPluginManager().isPluginEnabled("CreativeItemFilter");

		if(!cifEnabled) {
			return;
		}

		MetaCopierFactory factory = ((CreativeItemFilter) Bukkit.getPluginManager().getPlugin("CreativeItemFilter"))
				.getMetaCopierFactory();

		factory.addCopier(new FurnitureMetaCopier(plugin));
	}
}
