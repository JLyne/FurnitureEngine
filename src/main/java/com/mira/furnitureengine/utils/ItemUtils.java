package com.mira.furnitureengine.utils;

import java.util.ArrayList;
import java.util.List;

import com.mira.furnitureengine.Furniture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mira.furnitureengine.FurnitureEngine;


public class ItemUtils {
	static final FurnitureEngine plugin = FurnitureEngine.getPlugin(FurnitureEngine.class);
	static final MiniMessage serializer = MiniMessage.miniMessage();

	public static void giveItem(Player player, String id, int amount, Location loc) {
		Furniture item = plugin.getFurnitureManager().getFurnitureById(id);

		if (item == null) {
			return;
		}

		giveItem(player, item, amount, loc);
	}

	public static void giveItem(Player player, Furniture furniture, int amount, Location loc) {
		if (loc == null) {
			// if no location is provided it will default to player location
			loc = player.getLocation();
		}

		ItemStack item = createFurnitureItem(furniture, amount);

		if (player == null) {
			loc.getWorld().dropItem(loc, item);
			return;
		}

		if (!player.getInventory().addItem(item).isEmpty()) {
			loc.getWorld().dropItem(loc, item);
		}
	}

	public static void dropItem(Furniture furniture, int amount, Location loc) {
		ItemStack item = createFurnitureItem(furniture, amount);

		loc.getWorld().dropItem(loc, item);
	}

	public static ItemStack createFurnitureItem(Furniture furniture, int amount) {
		// Creates item
		ItemStack item = new ItemStack(furniture.getMaterial(), amount);
		// Sets item meta (display, lore, model data)
		ItemMeta meta = item.getItemMeta();

		// Display Name
		meta.itemName(furniture.getDisplayName());

		// Custom Model Data
		meta.setCustomModelData(furniture.getCustomModelData());
		meta.getPersistentDataContainer().set(plugin.furnitureKey, plugin.furnitureTagType, furniture);

		// Lore (Optional check)
		if (!furniture.getLore().isEmpty()) {
			List<Component> loresList = new ArrayList<>();

			for (String text : furniture.getLore()) {
				loresList.add(serializer.deserialize(text));
			}

			meta.lore(loresList);
		}

		item.setAmount(Math.min(item.getMaxStackSize(), amount));
		item.setItemMeta(meta);

		return item;
	}
}
