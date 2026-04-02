package com.mira.furnitureengine;

import com.mira.furnitureengine.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

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
			Map<Character, RecipeChoice> ingredients = new HashMap<>();

			try {
				if(ingredientSection != null) {
					ingredientSection.getKeys(false)
							.forEach(character -> {
								NamespacedKey itemTypeName = NamespacedKey.fromString(ingredientSection.getString(character, ""));

								if(itemTypeName == null) {
									throw new IllegalArgumentException("Invalid recipe ingredient for " + key + ": " + itemTypeName);
								}

								ItemType itemType = Registry.ITEM.get(itemTypeName);

								if(itemType == null) {
									throw new IllegalArgumentException("Invalid recipe ingredient for " + key + ": " + itemTypeName);
								}

								RecipeChoice choice;

								// Use Purpur's setPredicate when possible to exclude custom items from this and other plugins
								// in crafting recipes
								try {
									choice = new RecipeChoice.ExactChoice(itemType.createItemStack());
									Method setPredicate = choice.getClass().getMethod("setPredicate", Predicate.class);
									Predicate<ItemStack> predicate = (ItemStack i) ->
											itemType.equals(i.getType().asItemType()) && i.getPersistentDataContainer().isEmpty();
									setPredicate.invoke(choice, predicate);
								} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
									choice = RecipeChoice.itemType(itemType);
								}

								ingredients.put(character.charAt(0), choice);
							});
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
	}

	public void unregisterRecipes() {
		recipes.forEach((key, _) -> Bukkit.removeRecipe(key));
		recipes.clear();
	}
}
