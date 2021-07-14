package org.cathal.ultimateEnvoy.fileSystem.Serializers;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.crates.CrateOpenMethod;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrateDeserializer implements JsonDeserializer<Crate> {

    UltimateEnvoy plugin;
    public CrateDeserializer(UltimateEnvoy plugin){
        this.plugin = plugin;
    }

    @Override
    public Crate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String crateName = obj.get("crateName").getAsString();
        String requiredPermission = obj.get("requiredPermission").getAsString();
        String requiredKey = obj.get("requiredKey").getAsString();

        int minRewardAmount = obj.get("minRewardAmount").getAsInt();
        int maxRewardAmount = obj.get("maxRewardAmount").getAsInt();
        double requiredBalance = obj.get("requiredBalance").getAsDouble();
        int id = obj.get("id").getAsInt();
        CrateOpenMethod openMethod = CrateOpenMethod.valueOf(obj.get("openMethod").getAsString());
        ItemStack item = null;
        try {
            item = BukkitSerializer.base64ToItemStack(obj.get("crateItem").getAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = new Gson().fromJson(obj.get("crateItem").getAsJsonObject(), Map.class);
//        if(map != null){
//            item = ItemStack.deserialize(map);
//        }

        List<CrateReward> rewards = new ArrayList<>();
        for(JsonElement i : obj.get("crateRewardIds").getAsJsonArray()){
            rewards.add(plugin.getRewardManager().getCrateReward(UUID.fromString(i.getAsString())));
        }
        return new Crate(crateName,requiredPermission,requiredKey,minRewardAmount,maxRewardAmount,requiredBalance,item,rewards,openMethod,id);
    }
}
