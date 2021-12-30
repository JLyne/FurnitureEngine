package com.mira.furnitureengine;

import com.mira.furnitureengine.api.events.FurnitureBreakEvent;
import com.mira.furnitureengine.api.events.FurnitureInteractEvent;
import com.mira.furnitureengine.api.events.FurniturePlaceEvent;
import com.mira.furnitureengine.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedReturnValue", "unused", "BooleanMethodIsAlwaysInverted"})
public class FurnitureManager {
	private final FurnitureEngine plugin;
	private final Map<String, Furniture> furniture = new HashMap<>();

	FurnitureManager(FurnitureEngine plugin) {
		this.plugin = plugin;
	}

	public void loadFurniture() {
		furniture.clear();

		ConfigurationSection furnitureConfig = plugin.getConfig().getConfigurationSection("Furniture");

		if (furnitureConfig == null) {
			return;
		}

		furnitureConfig.getKeys(false).forEach(key -> {
			String displayName = furnitureConfig.getString(key + ".display", "");
			int customModelData = furnitureConfig.getInt(key + ".custommodeldata", 0);

			int height = furnitureConfig.getInt(key + ".height", 0);
			int width = furnitureConfig.getInt(key + ".width", 0);
			int length = furnitureConfig.getInt(key + ".length", 0);

			boolean fullRotate = furnitureConfig.getBoolean(key + ".full-rotate", false);
			boolean cancelItemDrop = furnitureConfig.getBoolean(key + ".cancel-item-drop", false);

			Material material = Material.getMaterial(furnitureConfig.getString(key + ".item", "OAK_PLANKS"));
			List<String> conditions = furnitureConfig.getStringList(key + ".conditions");
			Map<String, List<String>> commands = new HashMap<>();

			commands.put("right-click", furnitureConfig.getStringList(key + ".commands.right-click"));
			commands.put("block-place", furnitureConfig.getStringList(key + ".commands.block-place"));
			commands.put("block-break", furnitureConfig.getStringList(key + ".commands.block-break"));

			Furniture item = new Furniture(key);
			item.setDisplayName(displayName);
			item.setCustomModelData(customModelData);
			item.setSize(height, width, length);
			item.setCancelDrop(cancelItemDrop);
			item.setFullRotate(fullRotate);
			item.setCommands(commands);
			item.setConditions(conditions);

			if (material != null) {
				item.setMaterial(material);
			}

			furniture.put(key, item);
		});
	}

	// Places furniture at location.
	public boolean placeFurniture(Furniture furniture, Block block, Rotation rotation, @Nullable Player actor) {
		Location blockLocation = block.getLocation();

		//Don't allow overlapping with non-prop furniture, or props of the same type
		if (getPlacedFurniture(block).values().stream()
				.anyMatch(existing -> !existing.isProp() || existing.getId().equals(furniture.getId()))) {
			return false;
		}

		FurniturePlaceEvent event = new FurniturePlaceEvent(actor, blockLocation);

		if (actor != null && !furniture.checkCondition("-OnBlockPlace", actor)) {
			event.setCancelled(true);
		}

		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return false;
		}

		ItemFrame frame;
		World world = blockLocation.getWorld();

		if (furniture.getWidth() == 1 && furniture.getHeight() == 1 && furniture.getLength() == 1) { // 1x1x1 Placing
			frame = world.spawn(blockLocation.add(0, 1, 0), ItemFrame.class); // Placing item frame on top of block
			block.setType(Material.BARRIER);
		} else if (furniture.getHeight() == 0) { // 0x0x0 Placing
			block.setType(Material.AIR);
			frame = world.spawn(blockLocation, ItemFrame.class);
		} else {
			return false;
		}

		ItemStack furnitureItem = new ItemStack(furniture.getMaterial(), 1);
		ItemMeta meta = furnitureItem.getItemMeta();
		meta.setCustomModelData(furniture.getCustomModelData());
		meta.getPersistentDataContainer().set(plugin.furnitureKey, plugin.furnitureTagType, furniture);
		furnitureItem.setItemMeta(meta);

		frame.setInvulnerable(true);
		frame.setFixed(true);
		frame.setItemDropChance(0.0f);
		frame.setVisible(false);
		frame.setItem(furnitureItem);
		frame.setFacingDirection(BlockFace.UP);
		frame.getPersistentDataContainer().set(plugin.furnitureKey, plugin.furnitureTagType, furniture);

		// Rotation of item-frame
		frame.setRotation(rotation);

		if (plugin.getServer().getPluginManager().getPlugin("LightAPI") != null) {
			LightAPI.get().setLightLevel(blockLocation.getWorld().getName(),
										 blockLocation.getBlockX(),
										 blockLocation.getBlockY(),
										 blockLocation.getBlockZ(),
										 furniture.getLightLevel());
		}

		furniture.executeCommand("block-place", actor, blockLocation);

