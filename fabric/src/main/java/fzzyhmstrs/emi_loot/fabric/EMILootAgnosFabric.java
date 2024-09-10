package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILootAgnos;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class EMILootAgnosFabric extends EMILootAgnos {
    static {
        EMILootAgnos.delegate = new EMILootAgnosFabric();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected String getModNameAgnos(String namespace) {
        if (namespace.equals("c")) {
            return "Common";
        }
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(namespace);
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }
        container = FabricLoader.getInstance().getModContainer(namespace.replace('_', '-'));
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }
        return WordUtils.capitalizeFully(namespace.replace('_', ' '));
    }

    @Override
    protected boolean isDevelopmentEnvironmentAgnos() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    protected boolean isModLoadedAgnos(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    protected LootTable loadLootTableAgnos(Identifier id, LootTable lootTable) {
        return lootTable;
    }
}
