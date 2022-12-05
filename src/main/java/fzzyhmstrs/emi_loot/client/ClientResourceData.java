package fzzyhmstrs.emi_loot.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fzzyhmstrs.emi_loot.EMILoot;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class ClientResourceData {

    public static final Object2IntMap<EntityType<?>> MOB_OFFSETS = new Object2IntOpenHashMap<>();
    public static final Object2FloatMap<EntityType<?>> MOB_SCALES = new Object2FloatOpenHashMap<>();
    public static final Map<EntityType<?>, Vec3f> MOB_ROTATIONS = new HashMap<>();

    public static void register(){
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EntityOffsetsReloadListener());
    }


    private static class EntityOffsetsReloadListener implements SimpleSynchronousResourceReloadListener{

        @Override
        public Identifier getFabricId() {
            return new Identifier(EMILoot.MOD_ID,"client_loot_resources");
        }

        @Override
        public void reload(ResourceManager manager) {
            MOB_OFFSETS.clear();
            MOB_SCALES.clear();
            MOB_ROTATIONS.clear();
            manager.findResources("entity_fixers",path -> path.getPath().endsWith(".json")).forEach(this::load);
            if (EMILoot.DEBUG) EMILoot.LOGGER.info(MOB_OFFSETS.toString());
            if (EMILoot.DEBUG) EMILoot.LOGGER.info(MOB_ROTATIONS.toString());
            if (EMILoot.DEBUG) EMILoot.LOGGER.info(MOB_SCALES.toString());
        }

        private void load(Identifier fileId, Resource resource){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Reading entity fixers from file: " + fileId.toString());
            try {
                BufferedReader reader = resource.getReader();
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                json.entrySet().forEach((entry)->{
                    JsonElement element = entry.getValue();
                    Identifier mobId = new Identifier(entry.getKey());
                    if (Registry.ENTITY_TYPE.containsId(mobId)) {
                        if (element.isJsonObject()) {
                            JsonObject object = element.getAsJsonObject();
                            JsonElement offset = object.get("offset");
                            if (offset != null && offset.isJsonPrimitive()) {

                                MOB_OFFSETS.put(Registry.ENTITY_TYPE.get(mobId), offset.getAsInt());

                            }
                            JsonElement scaling = object.get("scale");
                            if (scaling != null && scaling.isJsonPrimitive()) {
                                    MOB_SCALES.put(Registry.ENTITY_TYPE.get(mobId), scaling.getAsFloat());
                            }
                            float x = 0f;
                            float y = 0f;
                            float z = 0f;
                            JsonElement xEl = object.get("x");
                            if (xEl != null && xEl.isJsonPrimitive()) {
                                x = xEl.getAsFloat();
                            }
                            JsonElement yEl = object.get("y");
                            if (yEl != null && yEl.isJsonPrimitive()) {
                                y = yEl.getAsFloat();
                            }
                            JsonElement zEl = object.get("z");
                            if (zEl != null && zEl.isJsonPrimitive()) {
                                z = zEl.getAsFloat();
                            }
                            if (x != 0f || y != 0f || z != 0f) {
                                MOB_ROTATIONS.put(Registry.ENTITY_TYPE.get(mobId), new Vec3f(x, y, z));
                            }

                        } else {
                            throw new IllegalArgumentException("Element in mobfixer " + fileId + "not properly formatted");
                        }
                    }
                });
            } catch (Exception e){
                EMILoot.LOGGER.severe("Failed to open or read Entity Offsets file: " + fileId);
            }
        }
    }

}
