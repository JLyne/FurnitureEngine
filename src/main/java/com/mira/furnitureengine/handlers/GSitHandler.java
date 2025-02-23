package com.mira.furnitureengine.handlers;

import dev.geco.gsit.object.GStopReason;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import dev.geco.gsit.api.GSitAPI;

public class GSitHandler {
	public void sit(Block seat, Player player, double offset) {
		if(GSitAPI.getSeatsByBlock(seat).isEmpty()
				&& GSitAPI.canEntityUseSit(player)
				&& !GSitAPI.isEntitySitting(player)
				&& !GSitAPI.isPlayerPosing(player)
				&& !GSitAPI.isPlayerCrawling(player)) {
			GSitAPI.createSeat(seat, player, true, 0, -0.4 + offset, 0, 0f, true);
		}
	}

	public void removeSeats(Block block) {
		GSitAPI.getSeatsByBlock(block).forEach(seat -> GSitAPI.removeSeat(seat, GStopReason.BLOCK_BREAK, true));
	}
}
