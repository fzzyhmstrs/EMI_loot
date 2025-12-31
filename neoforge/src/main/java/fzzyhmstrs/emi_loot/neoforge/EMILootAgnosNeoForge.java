package fzzyhmstrs.emi_loot.neoforge;

import fzzyhmstrs.emi_loot.EMILootAgnos;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.loot.LootTable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.EventHooks;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class EMILootAgnosNeoForge extends EMILootAgnos {
    static {
        EMILootAgnos.delegate = new EMILootAgnosNeoForge();
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
    protected LootTable loadLootTableAgnos(Identifier id, LootTable lootTable) {
        return EventHooks.loadLootTable(id, lootTable);
    }

    @Override
    protected void releaseBufferAgnos(PacketByteBuf buf) {
        //do nothing in neo apparently, either it release automatically or something else is happening with ref counts here.
    }

    @Override
    protected SpawnEggItem getAllSpawnEggsAgnos(EntityType<?> type) {
        return SpawnEggItem.forEntity(type);
    }
}