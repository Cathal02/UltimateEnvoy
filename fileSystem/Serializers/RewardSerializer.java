package org.cathal.ultimateEnvoy.fileSystem.Serializers;

import com.google.gson.*;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;

import java.lang.reflect.Type;

public class RewardSerializer implements JsonSerializer<CrateReward> {
    @Override
    public JsonElement serialize(CrateReward src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", BukkitSerializer.itemToBase64(src.getRewardItem()));
        obj.addProperty("chance", src.getChance());
        obj.addProperty("id", src.getId().toString());


        return obj;
    }
}
