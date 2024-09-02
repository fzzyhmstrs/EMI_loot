package fzzyhmstrs.emi_loot.fabric;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Optional;

public class EMILootExpectPlatformImpl {
    public static String getModName(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(namespace);
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }
        return namespace;
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .toAbsolutePath()
                .normalize();
    }

    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static LootTable loadLootTable(Gson gson, Identifier id, JsonElement json) {
        return gson.fromJson(json, LootTable.class);
    }
}
