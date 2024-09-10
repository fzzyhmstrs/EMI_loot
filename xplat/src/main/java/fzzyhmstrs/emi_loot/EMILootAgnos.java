package fzzyhmstrs.emi_loot;

import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

public abstract class EMILootAgnos {
    public static EMILootAgnos delegate;

    static {
        try {
            Class.forName("fzzyhmstrs.emi_loot.fabric.EMILootAgnosFabric");
        } catch (Throwable t) {
        }
        try {
            Class.forName("fzzyhmstrs.emi_loot.forge.EMILootAgnosNeoForge");
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
}
