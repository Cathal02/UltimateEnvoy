package org.cathal.ultimateEnvoy.fileSystem.Serializers;

import com.google.gson.*;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

public class CrateSerializer implements JsonSerializer<Crate> {

    @Override
    public JsonElement serialize(Crate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("crateName", src.getName());
        obj.addProperty("requiredPermission", src.getRequiredPermission());
        obj.addProperty("requiredKey", src.getRequiredKey());

        obj.addProperty("minRewardAmount", src.getMinRewardAmount());
        obj.addProperty("maxRewardAmount", src.getMaxRewardAmount());
        obj.addProperty("requiredBalance", src.getRequiredBalance());
        obj.addProperty("openMethod", src.getCrateOpenMethod().toString());
        obj.addProperty("id",src.getId());
        JsonArray jsonArray = new Gson().toJsonTree(src.getCrateRewardsAsList().stream().map(CrateReward::getId).collect(Collectors.toList())).getAsJsonArray();
        obj.add("crateRewardIds", jsonArray);
//        obj.add("crateItem", new Gson().toJsonTree(src.getCrateItem().serialize()));
            obj.addProperty("crateItem", BukkitSerializer.itemToBase64(src.getCrateItem()));
        return obj;
    }
}
