package com.mira.furnitureengine.commands;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureManager;
import com.mira.furnitureengine.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.utils.*;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

public class CoreCommand implements CommandExecutor {
	private final FurnitureEngine plugin = FurnitureEngine.getPlugin(FurnitureEngine.class);
	private final FurnitureManager furnitureManager = plugin.getFurnitureManager();
	private final RecipeManager recipeManager = plugin.getRecipeManager();

	public boolean onCommand(
			@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

		if (args.length == 0) {
			sender.sendMessage(
					ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.RED + "Incorrect Command usage!");
			return false;
		}

		if (args[0].equals("reload")) {
			this.reloadCommand(sender);
			return true;
		}

		if (args[0].equals("give")) {
			if (args.length == 1) {
				sender.sendMessage(
						ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.RED + "Incorrect Command usage!");
				return false;
			} else {
				Furniture item = furnitureManager.getFurnitureById(args[2]);
				Player player = Bukkit.getPlayer(args[1]);

				if(item != null) {
					if (args.length == 4 && Integer.parseInt(args[3]) != 0) {
						ItemUtils.giveItem(player, item, Integer.parseInt(args[3]), null);
					} else {
						ItemUtils.giveItem(player, item, 1, null);
					}
				}
			}

			return true;
		}

		if (args[0].equals("get")) {
			if (args.length == 1) {
				sender.sendMessage(
						ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.RED + "Incorrect Command usage!");
				return false;
			} else {
				if (sender instanceof Player) {
					Furniture item = furnitureManager.getFurnitureById(args[1]);

					if(item != null) {
						if (args.length == 3 && Integer.parseInt(args[2]) != 0) {
							ItemUtils.giveItem((Player) sender, item, Integer.parseInt(args[2]), null);
						} else {
							ItemUtils.giveItem((Player) sender, item, 1, null);
						}
					}
				}
			}

			return true;
		}

		if (args[0].equals("remove")) {
			if (sender instanceof Player p) {
				if (args.length < 4) {
					sender.sendMessage(
							ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.RED + "Incorrect Command usage!");
					return false;
				} else {
					furnitureManager.breakFurniture(
							new Location(p.getLocation().getWorld(), Double.parseDouble(args[1]),
										 Double.parseDouble(args[2]), Double.parseDouble(args[3])), p);
				}

				return true;
			}
		}

		return false;
	}

	// Commands

	public void reloadCommand(CommandSender sender) {
		plugin.reloadConfig();
		furnitureManager.loadFurniture();
		recipeManager.registerRecipes();
		sender.sendMessage(
				ChatColor.GOLD + "Furniture" + ChatColor.YELLOW + "Engine" + ChatColor.DARK_GRAY + " � " + ChatColor.GRAY + "Config reloaded!");
	}
}
