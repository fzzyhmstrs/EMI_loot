package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.function.LootFunctionTypes;

public class EMILootFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EMILoot.WITHER_KILL = () -> LootConditionTypes.register("lootify:wither_kill", new KilledByWitherLootCondition.Serializer());
        EMILoot.SPAWNS_WITH = () -> LootConditionTypes.register("lootify:spawns_with", new MobSpawnedWithLootCondition.Serializer());
        EMILoot.CREEPER = () -> LootConditionTypes.register("lootify:creeper", new BlownUpByCreeperLootCondition.Serializer());
        EMILoot.SET_ANY_DAMAGE = () -> LootFunctionTypes.register("lootify:set_any_damage", new SetAnyDamageLootFunction.Serializer());
        EMILoot.OMINOUS_BANNER = () -> LootFunctionTypes.register("lootify:ominous_banner", new OminousBannerLootFunction.Serializer());

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->{
            EMILoot.parser.registerServer(player);
        });

        EMILoot.init();
    }
}
