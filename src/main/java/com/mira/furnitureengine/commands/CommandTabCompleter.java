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
			autoCompletion.add("reload");
			autoCompletion.add("give");
			autoCompletion.add("remove");
			autoCompletion.add("get");

			return autoCompletion;
		}

		if (args.length == 2 && args[0].equals("give")) {
			List<String> autoCompletion = new ArrayList<>();

			for (Player p : Bukkit.getOnlinePlayers()) {
				autoCompletion.add(p.getName());
			}

			return autoCompletion;
		}

		if (args.length == 3 && args[0].equals("give")) {
			return furnitureManager.getAllFurniture().keySet().stream()
					.filter(id -> id.startsWith(args[2]))
					.collect(Collectors.toList());
		}

		if (args.length == 4 && args[0].equals("give")) {
			List<String> autoCompletion = new ArrayList<>();
			autoCompletion.add(Integer.toString(1));
			return autoCompletion;
		}

		if (args.length == 2 && args[0].equals("get")) {
			return furnitureManager.getAllFurniture().keySet().stream()
					.filter(id -> id.startsWith(args[1]))
					.collect(Collectors.toList());
		}

		if (args.length == 3 && args[0].equals("get")) {
			List<String> autoCompletion = new ArrayList<>();
			autoCompletion.add(Integer.toString(1));
			return autoCompletion;
		}

		if (args.length == 2 && args[0].equals("remove")) {
			List<String> autoCompletion = new ArrayList<>();

			autoCompletion.add("0");

			return autoCompletion;
		}

		if (args.length == 3 && args[0].equals("remove")) {
			List<String> autoCompletion = new ArrayList<>();

			autoCompletion.add("0");

			return autoCompletion;
		}

		if (args.length == 4 && args[0].equals("remove")) {
			List<String> autoCompletion = new ArrayList<>();

			autoCompletion.add("0");

			return autoCompletion;
		}

		return null;
	}
}