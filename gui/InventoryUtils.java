package org.cathal.ultimateEnvoy.gui;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.utils.XMaterial;

public  class InventoryUtils {


    public static void fill(Inventory inventory){
        ItemStack itemStack = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(" ");
        itemStack.setItemMeta(meta);

       for(int i = 0; i< inventory.getSize(); i++){
           if (inventory.getItem(i) == null){

               inventory.setItem(i, itemStack);
           }
           if(inventory.getItem(i).getType() == XMaterial.AIR.parseMaterial()){
               inventory.setItem(i, itemStack);
           }
       }
    }

    public static void fill(Inventory inventory, ItemStack item){
        for(int i = 0; i< inventory.getSize(); i++){
            if (inventory.getItem(i) == null){

                inventory.setItem(i, item);
            }
            if(inventory.getItem(i).getType() == XMaterial.AIR.parseMaterial()){
                inventory.setItem(i, item);
            }
        }
    }

    public static void fillPlayerHotbar(Inventory inventory){
        for(int i = 0; i < 9; i++){
            if(inventory.getItem(i)!=null)return;
            inventory.setItem(i, new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()).setName(" ").toItemStack());
        }
    }

    public static int roundUpToInvSizeWithHotbar(int size){
        int x = roundUpToInvSize(size);
        if(x == 54)return x;
        return x+9;
    }

    public static void addBackHotbar(Inventory inventory){
        int size = inventory.getSize();
        for(int i = size-1; i > size-10; i--){
            inventory.setItem(i,new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).setName(" ").toItemStack());
        }
        inventory.setItem(size-9, new ItemBuilder(XMaterial.ARROW.parseMaterial()).setName(ChatColor.GREEN + " " + ChatColor.BOLD + "BACK").toItemStack());

    }


    public static int roundUpToInvSize(int size){
        if(size <=9){
            return 9;
        } else if(size <= 18){
            return 18;
        } else if(size <= 27){
            return 27;
        } else if(size <= 36){
            return 36;
        } else if(size <= 45){
            return 45;
        } else
        {
            return 54;
        }
    }
}
