package com.mira.furnitureengine.utils;

import java.util.ArrayList;
import java.util.List;

import com.mira.furnitureengine.Furniture;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mira.furnitureengine.FurnitureEngine;


@SuppressWarnings("UnstableApiUsage")
public class ItemUtils {
	static final FurnitureEngine plugin = FurnitureEngine.getPlugin(FurnitureEngine.class);
	static final MiniMessage serializer = MiniMessage.miniMessage();

	public static ItemStack giveItem(Player player, String id, int amount, Location loc) {
		Furniture item = plugin.getFurnitureManager().getFurnitureById(id);

		if (item == null) {
			return null;
		}

		return giveItem(player, item, amount, loc);
	}

	public static ItemStack giveItem(Player player, Furniture furniture, int amount, Location loc) {
		if (loc == null) {
			// if no location is provided it will default to player location
			loc = player.getLocation();
		}

		ItemStack item = createFurnitureItem(furniture, amount);

		if (player == null) {
			loc.getWorld().dropItem(loc, item);
			return item;
		}

		if (!player.getInventory().addItem(item).isEmpty()) {
			loc.getWorld().dropItem(loc, item);
		}

		return item;
	}

	public static void dropItem(Furniture furniture, int amount, Location loc) {
		ItemStack item = createFurnitureItem(furniture, amount);

		loc.getWorld().dropItem(loc, item);
	}

	public static ItemStack createFurnitureItem(Furniture furniture, int amount) {
		// Creates item
		ItemStack item = new ItemStack(Material.OAK_PLANKS, amount);

		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(plugin.furnitureKey, plugin.furnitureTagType, furniture);
		item.setItemMeta(meta);

		// Sets item data (name, rarity, lore, model data)
		item.setData(DataComponentTypes.RARITY, furniture.getRarity());
		item.setData(DataComponentTypes.ITEM_NAME, furniture.getItemName());

		if(furniture.getItemModel() != null) {
			item.setData(DataComponentTypes.ITEM_MODEL, furniture.getItemModel());
		}

		if(furniture.getCustomModelData() != null) {
			item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, furniture.getCustomModelData());
		}

		if (!furniture.getLore().isEmpty()) {
			List<Component> loresList = new ArrayList<>();

			for (String text : furniture.getLore()) {
				loresList.add(serializer.deserialize(text));
			}

			item.setData(DataComponentTypes.LORE, ItemLore.lore(loresList));
		}

		item.setAmount(Math.min(item.getMaxStackSize(), amount));

		return item;
	}
}
