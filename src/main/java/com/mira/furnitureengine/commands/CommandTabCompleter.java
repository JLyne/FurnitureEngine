package com.mira.furnitureengine.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mira.furnitureengine.FurnitureManager;
import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.mira.furnitureengine.FurnitureEngine;
import org.jetbrains.annotations.NotNull;

public class CommandTabCompleter implements TabCompleter {
	private final FurnitureManager furnitureManager = FurnitureEngine.getPlugin(FurnitureEngine.class).getFurnitureManager();

	@Override
	public List<String> onTabComplete(
			@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

		if (args.length == 1) {
			List<String> autoCompletion = new ArrayList<>();

			if (sender.hasPermission("furnitureengine.admin")) {
				autoCompletion.add("reload");
			}

			if (sender.hasPermission("furnitureengine.give")) {
				autoCompletion.add("give");
			}

			if (sender.hasPermission("furnitureengine.remove")) {
				autoCompletion.add("remove");
			}

			if (sender.hasPermission("furnitureengine.get")) {
				autoCompletion.add("get");
			}

			return autoCompletion;
		}

		if(args[0].equals("give") && sender.hasPermission("furnitureengine.give")) {
			switch (args.length) {
				case 2: {
					List<String> autoCompletion = new ArrayList<>();

					for (Player p : Bukkit.getOnlinePlayers()) {
						autoCompletion.add(p.getName());
					}

					return autoCompletion;
				}
				case 3:
					return furnitureManager.getAllFurniture().keySet().stream()
							.filter(id -> id.startsWith(args[2]))
							.collect(Collectors.toList());
				case 4: {
					List<String> autoCompletion = new ArrayList<>();
					autoCompletion.add(Integer.toString(1));
					return autoCompletion;
				}
			}
		}

		if(args[0].equals("get") && sender.hasPermission("furnitureengine.get")) {
			switch (args.length) {
				case 2:
					return furnitureManager.getAllFurniture().keySet().stream()
							.filter(id -> id.startsWith(args[1]))
							.collect(Collectors.toList());
				case 3:
					List<String> autoCompletion = new ArrayList<>();
					autoCompletion.add(Integer.toString(1));
					return autoCompletion;
			}
		}

		if(args[0].equals("remove") && sender.hasPermission("furnitureengine.remove")) {
			switch (args.length) {
				case 2 -> {
					List<String> autoCompletion = new ArrayList<>();

					autoCompletion.add("0");

					return autoCompletion;
				}
				case 3 -> {
					List<String> autoCompletion = new ArrayList<>();

					autoCompletion.add("0");

					return autoCompletion;
				}
				case 4 -> {
					List<String> autoCompletion = new ArrayList<>();

					autoCompletion.add("0");

					return autoCompletion;
				}
			}
		}

		return null;
	}
}