		return true;
	}

	public boolean breakFurniture(Location blockLocation, @Nullable Player actor) {
		return breakFurniture(blockLocation.getBlock(), actor);
	}

	// Destroys furniture at location.
	public boolean breakFurniture(Block block, @Nullable Player actor) {
		Location blockLocation = block.getLocation();
		Map<ItemFrame, Furniture> existingFurniture = getPlacedFurniture(blockLocation);

		if (existingFurniture.isEmpty()) {
			return true;
		}

		existingFurniture.keySet().removeIf(frame -> breakFurniture(frame, actor));

		if (block.getType().equals(Material.BARRIER) && existingFurniture.values().stream().allMatch(
				Furniture::isProp)) {
			block.breakNaturally();
			blockLocation.getWorld().playSound(blockLocation, Sound.BLOCK_WOOD_BREAK, 3, 1);
			return true;
		}

		return false;
	}

	private boolean breakFurniture(ItemFrame frame, @Nullable Player actor) {
		Furniture furniture = getPlacedFurnitureType(frame);

		if (furniture == null) {
			return false;
		}

		//Get center of below block
		Location blockLocation = frame.getLocation().getBlock().getLocation();
		blockLocation.subtract(blockLocation.getX() < 0 ? 0.5 : -0.5, 1, blockLocation.getZ() < 0 ? 0.5 : -0.5);

		FurnitureBreakEvent event = new FurnitureBreakEvent(actor, blockLocation);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!furniture.checkCondition("-OnBlockBreak", actor)) {
			event.setCancelled(true);
		}

		if (event.isCancelled()) {
			return false;
		}

		frame.remove();

		if (furniture.getLightLevel() > 0
				&& plugin.getServer().getPluginManager().getPlugin("LightAPI") != null) {
			LightAPI.get().setLightLevel(blockLocation.getWorld().getName(),
										 blockLocation.getBlockX(),
										 blockLocation.getBlockY(),
										 blockLocation.getBlockZ(), 0);
		}

		if (event.isDroppingItems() && (actor == null || actor.getGameMode() != GameMode.CREATIVE)) {
			ItemUtils.dropItem(furniture, 1, blockLocation);
		}

		furniture.executeCommand("block-break", actor, blockLocation);

		return true;
	}

	public void executeAction(ItemFrame frame, Player actor, @Nullable Location location) {
		Furniture furniture = getPlacedFurnitureType(frame);

		if (furniture == null) {
			return;
		}

		FurnitureInteractEvent event = new FurnitureInteractEvent(actor, frame.getLocation().getBlock().getLocation());
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			// Commands Executer
			furniture.executeCommand("right-click", actor, location);
		}
	}

	public void updateCollision(Block block) {
		if(block.getType() != Material.BARRIER && !block.isReplaceable()) {
			return;
		}

		Map<ItemFrame, Furniture> existingFurniture = getPlacedFurniture(block);

		if(existingFurniture.values().stream().allMatch(Furniture::isProp)) {
			block.setType(Material.AIR);
		} else {
			block.setType(Material.BARRIER);
		}
	}

	// I wanted to keep stuff simple so instead of having to access "Util" these things can be found in the FurnitureAPI
	public void giveFurniture(String id, Player player, int amount) {
		ItemUtils.giveItem(player, id, amount, null);
	}

	public Map<ItemFrame, Furniture> getPlacedFurniture(Location blockLocation) {
		return getPlacedFurniture(blockLocation.getBlock());
	}

	public Map<ItemFrame, Furniture> getPlacedFurniture(Block block) {
		Location blockLocation = block.getLocation();
		List<Entity> nearbyEntities = (List<Entity>) blockLocation.getWorld()
				.getNearbyEntities(blockLocation.add(0, 1, 0), 0.13, 0.2, 0.13);

		Map<ItemFrame, Furniture> results = new HashMap<>();

		for (Entity nearbyEntity : nearbyEntities) {
			if (!(nearbyEntity instanceof ItemFrame frame)) {
				continue;
			}

			Location frameLocation = frame.getLocation().getBlock().getLocation();

			if (!frameLocation.equals(blockLocation)) {
				continue;
			}

			Furniture furniture = getPlacedFurnitureType(frame);

			if (furniture != null) {
				results.put(frame, furniture);
			}
		}

		return results;
	}

	public boolean isPlacedFurniture(ItemFrame frame) {
		return frame != null && frame.getPersistentDataContainer().has(plugin.furnitureKey, plugin.furnitureTagType);
	}

	public Furniture getPlacedFurnitureType(ItemFrame frame) {
		if (!isPlacedFurniture(frame)) {
			return null;
		}

		return frame.getPersistentDataContainer().get(plugin.furnitureKey, plugin.furnitureTagType);
	}

	public boolean isFurnitureItem(ItemStack item) {
		return item != null && item.getItemMeta().getPersistentDataContainer()
				.has(plugin.furnitureKey, plugin.furnitureTagType);
	}

	public Furniture getFurnitureFromItem(ItemStack item) {
		if (!isFurnitureItem(item)) {
			return null;
		}

		return item.getItemMeta().getPersistentDataContainer().get(plugin.furnitureKey, plugin.furnitureTagType);
	}

	public Map<String, Furniture> getAllFurniture() {
		return Collections.unmodifiableMap(furniture);
	}

	public Furniture getFurnitureById(String id) {
		return furniture.get(id);
	}
}
