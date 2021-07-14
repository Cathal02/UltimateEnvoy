package org.cathal.ultimateEnvoy.gui;

import org.bukkit.entity.Player;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.gui.managers.Page;

public class InventoryNavigator {

    UltimateEnvoy plugin;
    public InventoryNavigator(UltimateEnvoy plugin){
        this.plugin = plugin;
    }
    public void handlePageChange(Page page, Player player){
        switch (page){
            case HOME:
                plugin.getEnvoyGuiManager().open(player);
                break;
            case ENVOY_EDITOR_HOME:
                plugin.getEnvoyGuiManager().getEnvoyHomeGuiManager().open(player);
                break;
            case CRATE_EDITOR_HOME:
                plugin.getEnvoyGuiManager().getCrateCreatorGui().open(player);
                break;
        }
    }

    public void handlePageChange(Page page, Player player, Crate crate){
        switch (page){
            case CRATE_EDITOR:
                plugin.getEnvoyGuiManager().getCrateEditorGui().open(player,crate);
                break;
        }
    }

    public void handlePageChange(Page page, Player player, Envoy envoy){
        switch (page){
            case ENVOY_EDITOR:
                plugin.getEnvoyGuiManager().getEnvoyEditorGui().open(player,envoy);
                break;
            case ENVOY_EDITOR_CHEST:
                plugin.getEnvoyGuiManager().getEnvoyCrateEditor().open(player,envoy);
                break;
            case ENVOY_EDITOR_SCHEDULER:
            case ENVOY_EDITOR_SCHEDULER_EDIT:
                plugin.getEnvoyGuiManager().getEnvoySchedulerGui().open(player,envoy);
                break;
            default:
                plugin.getEnvoyGuiManager().getEnvoyHomeGuiManager().open(player);
                break;
        }
    }
}
