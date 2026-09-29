package com.mira.furnitureengine.conditions;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class ConditionWorld {
	public static boolean check(boolean org, Player player, String input) {
		NamespacedKey key = NamespacedKey.fromString(input.replace("w=", ""));

		if (player.getLocation().getBlock().getWorld().getKey().equals(key)) {
			return org;
		}

		return false;
	}
}
