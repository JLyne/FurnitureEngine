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

public class RightClick implements Listener {
	private final FurnitureEngine plugin;
	private final FurnitureManager furnitureManager;

	public RightClick(FurnitureEngine plugin) {
		this.plugin = plugin;
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

		event.setCancelled(true);
		Location blockLocation = clicked.getLocation();

		furnitureManager.getPlacedFurniture(clicked)
				.forEach((itemFrame, furniture) -> furnitureManager.executeAction(itemFrame, player, blockLocation));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();

		if (player.isSneaking() || !(entity instanceof ItemFrame frame)) {
			return;
		}

		furnitureManager.executeAction(frame, player, frame.getLocation());
	}
}
