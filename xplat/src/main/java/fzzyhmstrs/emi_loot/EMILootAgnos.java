package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class EMILootAgnos {
    public static EMILootAgnos delegate;

    static {
        try {
            Class.forName("fzzyhmstrs.emi_loot.fabric.EMILootAgnosFabric");
        } catch (Throwable t) {
        }
        try {
            Class.forName("fzzyhmstrs.emi_loot.neoforge.EMILootAgnosNeoForge");
        } catch (Throwable t) {
        }
    }

    public static String getModName(String namespace) {
        return delegate.getModNameAgnos(namespace);
    }

    protected abstract String getModNameAgnos(String namespace);

    public static boolean isDevelopmentEnvironment() {
        return delegate.isDevelopmentEnvironmentAgnos();
    }

    protected abstract boolean isDevelopmentEnvironmentAgnos();

    public static boolean isModLoaded(String id) {
        return delegate.isModLoadedAgnos(id);
    }

    protected abstract boolean isModLoadedAgnos(String id);

    public static LootTable loadLootTable(Identifier id, LootTable lootTable) {
        return delegate.loadLootTableAgnos(id, lootTable);
    }

    protected abstract LootTable loadLootTableAgnos(Identifier id, LootTable lootTable);

    public static SpawnEggItem getSpawnEgg(EntityType<?> type) {
        return delegate.getAllSpawnEggsAgnos(type);
    }

    @Nullable
    protected abstract SpawnEggItem getAllSpawnEggsAgnos(EntityType<?> type);
}