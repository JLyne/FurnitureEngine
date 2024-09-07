package com.mira.furnitureengine.commands;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureManager;
import com.mira.furnitureengine.RecipeManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.utils.*;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.messageshelper.Message;

import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.blockPosition;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.players;

@SuppressWarnings("UnstableApiUsage")
public final class CoreCommand {
	private final FurnitureEngine plugin = FurnitureEngine.getPlugin(FurnitureEngine.class);
	private final FurnitureManager furnitureManager = plugin.getFurnitureManager();
	private final RecipeManager recipeManager = plugin.getRecipeManager();

	public CoreCommand(Commands commands) {
		FurnitureArgumentType furnitureArgumentType = new FurnitureArgumentType(plugin);

		LiteralCommandNode<CommandSourceStack> reloadCommand = literal("reload")
				.requires(source -> source.getSender().hasPermission("furnitureengine.admin"))
				.executes(this::onReload)
				.build();

		LiteralCommandNode<CommandSourceStack> giveCommand = literal("give")
				.requires(source -> source.getSender().hasPermission("furnitureengine.give"))
				.then(argument("players", players())
							  .then(argument("furniture", furnitureArgumentType)
											.executes(ctx -> onGive(ctx, 1))
											.then(argument("amount", integer(0, 6400))
														  .executes(ctx -> {
															  int amount = ctx.getArgument("amount", int.class);
															  return onGive(ctx, amount);
														  }))))
				.build();

		ArgumentCommandNode<CommandSourceStack, Furniture> getStuff = argument("furniture", furnitureArgumentType)
				.executes(this::onGet)
				.build();

		LiteralCommandNode<CommandSourceStack> getCommand = literal("get")
				.requires(source -> source.getSender().hasPermission("furnitureengine.get"))
				.then(getStuff)
				.build();

		LiteralCommandNode<CommandSourceStack> removeCommand = literal("remove")
				.requires(source -> source.getSender().hasPermission("furnitureengine.remove"))
				.then(argument("location", blockPosition())
							  .executes(this::onRemove)).build();

		LiteralCommandNode<CommandSourceStack> topCommand = literal("furnitureengine")
				.then(reloadCommand)
				.then(giveCommand)
				.then(getCommand)
				.then(removeCommand)
				.build();

		commands.register(topCommand, "Main command for FurnitureEngine");

		commands.register(literal("furniture")
								  .requires(source -> source.getSender().hasPermission("furnitureengine.get"))
								  .then(getStuff)
								  .build(), "Get an item of furniture");
	}

	public int onGive(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
		Furniture furniture = ctx.getArgument("furniture", Furniture.class);
		List<Player> players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());

		for(Player player: players) {
			ItemStack item = ItemUtils.giveItem(player, furniture, amount, null);
			plugin.getMessagesHelper().send(ctx.getSource().getSender(), Message.builder("command.give-success")
					.replacement("player", player.displayName().hoverEvent(player.asHoverEvent()))
                    .replacement("amount", String.valueOf(amount))
                    .replacement("item", item.displayName().hoverEvent(item.asHoverEvent()))
                    .build());
		}

		return Command.SINGLE_SUCCESS;
	}

	public int onGet(CommandContext<CommandSourceStack> ctx) {
		if(!(ctx.getSource().getSender() instanceof Player player)) {
			plugin.getMessagesHelper().send(ctx.getSource().getSender(), Message.builder("command.not-a-player")
					.build());

			return Command.SINGLE_SUCCESS;
		}

		Furniture furniture = ctx.getArgument("furniture", Furniture.class);
		ItemStack item = ItemUtils.giveItem(player, furniture, 1, null);

		plugin.getMessagesHelper().send(ctx.getSource().getSender(), Message.builder("command.get-success")
				.replacement("player", player.displayName().hoverEvent(player.asHoverEvent()))
				.replacement("amount", String.valueOf(1))
				.replacement("item", item.displayName().hoverEvent(item.asHoverEvent()))
				.build());

		return Command.SINGLE_SUCCESS;
	}

	public int onRemove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSender sender = ctx.getSource().getSender();
		Player actor = sender instanceof Player ? (Player) sender : null;

		BlockPosition pos = ctx.getArgument("location", BlockPositionResolver.class).resolve(ctx.getSource());
		Location location = new Location(ctx.getSource().getLocation().getWorld(), pos.x(), pos.y(), pos.z());

		furnitureManager.breakFurniture(location, actor);
		return Command.SINGLE_SUCCESS;
	}

	public int onReload(CommandContext<CommandSourceStack> ctx) {
		plugin.reloadConfig();
		furnitureManager.loadFurniture();
		recipeManager.registerRecipes();

		plugin.getMessagesHelper().send(ctx.getSource().getSender(), Message.builder("command.reload-success")
				.build());

		return Command.SINGLE_SUCCESS;
	}
}
