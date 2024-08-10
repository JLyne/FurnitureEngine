package com.mira.furnitureengine;

import com.mira.furnitureengine.commands.CoreCommand;
import com.mira.furnitureengine.creativeitemfilter.CreativeItemFilterHandler;
import com.mira.furnitureengine.customitems.CustomItemsHandler;
import com.mira.furnitureengine.handlers.GSitHandler;
import com.mira.furnitureengine.handlers.GriefPreventionHandler;
import com.mira.furnitureengine.handlers.PlotSquaredHandler;
import com.mira.furnitureengine.handlers.WorldGuardHandler;
import com.mira.furnitureengine.listeners.FurnitureBreak;
import com.mira.furnitureengine.listeners.FurniturePlace;
import com.mira.furnitureengine.listeners.PlayerJoin;
import com.mira.furnitureengine.listeners.RightClick;
import com.mira.furnitureengine.tags.FurnitureTag;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public final class FurnitureEngine extends JavaPlugin implements Listener {
	public final NamespacedKey furnitureKey;
	public final FurnitureTag furnitureTagType;

	private WorldGuardHandler worldGuardHandler;
	private GriefPreventionHandler griefPreventionHandler;
	private PlotSquaredHandler plotSquaredHandler;
	private GSitHandler gSitHandler;
	private CreativeItemFilterHandler creativeItemFilterHandler;
	private CustomItemsHandler customItemsHandler;

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

		LifecycleEventManager<Plugin> manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> new CoreCommand(event.registrar()));

		getServer().getPluginManager().registerEvents(this, this);

		// Event Handlers
		new RightClick(this);
		new FurniturePlace(this);
		new FurnitureBreak(this);
		new PlayerJoin(this);
	}

	public void onDisable() {
		recipeManager.unregisterRecipes();
	}

	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		switch (event.getPlugin().getName()) {
			case "GriefPrevention" -> {
				getLogger().info("Initialising GriefPrevention handler");
				griefPreventionHandler = new GriefPreventionHandler();
			}
			case "WorldGuard" -> {
				getLogger().info("Initialising WorldGuard handler");
				worldGuardHandler = new WorldGuardHandler();
			}
			case "PlotSquared" -> {
				getLogger().info("Initialising PlotSquared handler");
				plotSquaredHandler = new PlotSquaredHandler();
			}
			case "CreativeItemFilter" -> {
				getLogger().info("Initialising CreativeItemFilter handler");
				creativeItemFilterHandler = new CreativeItemFilterHandler(this);
			}
			case "GSit" -> {
				getLogger().info("Initialising GSit handler");
				gSitHandler = new GSitHandler();
			}
			case "CustomItems" -> {
				getLogger().info("Registering CustomItems provider");
				customItemsHandler = new CustomItemsHandler(this);
			}
		}
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		switch (event.getPlugin().getName()) {
			case "GriefPrevention" -> {
				if (griefPreventionHandler != null) {
					getLogger().info("Disabling GriefPrevention handler");
					griefPreventionHandler = null;
				}
			}
			case "WorldGuard" -> {
				if (worldGuardHandler != null) {
					getLogger().info("Disabling WorldGuard handler");
					worldGuardHandler = null;
				}
			}
			case "PlotSquared" -> {
				if (plotSquaredHandler != null) {
					getLogger().info("Disabling PlotSquared handler");
					plotSquaredHandler = null;
				}
			}
			case "CreativeItemFilter" -> {
				if (creativeItemFilterHandler != null) {
					getLogger().info("Disabling CreativeItemFilter handler");
					creativeItemFilterHandler = null;
				}
			}
			case "GSit" -> {
				if (gSitHandler != null) {
					getLogger().info("Disabling GSit handler");
					gSitHandler = null;
				}
			}
			case "CustomItems" -> {
				if (customItemsHandler != null) {
					getLogger().info("Disabling CustomItems provider");
					customItemsHandler = null;
				}
			}
		}
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
