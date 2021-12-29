package com.mira.furnitureengine.handlers;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GriefPreventionHandler {
	private final boolean gpEnabled;
	private GriefPrevention griefPrevention = null;

	public GriefPreventionHandler() {
		gpEnabled = Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");

		if(!gpEnabled) {
			return;
		}

		griefPrevention = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
	}

	public boolean checkPermission(Block block, Player player) {
		if(!gpEnabled) {
			return true;
		}

		Claim claim = griefPrevention.dataStore.getClaimAt(block.getLocation(), false, null);

		if(claim != null && claim.allowBuild(player, block.getType()) != null) {
			player.sendMessage(Component.text(claim.allowBuild(player, block.getType())).color(NamedTextColor.RED));

			return false;
		}

		return true;
	}
}
