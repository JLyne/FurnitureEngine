package com.mira.furnitureengine.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FurnitureBreakEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private boolean dropitem;
	
	private final Player interactingPlayer;
	
	private final Location furnitureLocation;
	
	public FurnitureBreakEvent(Player interactingPlayer, Location furnitureLocation) {
		this.interactingPlayer = interactingPlayer;
		this.furnitureLocation = furnitureLocation;
		
		// Drops items by default
		dropItems(true);
	}
	
	public Player getPlayer() {
		return interactingPlayer;
	}
	
	public Location getFurnitureLocation() {
		return furnitureLocation;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public boolean isDroppingItems() {
		return dropitem;
	}
	
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	public void dropItems(boolean yn) {
		dropitem = yn;
	}

	public @NotNull HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
