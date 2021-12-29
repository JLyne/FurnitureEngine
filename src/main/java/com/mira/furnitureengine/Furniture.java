package com.mira.furnitureengine;

import com.mira.furnitureengine.conditions.*;
import com.mira.furnitureengine.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class Furniture {
	private final String id;
	private String displayName = "";
	private List<String> lore = new ArrayList<>();
	private Material material = Material.OAK_PLANKS;

	private int customModelData = 0;
	private int height = 1;
	private int width = 1;
	private int length = 1;
	private int lightLevel = 0;

	private boolean fullRotate = false;
	private boolean cancelDrop = false;

	private Map<String, List<String>> commands = new HashMap<>();
	private List<String> conditions = new ArrayList<>();

	public Furniture(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		Objects.requireNonNull(displayName);
		this.displayName = displayName;
	}

	public int getCustomModelData() {
		return customModelData;
	}

	public void setCustomModelData(int customModelData) {
		this.customModelData = customModelData;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLightLevel() {
		return lightLevel;
	}

	public void setLightLevel(int lightLevel) {
		this.lightLevel = lightLevel;
	}

	public void setSize(int height, int width, int length) {
		this.height = height;
		this.width = width;
		this.length = length;
	}

	public boolean isFullRotate() {
		return fullRotate;
	}

	public void setFullRotate(boolean fullRotate) {
		this.fullRotate = fullRotate;
	}

	public boolean isCancelDrop() {
		return cancelDrop;
	}

	public void setCancelDrop(boolean cancelDrop) {
		this.cancelDrop = cancelDrop;
	}

	public Map<String, List<String>> getCommands() {
		return commands;
	}

	public void setCommands(Map<String, List<String>> commands) {
		Objects.requireNonNull(commands);
		this.commands = commands;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		Objects.requireNonNull(lore);
		this.lore = lore;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		Objects.requireNonNull(material);
		this.material = material;
	}

	public List<String> getConditions() {
		return conditions;
	}

	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

	public boolean isProp() {
		return height == 0 || width == 0 || length == 0;
	}

	public void executeCommand(String mode, @Nullable Player player, @Nullable Location loc) {
		List<String> modeCommands = commands.getOrDefault(mode, Collections.emptyList());
		int i = 0;

		for (String modeCommand : modeCommands) {
			if (player != null) {
				modeCommand = modeCommand.replace("<player>", player.getName());
			}

			if (loc != null) {
				modeCommand = modeCommand.replace("<location>", loc.getX() + " " + (loc.getY() - 1) + " " + loc.getZ());
				System.out.println(loc.getX() + " " + loc.getY() + " " + loc.getZ());
			}

			if (checkCondition("-OnCommand[" + (i++) + "]", player)) {
				if (modeCommand.startsWith("[c]")) {
					// Console Command
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), modeCommand.substring(3));
				} else if (player != null) {
					// Normal Command
					player.performCommand(modeCommand);
				}
			}
		}
	}

	public boolean checkCondition(String type, Player player) {
		boolean isOk = true;

		for (String text : conditions) {
			if (text.endsWith(type)) {
				if (isOk) {
					String prefix = text.substring(0, text.lastIndexOf("{"));
					String input = Utils.getText(text);

					switch (prefix.toLowerCase()) {
						case "biome" -> isOk = ConditionBiome.check(isOk, player, input);
						case "world" -> isOk = ConditionWorld.check(isOk, player, input);
						case "permission" -> isOk = ConditionPermission.check(isOk, player, input);
						case "weather" -> isOk = ConditionWeather.check(isOk, player, input);
						case "time" -> isOk = ConditionTime.check(isOk, player, input);
					}
				} else return false;
			}
		}

		return isOk;
	}
}
