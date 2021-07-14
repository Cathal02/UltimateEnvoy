package org.cathal.ultimateEnvoy.envoys.crates;

import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RewardManager {

    private List<CrateReward> rewards = new ArrayList<>();

    public RewardManager(UltimateEnvoy plugin){
        rewards = plugin.getRewardDataManager().getCrateRewards();
    }

    public void createNewReward(Crate crate, ItemStack currentItem){
        CrateReward reward = new CrateReward(currentItem,10,null);
        rewards.add(reward);

        crate.addReward(reward);
    }

    public List<CrateReward> getRewards(){
        return rewards;
    }

    public CrateReward getCrateReward(UUID id){
        Optional<CrateReward> reward= rewards.stream().filter(crateReward -> crateReward.getId().equals(id)).findFirst();
        return reward.orElse(null);
    }

    public List<CrateReward> idsToCrateRewards(){
        return null;
    }


}
