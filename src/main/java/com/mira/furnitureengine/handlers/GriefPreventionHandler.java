package com.mira.furnitureengine.handlers;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

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

		if(claim == null) {
			return true;
		}

		Supplier<String> denialMessage = claim.checkPermission(player, ClaimPermission.Build, null);

		if(denialMessage != null) {
			player.sendMessage(Component.text(denialMessage.get()).color(NamedTextColor.RED));
			return false;
		}

		return true;
	}
}
