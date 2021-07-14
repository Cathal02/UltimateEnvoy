package org.cathal.ultimateEnvoy.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.lang.reflect.InvocationTargetException;

public class BlockPlacer {

    private static UltimateEnvoy plugin;
    public BlockPlacer(UltimateEnvoy plugin){
        this.plugin = plugin;
    }

    public static void simulateBlockPlace(Player player, Location loc, Material material){
        if(player == null || loc == null || material == null )return;

        PacketContainer blockPlace = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
        blockPlace.getModifier().writeDefaults();
        WrappedBlockData blockData = WrappedBlockData.createData(material);
        blockPlace.getBlockData().write(0, blockData);
        blockPlace.getBlockPositionModifier().write(0, new BlockPosition(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()));

        new BukkitRunnable(){

            @Override
            public void run() {
                try {
                    plugin.getPacketManager().sendServerPacket(player, blockPlace);
                } catch (InvocationTargetException err) {
                    throw new RuntimeException(
                            "Cannot send packet " + blockPlace, err);
                }
            }
        }.runTaskLater(plugin,1L);
    }
}
