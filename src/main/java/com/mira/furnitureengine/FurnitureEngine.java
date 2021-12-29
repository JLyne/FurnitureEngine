package com.mira.furnitureengine;

import com.mira.furnitureengine.commands.CommandTabCompleter;
import com.mira.furnitureengine.commands.CoreCommand;
import com.mira.furnitureengine.handlers.GriefPreventionHandler;
import com.mira.furnitureengine.handlers.WorldGuardHandler;
import com.mira.furnitureengine.listeners.FurnitureBreak;
import com.mira.furnitureengine.listeners.FurniturePlace;
import com.mira.furnitureengine.listeners.RightClick;
import com.mira.furnitureengine.tags.FurnitureTag;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class FurnitureEngine extends JavaPlugin {
	public final NamespacedKey furnitureKey;
	public final FurnitureTag furnitureTagType;

	private WorldGuardHandler worldGuardHandler;
	private GriefPreventionHandler griefPreventionHandler;

	private final FurnitureManager furnitureManager;

	public FurnitureEngine() {
		super();

		furnitureManager = new FurnitureManager(this);
		furnitureKey = new NamespacedKey(this, "furniture");
		furnitureTagType = new FurnitureTag(this);
	}

	public void onEnable() {
		loadConfig();

		furnitureManager.loadFurniture();

		try {
			worldGuardHandler = new WorldGuardHandler();
		} catch (NoClassDefFoundError e) {
			getLogger().warning("WorldGuard not found");
		}

		try {
			griefPreventionHandler = new GriefPreventionHandler();
		} catch (NoClassDefFoundError e) {
			getLogger().warning("WorldGuard not found");
		}

		// default
		getCommand("furnitureengine").setExecutor(new CoreCommand());
		getCommand("furnitureengine").setTabCompleter(new CommandTabCompleter());

		// Event Handlers
		new RightClick(this);
		new FurniturePlace(this);
		new FurnitureBreak(this);
	}

	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public FurnitureManager getFurnitureManager() {
		return furnitureManager;
	}

	public WorldGuardHandler getWorldGuardHandler() {
		return worldGuardHandler;
	}

	public GriefPreventionHandler getGriefPreventionHandler() {
		return griefPreventionHandler;
	}
}
