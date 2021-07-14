package org.cathal.ultimateEnvoy.fileSystem.Serializers;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

public class RewardDeserializer implements JsonDeserializer<CrateReward> {
    @Override
    public CrateReward deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

//        final Map<String, Object> map = new Gson().fromJson(obj.get("item").getAsJsonObject(), Map.class);
        ItemStack item = new ItemStack(Material.IRON_AXE);
//        if(map != null){
//            item = ItemStack.deserialize(map);
//        }
        try {
            item = BukkitSerializer.base64ToItemStack(obj.get("item").getAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        double chance = obj.get("chance").getAsDouble();
        UUID id = UUID.fromString(obj.get("id").getAsString());

        return new CrateReward(item,chance,id,null);
    }
}
