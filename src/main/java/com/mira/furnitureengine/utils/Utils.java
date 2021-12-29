package com.mira.furnitureengine.utils;

import com.mira.furnitureengine.FurnitureEngine;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Utils {
	private static final FurnitureEngine plugin = FurnitureEngine.getPlugin(FurnitureEngine.class);

	public static Rotation getRotationFromYaw(float yaw, boolean fullRotation) {
		if (yaw < 0) {
			yaw += 360;
		}

		yaw %= 360;
		int i = (int) ((yaw + 8) / 22.5);
		Rotation rotation = Rotation.NONE;

		if (fullRotation) {
			// 8 Side Rotation
			// West
			if (i == 15 || i == 0 || i == 1 || i == 16) rotation = Rotation.FLIPPED;
			// North-West
			if (i == 2) rotation = Rotation.FLIPPED_45;
			// North
			if (i == 3 || i == 4 || i == 5) rotation = Rotation.COUNTER_CLOCKWISE;
			// North-East
			if (i == 6) rotation = Rotation.COUNTER_CLOCKWISE_45;
			// South-East
			if (i == 10) rotation = Rotation.CLOCKWISE_45;
			// South
			if (i == 11 || i == 12 || i == 13) rotation = Rotation.CLOCKWISE;
			// South-West
			if (i == 14) rotation = Rotation.CLOCKWISE_135;
			// East
//			if (i == 7 || i == 8 || i == 9) rotation = Rotation.NONE;
		} else {
			// 4 Side Rotation
//			if (i == 6 || i == 7 || i == 8 || i == 9) rotation = Rotation.NONE;
			if (i == 10 || i == 11 || i == 12 || i == 13 || i == 14) rotation = Rotation.CLOCKWISE;
			if (i == 2 || i == 3 || i == 4 || i == 5) rotation = Rotation.COUNTER_CLOCKWISE;
			if (i == 15 || i == 16 || i == 0 || i == 1) rotation = Rotation.FLIPPED;
		}

		return rotation;
	}

	public static String getText(String input) {
		int s = input.indexOf("{") + 1;
		int e = input.lastIndexOf("}");
		if (s > 0 && e > s) {
			return (input.substring(s, e));
		}
		return null;
	}

	public static boolean checkPlacePermission(Block block, Player player) {
		boolean placePermission = !plugin.getConfig().getBoolean("Options.check-place-permissions")
				|| player.hasPermission("furnitureengine.blockplace");

		return placePermission
				&& plugin.getWorldGuardHandler().checkPermission(block, player)
				&& plugin.getGriefPreventionHandler().checkPermission(block, player);
	}

	public static boolean checkBreakPermissions(Block block, Player player) {
		boolean placePermission = !plugin.getConfig().getBoolean("Options.check-break-permissions")
				|| player.hasPermission("furnitureengine.blockbreak");

		return placePermission
				&& plugin.getWorldGuardHandler().checkPermission(block, player)
				&& plugin.getGriefPreventionHandler().checkPermission(block, player);
	}
}
