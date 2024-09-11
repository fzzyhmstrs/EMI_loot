package fzzyhmstrs.emi_loot.forge;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class EMILootAgnosForge extends EMILootAgnos {
    static {
        EMILootAgnos.delegate = new EMILootAgnosForge();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected String getModNameAgnos(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<? extends ModContainer> container = ModList.get().getModContainerById(namespace);
        if (container.isPresent()) {
            return container.get().getModInfo().getDisplayName();
        }
        container = ModList.get().getModContainerById(namespace.replace('_', '-'));
        if (container.isPresent()) {
            return container.get().getModInfo().getDisplayName();
        }
        return WordUtils.capitalizeFully(namespace.replace('_', ' '));
    }

    @Override
    protected boolean isDevelopmentEnvironmentAgnos() {
        return !FMLLoader.isProduction();
    }

    @Override
    protected boolean isModLoadedAgnos(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    protected LootTable loadLootTableAgnos(Gson gson, Identifier id, JsonObject json) {
        return ForgeHooks.loadLootTable(gson, id, json, true);
    }
}
