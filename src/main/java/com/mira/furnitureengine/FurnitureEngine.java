package com.mira.furnitureengine;

import com.mira.furnitureengine.commands.CommandTabCompleter;
import com.mira.furnitureengine.commands.CoreCommand;
import com.mira.furnitureengine.handlers.GSitHandler;
import com.mira.furnitureengine.handlers.GriefPreventionHandler;
import com.mira.furnitureengine.handlers.PlotSquaredHandler;
import com.mira.furnitureengine.handlers.WorldGuardHandler;
import com.mira.furnitureengine.listeners.FurnitureBreak;
import com.mira.furnitureengine.listeners.FurniturePlace;
import com.mira.furnitureengine.listeners.PlayerJoin;
import com.mira.furnitureengine.listeners.RightClick;
import com.mira.furnitureengine.tags.FurnitureTag;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class FurnitureEngine extends JavaPlugin {
	public final NamespacedKey furnitureKey;
	public final FurnitureTag furnitureTagType;

	private WorldGuardHandler worldGuardHandler;
	private GriefPreventionHandler griefPreventionHandler;
	private PlotSquaredHandler plotSquaredHandler;
	private GSitHandler gSitHandler;

	private final FurnitureManager furnitureManager;
	private final RecipeManager recipeManager;

	public FurnitureEngine() {
		super();

		furnitureManager = new FurnitureManager(this);
		recipeManager = new RecipeManager(this);
		furnitureKey = new NamespacedKey(this, "furniture");
		furnitureTagType = new FurnitureTag(this);
	}

	@Override
	public void onEnable() {
		loadConfig();

		furnitureManager.loadFurniture();
		recipeManager.registerRecipes();

		try {
			worldGuardHandler = new WorldGuardHandler();
		} catch (NoClassDefFoundError e) {
			getLogger().warning("WorldGuard not found");
		}

		try {
			griefPreventionHandler = new GriefPreventionHandler();
		} catch (NoClassDefFoundError e) {
			getLogger().warning("GriefPrevention not found");
		}

		try {
			plotSquaredHandler = new PlotSquaredHandler();
		} catch (NoClassDefFoundError e) {
			getLogger().warning("PlotSquared not found");
		}

		gSitHandler = new GSitHandler();

		if(!gSitHandler.isgSitEnabled()) {
			getLogger().warning("GSit not found");
		}

		// default
		getCommand("furnitureengine").setExecutor(new CoreCommand());
		getCommand("furnitureengine").setTabCompleter(new CommandTabCompleter());

		// Event Handlers
		new RightClick(this);
		new FurniturePlace(this);
		new FurnitureBreak(this);
		new PlayerJoin(this);
	}

	public void onDisable() {
		recipeManager.unregisterRecipes();
	}

	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public FurnitureManager getFurnitureManager() {
		return furnitureManager;
	}

	public RecipeManager getRecipeManager() {
		return recipeManager;
	}

	public WorldGuardHandler getWorldGuardHandler() {
		return worldGuardHandler;
	}

	public GriefPreventionHandler getGriefPreventionHandler() {
		return griefPreventionHandler;
	}

	public PlotSquaredHandler getPlotSquaredHandler() {
		return plotSquaredHandler;
	}

	public GSitHandler getgSitHandler() {
		return gSitHandler;
	}
}
