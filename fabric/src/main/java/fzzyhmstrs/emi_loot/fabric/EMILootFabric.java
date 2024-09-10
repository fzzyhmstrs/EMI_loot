package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EMILootFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EMILoot.WITHER_KILL = () -> Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier("lootify", "wither_kill"), new LootConditionType(new KilledByWitherLootCondition.Serializer()));
        EMILoot.SPAWNS_WITH = () -> Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier("lootify", "spawns_with"), new LootConditionType(new MobSpawnedWithLootCondition.Serializer()));
        EMILoot.CREEPER = () -> Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier("lootify", "creeper"), new LootConditionType(new BlownUpByCreeperLootCondition.Serializer()));
        EMILoot.SET_ANY_DAMAGE = () -> Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier("lootify", "set_any_damage"), new LootFunctionType(new SetAnyDamageLootFunction.Serializer()));
        EMILoot.OMINOUS_BANNER = () -> Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier("lootify", "ominous_banner"), new LootFunctionType(new OminousBannerLootFunction.Serializer()));

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->{
            EMILoot.parser.registerServer(player);
        });

        EMILoot.onInitialize();
    }
}
