package com.mira.furnitureengine;

import com.mira.furnitureengine.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class RecipeManager {
	private final FurnitureEngine plugin;

	private final Map<NamespacedKey, ShapedRecipe> recipes = new HashMap<>();

	public RecipeManager(FurnitureEngine plugin) {
		this.plugin = plugin;
	}

	public void registerRecipes() {
		unregisterRecipes();
		ConfigurationSection furnitureConfig = plugin.getConfig().getConfigurationSection("Furniture");

		if (furnitureConfig == null) {
			return;
		}

		furnitureConfig.getKeys(false).forEach(key -> {
			Furniture furniture = plugin.getFurnitureManager().getFurnitureById(key);

			if(furniture == null) {
				return;
			}

			boolean crafting = furnitureConfig.getBoolean(key + ".crafting.enabled", false);

			if(!crafting) {
				return;
			}

			NamespacedKey recipeKey = new NamespacedKey(plugin, key);
			String[] shape = furnitureConfig.getStringList(key + ".crafting.recipe").toArray(new String[0]);
			String group = furnitureConfig.getString(key + ".crafting.group", "");
			ConfigurationSection ingredientSection = furnitureConfig.getConfigurationSection(key + ".crafting.ingredients");
			Map<Character, Material> ingredients = new HashMap<>();

			try {
				if(ingredientSection != null) {
					ingredientSection.getKeys(false)
							.forEach(character -> ingredients.put(character.charAt(0),
															 Material.valueOf(ingredientSection.getString(character, ""))));
				}

				ShapedRecipe recipe = new ShapedRecipe(recipeKey, ItemUtils.createFurnitureItem(furniture, 1));
				recipe.shape(shape);
				recipe.setGroup(group);
				ingredients.forEach(recipe::setIngredient);

				Bukkit.addRecipe(recipe);
				recipes.put(recipeKey, recipe);
			} catch(IllegalArgumentException e) {
				plugin.getLogger().warning("Failed to create recipe for item " + key + ": " + e.getMessage());
			}
		});

		for (Player players : Bukkit.getOnlinePlayers()) {
			discoverRecipes(players);
		}
	}

	public void unregisterRecipes() {
		for (Player players : Bukkit.getOnlinePlayers()) {
			undiscoverRecipes(players);
		}

		recipes.forEach((key, recipe) -> Bukkit.removeRecipe(key));
		recipes.clear();
	}

	public void discoverRecipes(Player player) {
		player.discoverRecipes(recipes.keySet());
	}

	public void undiscoverRecipes(Player player) {
		player.undiscoverRecipes(recipes.keySet());
	}
}
