package com.mira.furnitureengine.handlers;

import com.plotsquared.bukkit.BukkitPlatform;
import com.plotsquared.bukkit.player.BukkitPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.configuration.Settings;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.permissions.Permission;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.implementations.BreakFlag;
import com.plotsquared.core.plot.flag.implementations.DoneFlag;
import com.plotsquared.core.plot.flag.implementations.PlaceFlag;
import com.plotsquared.core.plot.flag.types.BlockTypeWrapper;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class PlotSquaredHandler {
	private final boolean psEnabled;
	private BukkitPlatform plotSquared = null;

	public PlotSquaredHandler() {
		psEnabled = Bukkit.getPluginManager().isPluginEnabled("PlotSquared");

		if(!psEnabled) {
			return;
		}

		plotSquared = (BukkitPlatform) Bukkit.getPluginManager().getPlugin("PlotSquared");
	}

	// Adapted from Plotsquared's BlockEventListener.blockCreate method,
	// because they don't have an api for build permissions (why?)
	public boolean checkBuildPermission(Block block, Player player) {
		if (!psEnabled) {
			return true;
		}

		Location location = BukkitUtil.adapt(block.getLocation());
		PlotArea area = location.getPlotArea();

		if (area == null) {
			return true;
		}

		BukkitPlayer pp = BukkitUtil.adapt(player);
		Plot plot = area.getPlot(location);
		if (plot != null) {
			if ((location.getY() >= area.getMaxBuildHeight() || location.getY() < area
					.getMinBuildHeight()) && !pp.hasPermission(Permission.PERMISSION_ADMIN_BUILD_HEIGHT_LIMIT)) {
				return false;
			}

			if (!plot.hasOwner()) {
				return false; //Entities get removed in unowned plots so don't allow
			} else if (!plot.isAdded(pp.getUUID())) {
				List<BlockTypeWrapper> place = plot.getFlag(PlaceFlag.class);

				if (place != null) {
					if (place.contains(
							BlockTypeWrapper.get(BukkitAdapter.asBlockType(block.getType())))) {
						return true;
					}
				}

				return pp.hasPermission(Permission.PERMISSION_ADMIN_BUILD_OTHER);
			} else if (Settings.Done.RESTRICT_BUILDING && DoneFlag.isDone(plot)) {
				return pp.hasPermission(Permission.PERMISSION_ADMIN_BUILD_OTHER);
			}
		} else return false; //Entities get removed on roads so don't allow

		return true;
	}

	// Adapted from Plotsquared's BlockEventListener.blockDestroy method,
	// because they don't have an api for build permissions (why?)
	public boolean checkBreakPermission(Block block, Player player) {
		if (!psEnabled) {
			return true;
		}

        Location location = BukkitUtil.adapt(block.getLocation());
        PlotArea area = location.getPlotArea();

        if (area == null) {
            return true;
        }

        Plot plot = area.getPlot(location);

        if (plot != null) {
            BukkitPlayer plotPlayer = BukkitUtil.adapt(player);
            // == rather than <= as we only care about the "ground level" not being destroyed
            if (block.getY() == area.getMinGenHeight()) {
                if (!plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_DESTROY_GROUNDLEVEL)) {
                    return false;
                }
            } else if ((location.getY() >= area.getMaxBuildHeight() || location.getY() < area
                    .getMinBuildHeight()) && !plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_BUILD_HEIGHT_LIMIT)) {
                return false;
            }
            if (!plot.hasOwner()) {
				return plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_DESTROY_UNOWNED, true);
			}
            if (!plot.isAdded(plotPlayer.getUUID())) {
                List<BlockTypeWrapper> destroy = plot.getFlag(BreakFlag.class);
                final BlockType blockType = BukkitAdapter.asBlockType(block.getType());

                for (final BlockTypeWrapper blockTypeWrapper : destroy) {
                    if (blockTypeWrapper.accepts(blockType)) {
                        return true;
                    }
                }

				return plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_DESTROY_OTHER);
			} else if (Settings.Done.RESTRICT_BUILDING && DoneFlag.isDone(plot)) {
				return plotPlayer.hasPermission(Permission.PERMISSION_ADMIN_BUILD_OTHER);
            }

            return true;
        }

        BukkitPlayer pp = BukkitUtil.adapt(player);
		return pp.hasPermission(Permission.PERMISSION_ADMIN_DESTROY_ROAD);
	}
}
