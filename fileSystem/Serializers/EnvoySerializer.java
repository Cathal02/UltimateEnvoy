package org.cathal.ultimateEnvoy.fileSystem.Serializers;

import com.google.gson.*;
import org.bukkit.Location;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.EnvoyDate;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnvoySerializer implements JsonSerializer<Envoy> {
    @Override
    public JsonElement serialize(Envoy src, Type typeOfSrc, JsonSerializationContext context) {
        Gson gson = new Gson();

        JsonObject obj = new JsonObject();
        obj.addProperty("envoyName", src.getName());
        obj.addProperty("id", src.getId());
        if(src.getEdgeOne() != null){
            obj.add("edgeOne", locationToJsonObject(src.getEdgeOne()));
        }

        if(src.getEdgeTwo() != null){
            obj.add("edgeTwo", locationToJsonObject(src.getEdgeTwo()));
        }

        List<JsonObject> locObjs = new ArrayList<>();
        for(Location loc : src.getCrateSpawnLocations()){
            locObjs.add(locationToJsonObject(loc));
        }

        List<JsonObject> envoyDates = new ArrayList<>();
        for(EnvoyDate date : src.getEnvoyDates()){
            envoyDates.add(envoyDateToJsonObject(date));
        }
        JsonArray locationArray = gson.toJsonTree(locObjs).getAsJsonArray();
        JsonArray dateArray =gson.toJsonTree(envoyDates).getAsJsonArray();

        obj.addProperty("crates",gson.toJson(src.getCrateMap()));
        obj.add("crateSpawnLocations", locationArray);
        obj.add("envoyDates",dateArray);
        obj.addProperty("usingRandomSpawnLocations", src.getIsEnvoyUsingRandomCrateSpawnPositions());
        obj.addProperty("playersRequiredToStart", src.getPlayersRequiredToStartEnvoy());
        obj.addProperty("envoyDuration", src.getEnvoyDuration());
        obj.addProperty("minCrates",src.getMinCrates());
        obj.addProperty("maxCrates",src.getMaxCrates());
        obj.addProperty("enableFallingCrates",src.isEnableFallingCrates());
        obj.addProperty("refillMode", src.getRefillMode().toString());
        return obj;

    }


    JsonObject locationToJsonObject(Location loc){
        JsonObject location = new JsonObject();
        location.addProperty("x",loc.getBlockX());
        location.addProperty("y",loc.getBlockY());
        location.addProperty("z",loc.getBlockZ());
        location.addProperty("world",loc.getWorld().getName());

        return location;
    }

    JsonObject envoyDateToJsonObject(EnvoyDate envoyDate){
        JsonObject date = new JsonObject();
        date.add("days",new Gson().toJsonTree(envoyDate.getDays()).getAsJsonArray());
        date.addProperty("hour",envoyDate.getHour());
        date.addProperty("minute",envoyDate.getMinute());
        return date;
    }

}

