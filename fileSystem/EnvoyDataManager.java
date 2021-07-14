package org.cathal.ultimateEnvoy.fileSystem;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.EnvoyManager;
import org.cathal.ultimateEnvoy.fileSystem.Serializers.EnvoyDeserializer;
import org.cathal.ultimateEnvoy.fileSystem.Serializers.EnvoySerializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EnvoyDataManager {

    private final UltimateEnvoy plugin;
    private final EnvoyManager envoyManager;
    private File file;

    public EnvoyDataManager(UltimateEnvoy plugin, EnvoyManager envoyManager) {
        this.plugin = plugin;
        this.envoyManager = envoyManager;
        file = new File(plugin.getDataFolder().getPath() + File.separator + "envoys.json");
        if (!file.exists()) plugin.saveResource(file.getName(), false);
    }

    public void saveEnvoys(Collection<Envoy> values) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Envoy.class, new EnvoySerializer()).setPrettyPrinting().create();
        List<Envoy> data = new ArrayList<>(values);
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


    public ArrayList<Envoy> getEnvoyData() {
        try {
            JsonArray arr = new JsonArray();

            JsonReader reader = new JsonReader(new FileReader(file));
            JsonParser parser = new JsonParser();
            JsonElement parsedElement = parser.parse(reader);
            if (parsedElement.isJsonArray()) {
                arr = parsedElement.getAsJsonArray();
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(Envoy.class, new EnvoyDeserializer(envoyManager)).create();
            reader.close();

            return new ArrayList<Envoy>(Arrays.asList(gson.fromJson(arr, Envoy[].class)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
