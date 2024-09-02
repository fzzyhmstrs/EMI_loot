package fzzyhmstrs.emi_loot.forge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Optional;

public class EMILootExpectPlatformImpl {
    public static String getModName(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<? extends ModContainer> container = ModList.get().getModContainerById(namespace);
        if (container.isPresent()) {
            return container.get().getModInfo().getDisplayName();
        }
        return namespace;
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static LootTable loadLootTable(Gson gson, Identifier id, JsonElement json) {
        return ForgeHooks.loadLootTable(gson, id, json, true);
    }
}
