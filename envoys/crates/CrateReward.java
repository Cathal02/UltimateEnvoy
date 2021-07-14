package org.cathal.ultimateEnvoy.envoys.crates;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class CrateReward {
    ItemStack reward;
    double chance;
    List<String> commandsToExecute;
    UUID id;

    public CrateReward(ItemStack reward, double chance, List<String> commandsToExecute){
        this.reward =reward;
        this.chance = chance;
        this.commandsToExecute =commandsToExecute;
        id = UUID.randomUUID();
    }

    public CrateReward(ItemStack reward, double chance, UUID id,List<String> commandsToExecute){
        this.reward =reward;
        this.chance = chance;
        this.commandsToExecute =commandsToExecute;
        this.id = id;
    }

    public UUID getId(){
        return id;
    }
    public ItemStack getRewardItem() {return reward;}
    public List<String> getCommandsToExecute(){return commandsToExecute;}
    public Double getChance(){return chance;}

    public void changeChance(double chance) {
        this.chance += chance;
        if(this.chance < 0){
            this.chance = 0;
        }
    }

    public void setChance(double chance){
        this.chance = chance;
    }
}
