package com.mira.furnitureengine.listeners;

import com.mira.furnitureengine.FurnitureManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.mira.furnitureengine.FurnitureEngine;

public final class RightClick implements Listener {
	private final FurnitureManager furnitureManager;

	public RightClick(FurnitureEngine plugin) {
		furnitureManager = plugin.getFurnitureManager();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// Manages Block Interaction
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND || player.isSneaking()) {
			return;
		}

		Block clicked = event.getClickedBlock();

		if (clicked == null || clicked.getType() != Material.BARRIER) {
			return;
		}

		Location blockLocation = clicked.getLocation();

		if(furnitureManager.getPlacedFurniture(clicked).keySet().stream()
				.anyMatch(itemFrame -> furnitureManager.executeAction(itemFrame, player, blockLocation))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();

		if (player.isSneaking() || !(entity instanceof ItemFrame frame)) {
			return;
		}

		if(furnitureManager.executeAction(frame, player, frame.getLocation())) {
			event.setCancelled(true);
		}
	}
}
