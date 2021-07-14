
package org.cathal.ultimateEnvoy.fileSystem;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;
import org.cathal.ultimateEnvoy.fileSystem.Serializers.RewardDeserializer;
import org.cathal.ultimateEnvoy.fileSystem.Serializers.RewardSerializer;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RewardDataManager {
    UltimateEnvoy plugin;

    File file;

    public RewardDataManager(UltimateEnvoy plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder().getPath() + File.separator + "rewards.json");
        if (!file.exists()) plugin.saveResource(file.getName(), false);
    }

    public void saveCrateRewards(List<CrateReward> rewards){
        Gson gson = new GsonBuilder().registerTypeAdapter(CrateReward.class, new RewardSerializer()).setPrettyPrinting().create();
        List<CrateReward> data = new ArrayList<>(rewards);
        data.toArray();
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.flush();
            gson.toJson(data, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<CrateReward> getCrateRewards(){
        try {
            Gson gson = null;
            JsonArray arr = new JsonArray();

            JsonReader reader = new JsonReader(new FileReader(file));
            JsonParser parser = new JsonParser();
            JsonElement parsedElement = parser.parse(reader);
            if (parsedElement.isJsonArray()) {
                arr = parsedElement.getAsJsonArray();
            }

            gson = new GsonBuilder().registerTypeAdapter(CrateReward.class, new RewardDeserializer()).create();
            reader.close();

            return new ArrayList<CrateReward>(Arrays.asList(gson.fromJson(arr, CrateReward[].class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
