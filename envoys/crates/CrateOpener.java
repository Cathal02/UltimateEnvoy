package org.cathal.ultimateEnvoy.envoys.crates;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.events.CrateOpenEvent;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.utils.XSound;
import org.cathal.ultimateEnvoy.VaultManager;
import org.cathal.ultimateEnvoy.fileSystem.Language;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;

public class CrateOpener {

    UltimateEnvoy plugin;
    public CrateOpener(UltimateEnvoy plugin){
        this.plugin = plugin;
    }

    public OpenCrateStatus openCrate(Crate crate, Player player, Location loc) {

            CrateReward[] rewards = crate.generateRandomRewards();
            OpenCrateStatus status = meetsRequirementsToOpenCrate(player,crate);
            if(status != OpenCrateStatus.SUCCESS){
                handleCrateFailOpen(status,player,crate);
            }

            Bukkit.getPluginManager().callEvent(new CrateOpenEvent(crate,player,loc,plugin.getEnvoyManager().getEnvoySpawner().getEnvoyFromCrateSpawnLocation(loc)));

                Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();

                fwm.setPower(2);
                fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

                fw.setFireworkMeta(fwm);

            switch (crate.getCrateOpenMethod()) {
                case OPEN_CHEST:
                    Inventory inv = Bukkit.createInventory(null, InventoryUtils.roundUpToInvSize(rewards.length), "Rewards");
                    for (int i = 0; i < rewards.length; i++) {
                        inv.setItem(i, rewards[i].getRewardItem());
                    }

                    player.openInventory(inv);
                    break;

                case DROP_ON_FLOOR:
                    for(int i = 0; i < rewards.length; i++){
                        loc.getWorld().dropItem(loc,rewards[i].getRewardItem());
                    }

                case INSERT_TO_INVENTORY:
                    for(int i = 0; i < rewards.length; i++){
                        if(player.getInventory().firstEmpty() == -1){
                            player.sendMessage(Language.getConfigString("inventory_full",true));
                            loc.getWorld().dropItem(loc,rewards[i].getRewardItem());
                        }else{
                            player.sendMessage(Language.getConfigString("item_received", true).replaceAll("\\{item\\}", rewards[i].getRewardItem().getType().name()));
                            player.getInventory().addItem(rewards[i].getRewardItem());
                        }
                    }

                default:
                    break;
            }

            XSound.play(player, "ANVIL_BREAK");

            return status;
        }

    private OpenCrateStatus meetsRequirementsToOpenCrate(Player player, Crate crate) {
        if(!crate.getRequiredPermission().equalsIgnoreCase("none")){
            if(!player.hasPermission(crate.getRequiredPermission()) && !player.isOp()) return OpenCrateStatus.NO_PERMISSION;
        }

        if(VaultManager.getEconomy().getBalance(player) < crate.getRequiredBalance()){
            return OpenCrateStatus.NO_BALANCE;
        }

        return OpenCrateStatus.SUCCESS;
    }

    private void handleCrateFailOpen(OpenCrateStatus status, Player player, Crate crate){
        if(status == OpenCrateStatus.NO_BALANCE){
           player.sendMessage(Language.translateBalanceMessage(Language.getString("notEnoughMoney",false),crate.getRequiredBalance()));
        } else if(status == OpenCrateStatus.NO_PERMISSION){
            player.sendMessage(Language.getString("noPermissionToOpenCrate",true));
        }
    }
}
