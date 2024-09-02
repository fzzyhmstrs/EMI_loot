package fzzyhmstrs.emi_loot.forge;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.forge.events.EMILootClientForgeEvents;
import fzzyhmstrs.emi_loot.forge.events.EMILootClientModEvents;
import fzzyhmstrs.emi_loot.forge.events.EMILootForgeEvents;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;

@Mod(EMILoot.MOD_ID)
public class EMILootForge {
    public static final DeferredRegister<LootConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(RegistryKeys.LOOT_CONDITION_TYPE, "lootify");
    public static final DeferredRegister<LootFunctionType> LOOT_FUNCTION_TYPES = DeferredRegister.create(RegistryKeys.LOOT_FUNCTION_TYPE, "lootify");

    static {
        EMILoot.WITHER_KILL = LOOT_CONDITION_TYPES.register("wither_kill", () -> new LootConditionType(new KilledByWitherLootCondition.Serializer()));
        EMILoot.SPAWNS_WITH = LOOT_CONDITION_TYPES.register("spawns_with", () -> new LootConditionType(new MobSpawnedWithLootCondition.Serializer()));
        EMILoot.CREEPER = LOOT_CONDITION_TYPES.register("creeper", () -> new LootConditionType(new BlownUpByCreeperLootCondition.Serializer()));
        EMILoot.SET_ANY_DAMAGE = LOOT_FUNCTION_TYPES.register("set_any_damage", () -> new LootFunctionType(new SetAnyDamageLootFunction.Serializer()));
        EMILoot.OMINOUS_BANNER = LOOT_FUNCTION_TYPES.register("ominous_banner", () -> new LootFunctionType(new OminousBannerLootFunction.Serializer()));
    }

    public EMILootForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        //EventBuses.registerModEventBus(EMILoot.MOD_ID, MOD_BUS);

        LOOT_CONDITION_TYPES.register(MOD_BUS);
        LOOT_FUNCTION_TYPES.register(MOD_BUS);

        MinecraftForge.EVENT_BUS.register(new EMILootForgeEvents());
        //MOD_BUS.register(new EMILootModEvents());

        if (FMLLoader.getDist().isClient()) {
            MinecraftForge.EVENT_BUS.register(new EMILootClientForgeEvents());
            MOD_BUS.register(new EMILootClientModEvents());
        }

        EMILoot.init();
    }
}
