package com.mira.furnitureengine.tags;

import com.mira.furnitureengine.Furniture;
import com.mira.furnitureengine.FurnitureEngine;
import com.mira.furnitureengine.FurnitureManager;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FurnitureTag implements PersistentDataType<String, Furniture> {
	private final FurnitureManager furnitureManager;

	public FurnitureTag(FurnitureEngine plugin) {
		furnitureManager = plugin.getFurnitureManager();
	}

	@NotNull
	@Override
	public Class<String> getPrimitiveType() {
		return String.class;
	}

	@NotNull
	@Override
	public Class<Furniture> getComplexType() {
		return Furniture.class;
	}

	@NotNull
	public String toPrimitive(@NotNull Furniture complex, @NotNull PersistentDataAdapterContext context) {
		return complex.getId();
	}

	@NotNull
	public Furniture fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
		Furniture furniture = furnitureManager.getFurnitureById(primitive);
		return furniture != null ? furniture : new Furniture(primitive);
	}
}
