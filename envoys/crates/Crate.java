package org.cathal.ultimateEnvoy.envoys.crates;

import  org.apache.commons.math3.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.cathal.ultimateEnvoy.VaultManager;
import org.cathal.ultimateEnvoy.fileSystem.Language;

public class Crate {
    private String crateName;
    private String requiredPermission = "none";
    private String requiredKey = "";

    private int minRewardAmount=1;
    private int maxRewardAmount=2;
    private double requiredBalance = 0;
    private int id;

    private List<CrateReward> crateRewards = new ArrayList<>();

    private ItemStack crateItem;
    private CrateOpenMethod crateOpenMethod = CrateOpenMethod.OPEN_CHEST;

    private EnumeratedDistribution enumeratedDistribution;
    public Crate(String crateName, int id) {
        this.crateName =  crateName;
        this.id = id;
        crateItem = XMaterial.CHEST.parseItem();

    }

    public Crate(String crateName, String requiredPermission, String requiredKey, int minRewardAmount, int maxRewardAmount, double requiredBalance, ItemStack item, List<CrateReward> rewards, CrateOpenMethod openMethod,int id){
        this.crateName = crateName;
        this.requiredPermission = requiredPermission;
        this.requiredKey = requiredKey;

        this.requiredBalance = requiredBalance;
        this.minRewardAmount = minRewardAmount;
        this.maxRewardAmount = maxRewardAmount;

        this.id = id;
        this.crateItem = item;
        this.crateOpenMethod = openMethod;
        this.crateRewards = rewards;
        //TODO: Convert reward ids

        updateEnumeratedDistribution();
    }

    private void updateEnumeratedDistribution() {
        final List<Pair<CrateReward, Double>> itemWeights = new ArrayList<>();
        for(CrateReward reward : crateRewards){
            itemWeights.add(new Pair(reward,reward.getChance()));
        }

        enumeratedDistribution = new EnumeratedDistribution(itemWeights);
    }

    public void addReward(CrateReward reward){
        crateRewards.add(reward);
        updateEnumeratedDistribution();
    }

    public ItemStack getCrateItem(){
        if(crateItem == null){
            return new ItemBuilder(XMaterial.BARRIER.parseMaterial()).setName(ChatColor.RED + " " + ChatColor.BOLD + "NO ITEM FOUND").toItemStack();
        }
        return new ItemBuilder(crateItem.getType()).setName(ChatColor.WHITE + Language.translate(crateName)).toItemStack();
    }

    public String getName() {
        return crateName;
    }

    public void setName(String text) {
        crateName = text;
    }

    public CrateOpenMethod getCrateOpenMethod(){
        return crateOpenMethod;
    }

    public void setOpenMethod(CrateOpenMethod openMethod) {
        crateOpenMethod = openMethod;
    }

    public void setRequiredPermission(String permission){
        this.requiredPermission = permission;
    }


    public CrateReward[] getCrateRewards(){
        return crateRewards.toArray(new CrateReward[0]);
    }
    public List<CrateReward> getCrateRewardsAsList(){return crateRewards;}
    public String getRequiredPermission() {
        return requiredPermission;
    }
    public String getRequiredKey() {
        return requiredKey;
    }

    public double getRequiredBalance() {
        return requiredBalance;
    }


    public int getMaxRewardAmount() {
        return maxRewardAmount;
    }

    public int getMinRewardAmount() {
        return minRewardAmount;
    }

    public CrateReward[] generateRandomRewards(){
        int amount = ThreadLocalRandom.current().nextInt(minRewardAmount, maxRewardAmount + 1);

        CrateReward[] rewards = new CrateReward[amount];
        enumeratedDistribution.sample(amount,rewards);
        return rewards;

    }


    public void setCrateItem(Material newItem) {
        crateItem.setType(newItem);
    }

    public int getId(){
        return id;
    }


    public void setBalanceRequired(double balance) {
        this.requiredBalance = balance;
    }

    public void handleCrateOpened(Player player) {
        if(requiredBalance > 0){
            VaultManager.getEconomy().withdrawPlayer(player,requiredBalance);
            player.sendMessage(Language.translateBalanceMessage(Language.getString("moneyWithdrawn",true),requiredBalance));
        }
    }
}
