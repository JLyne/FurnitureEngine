package com.mira.furnitureengine.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureManager;
import com.mira.furnitureengine.utils.ItemUtils;
import com.mira.furnitureengine.utils.Utils;
import com.sk89q.worldedit.history.change.EntityRemove;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mira.furnitureengine.FurnitureEngine;


public class FurnitureBreak implements Listener {
	private final FurnitureEngine plugin;
	private final FurnitureManager furnitureManager;

	public FurnitureBreak(FurnitureEngine plugin) {
		this.plugin = plugin;
		furnitureManager = plugin.getFurnitureManager();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockInteract(final PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getClickedBlock() == null) {
			plugin.getLogger().info("Wrong action " + event.getAction() + " or missing block " + event.getClickedBlock());
			return;
		}

		Block clicked = event.getClickedBlock();

		if (!clicked.getType().equals(Material.BARRIER)) {
			plugin.getLogger().info("Not a barrier " + clicked.getType());
			return;
		}

		Player player = event.getPlayer();

		if(!Utils.checkBreakPermissions(clicked, player)) {
			plugin.getLogger().info("No permission");
			event.setCancelled(true);
			return;
		}

		if(!furnitureManager.breakFurniture(clicked, player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityHit(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame frame)) {
			return;
		}

		Furniture furniture = furnitureManager.getPlacedFurnitureType(frame);

		if (furniture == null || !furniture.isProp()) {
			return;
		}

		event.setCancelled(true);
		Player player = event.getPlayer();

		if (!player.isSneaking()) {
			return;
		}

		Block block = event.getRightClicked().getLocation().getBlock().getRelative(BlockFace.DOWN);

		if(!Utils.checkBreakPermissions(block, player)) {
			plugin.getLogger().info("No permission");
			event.setCancelled(true);
			return;
		}

		furnitureManager.breakFurniture(block.getLocation().subtract(0, 1, 0), player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {
		if (!(event.getEntity() instanceof ItemFrame frame) || !furnitureManager.isPlacedFurniture(frame)) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityTeleport(EntityTeleportEvent event) {
		if (!(event.getEntity() instanceof ItemFrame frame) || !furnitureManager.isPlacedFurniture(frame)) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityRemove(EntityRemoveFromWorldEvent event) {
		if (!(event.getEntity() instanceof ItemFrame frame) || !furnitureManager.isPlacedFurniture(frame)) {
			return;
		}

		Block block = event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN);
		furnitureManager.updateCollision(block);
	}
}