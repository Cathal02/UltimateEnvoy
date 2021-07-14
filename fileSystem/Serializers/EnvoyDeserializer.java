package org.cathal.ultimateEnvoy.fileSystem.Serializers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.EnvoyDate;
import org.cathal.ultimateEnvoy.envoys.EnvoyManager;
import org.cathal.ultimateEnvoy.gui.managers.envoys.EnvoyRefillMode;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvoyDeserializer implements JsonDeserializer<Envoy> {

    EnvoyManager envoyManager;

    public EnvoyDeserializer(EnvoyManager envoyManager){
        this.envoyManager = envoyManager;
    }
    @Override
    public Envoy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        JsonObject obj = json.getAsJsonObject();
        Location edgeOne = null;
        Location edgeTwo = null;
        if(obj.has("edgeOne")){
            edgeOne = getLocationFromObj(obj.get("edgeOne").getAsJsonObject());
        }

        if(obj.has("edgeTwo")){
            edgeTwo = getLocationFromObj(obj.get("edgeTwo").getAsJsonObject());
        }

        Map<Integer,Double> crateChanceMap;
        Type type = new TypeToken<HashMap<Integer,Double>>(){}.getType();
        crateChanceMap = gson.fromJson(obj.get("crates").getAsString(),type);


        JsonArray locationJsonArray = obj.get("crateSpawnLocations").getAsJsonArray();
        List<Location> crateSpawnLocations = new ArrayList<>();


        for(int i = 0; i < locationJsonArray.size(); i++){
            crateSpawnLocations.add(getLocationFromObj(locationJsonArray.get(i).getAsJsonObject()));
        }


        List<EnvoyDate> envoyDates = new ArrayList<>();
        if(obj.has("envoyDates")){
            JsonArray envoyDateArray = obj.get("envoyDates").getAsJsonArray();
            for(int i = 0; i < envoyDateArray.size(); i++){
                envoyDates.add(getEnvoyDateFromObj(envoyDateArray.get(i).getAsJsonObject()));
            }
        }


        boolean usingRandomSpawnLocation = obj.has("usingRandomSpawnLocations") && obj.get("usingRandomSpawnLocations"
        ).getAsBoolean();

        int playersRequiredToStart = 0;
        if(obj.has("playersRequiredToStart")){
            playersRequiredToStart = obj.get("playersRequiredToStart").getAsInt();
        }
        int envoyDuration = 60*5;
        if(obj.has("envoyDuration")){
            envoyDuration = obj.get("envoyDuration").getAsInt();
        }
        int minCrates = 3;
        int maxCrates = 5;
        if(obj.has("minCrates")){
            minCrates = obj.get("minCrates").getAsInt();
        }

        if(obj.has("maxCrates")){
            maxCrates = obj.get("maxCrates").getAsInt();
        }

        boolean enableFallingCrates = false;
        if(obj.has("enableFallingCrates")){
            enableFallingCrates = obj.get("enableFallingCrates").getAsBoolean();
        }

        EnvoyRefillMode refillMode = EnvoyRefillMode.ALL_CRATES;
        if(obj.has("refillMode")){
            refillMode = EnvoyRefillMode.valueOf(obj.get("refillMode").getAsString());
        }

        return new Envoy(envoyManager,obj.get("envoyName").getAsString(),
                obj.get("id").getAsInt(),
                edgeOne,edgeTwo,crateChanceMap,
                usingRandomSpawnLocation,crateSpawnLocations,envoyDates,
                playersRequiredToStart,envoyDuration,minCrates,maxCrates,enableFallingCrates,refillMode);
    }

    private Location getLocationFromObj(JsonObject obj) {
        if(obj == null) return null;

        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        int z = obj.get("z").getAsInt();
        World world = Bukkit.getWorld(obj.get("world").getAsString());
        return new Location(world,x,y,z);
    }

    private EnvoyDate getEnvoyDateFromObj(JsonObject obj){
        if(obj==null)return null;
        int hour = obj.get("hour").getAsInt();
        int minute = obj.get("minute").getAsInt();

        Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer> days = new Gson().fromJson(obj.get("days").getAsJsonArray(), listType);

        return new EnvoyDate(days,hour,minute);
    }
}
