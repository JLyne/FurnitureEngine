package com.mira.furnitureengine.handlers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class WorldGuardHandler {
	private final boolean wgEnabled;
	private RegionQuery query = null;

	public WorldGuardHandler() {
		wgEnabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");

		if(!wgEnabled) {
			return;
		}

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		query = container.createQuery();
	}

	public boolean checkPermission(Block block, Player player) {
		if (!wgEnabled) {
			return true;
		}

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		return query.testState(BukkitAdapter.adapt(block.getLocation()), localPlayer, Flags.BUILD)
				|| player.hasPermission("furnitureengine.admin");
	}
}
