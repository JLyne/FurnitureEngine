package com.mira.furnitureengine.conditions;

import org.bukkit.entity.Player;

public class ConditionTime {
	public static boolean check(boolean org, Player player, String input) {
		input = input.replace("t=", "");

		long time = player.getLocation().getWorld().getTime();

		switch (input) {
			case "day" -> {
				if (time >= 0L && time <= 12000L) {
					return org;
				}
			}
			case "night" -> {
				if (time >= 12000L && time <= 24000L) {
					return org;
				}
			}
		}

		return false;
	}
}
