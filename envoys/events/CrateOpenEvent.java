package org.cathal.ultimateEnvoy.envoys.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cathal.ultimateEnvoy.envoys.ActiveEnvoy;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;

public class CrateOpenEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Crate crate;
    private final Player player;
    private final Location location;
    private final ActiveEnvoy activeEnvoy;
    public CrateOpenEvent(Crate crate, Player player, Location location, ActiveEnvoy envoy){
        this.crate =crate;
        this.player = player;
        this.location = location;
        this.activeEnvoy = envoy;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Crate getCrate(){
        return crate;
    }
    public Player getPlayer(){
        return player;
    }
    public Location getLocation(){
        return location;
    }
    public ActiveEnvoy getActiveEnvoy(){return activeEnvoy;}
}
