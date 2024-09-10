package fzzyhmstrs.emi_loot.fabric;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EMILootFabric implements ModInitializer {
    private static final LootConditionType WITHER_KILL = Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier("lootify", "wither_kill"), new LootConditionType(KilledByWitherLootCondition.CODEC));
    private static final LootConditionType SPAWNS_WITH = Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier("lootify", "spawns_with"), new LootConditionType(MobSpawnedWithLootCondition.CODEC));
    private static final LootConditionType CREEPER = Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier("lootify", "creeper"), new LootConditionType(BlownUpByCreeperLootCondition.CODEC));
    private static final LootFunctionType SET_ANY_DAMAGE = Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier("lootify", "set_any_damage"), new LootFunctionType(SetAnyDamageLootFunction.CODEC));
    private static final LootFunctionType OMINOUS_BANNER = Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier("lootify", "ominous_banner"), new LootFunctionType(OminousBannerLootFunction.CODEC));

    private static final Enchantment RANDOM = Registry.register(Registries.ENCHANTMENT, new Identifier(EMILoot.MOD_ID, "random"), new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values()) {
        @Override
        public boolean isAvailableForEnchantedBookOffer() {
            return false;
        }

        @Override
        public boolean isAvailableForRandomSelection() {
            return false;
        }
    });

    @Override
    public void onInitialize() {
        EMILoot.WITHER_KILL = () -> WITHER_KILL;
        EMILoot.SPAWNS_WITH = () -> SPAWNS_WITH;
        EMILoot.CREEPER = () -> CREEPER;
        EMILoot.SET_ANY_DAMAGE = () -> SET_ANY_DAMAGE;
        EMILoot.OMINOUS_BANNER = () -> OMINOUS_BANNER;

        EMILoot.RANDOM = () -> RANDOM;

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->{
            EMILoot.parser.registerServer(player);
        });

        EMILoot.onInitialize();
    }
}
