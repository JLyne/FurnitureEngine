package com.mira.furnitureengine.handlers;

import dev.geco.gsit.objects.GetUpReason;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import dev.geco.gsit.api.GSitAPI;

public class GSitHandler {
	private final boolean gSitEnabled;

	public GSitHandler() {
		gSitEnabled = Bukkit.getPluginManager().isPluginEnabled("GSit");
	}

	public boolean isgSitEnabled() {
		return gSitEnabled;
	}

	public void sit(Block seat, Player player, double offset) {
		if (!gSitEnabled) {
			return;
		}

		if(GSitAPI.getSeats(seat).size() == 0 && GSitAPI.canPlayerSit(player)) {
			GSitAPI.createSeat(seat, player, true, 0, -0.4 + offset, 0, 0f, true);
		}
	}

	public void removeSeats(Block block) {
		if (!gSitEnabled) {
			return;
		}

		GSitAPI.getSeats(block).forEach(seat -> GSitAPI.removeSeat(seat.getEntity(), GetUpReason.BREAK));
	}
}
