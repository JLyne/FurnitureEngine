package com.mira.furnitureengine.commands;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.FurnitureManager;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

@SuppressWarnings("UnstableApiUsage")
public class FurnitureArgumentType implements CustomArgumentType.Converted<Furniture, String> {
	private final FurnitureManager furnitureManager;

	public FurnitureArgumentType(FurnitureEngine plugin) {
		this.furnitureManager = plugin.getFurnitureManager();
	}

	@Override
	public @NotNull ArgumentType<String> getNativeType() {
		return word();
	}

	@Override
	public @NotNull Furniture convert(@NotNull String input) throws CommandSyntaxException {
		Furniture furniture = furnitureManager.getFurnitureById(input);

		if(furniture == null) {
			throw new SimpleCommandExceptionType(new LiteralMessage(input + " is not a a valid furniture type"))
					.create();
		}

		return furniture;
	}

	@Override
	public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(
			@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		String search = builder.getRemainingLowerCase();

		furnitureManager.getAllFurniture().keySet().stream()
				.filter(key -> key.startsWith(search))
				.forEach(builder::suggest);

		return CompletableFuture.completedFuture(builder.build());
	}
}
