package org.cathal.ultimateEnvoy.envoys.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cathal.ultimateEnvoy.envoys.ActiveEnvoy;

public class EnvoyEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final ActiveEnvoy activeEnvoy;

    public EnvoyEndEvent(ActiveEnvoy activeEnvoy){
        this.activeEnvoy = activeEnvoy;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ActiveEnvoy getActiveEnvoy(){
        return activeEnvoy;
    }
}
