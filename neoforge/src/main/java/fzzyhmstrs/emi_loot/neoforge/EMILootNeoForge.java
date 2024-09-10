package fzzyhmstrs.emi_loot.neoforge;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.neoforge.events.EMILootClientForgeEvents;
import fzzyhmstrs.emi_loot.neoforge.events.EMILootClientModEvents;
import fzzyhmstrs.emi_loot.neoforge.events.EMILootForgeEvents;
import fzzyhmstrs.emi_loot.neoforge.util.BlockRendererImpl;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(EMILoot.MOD_ID)
public class EMILootNeoForge {
    public static final DeferredRegister<LootConditionType> LOOT_CONDITION_TYPES = DeferredRegister.create(RegistryKeys.LOOT_CONDITION_TYPE, "lootify");
    public static final DeferredRegister<LootFunctionType> LOOT_FUNCTION_TYPES = DeferredRegister.create(RegistryKeys.LOOT_FUNCTION_TYPE, "lootify");
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(RegistryKeys.ENCHANTMENT, EMILoot.MOD_ID);

    static {
        EMILoot.WITHER_KILL = LOOT_CONDITION_TYPES.register("wither_kill", () -> new LootConditionType(KilledByWitherLootCondition.CODEC));
        EMILoot.SPAWNS_WITH = LOOT_CONDITION_TYPES.register("spawns_with", () -> new LootConditionType(MobSpawnedWithLootCondition.CODEC));
        EMILoot.CREEPER = LOOT_CONDITION_TYPES.register("creeper", () -> new LootConditionType(BlownUpByCreeperLootCondition.CODEC));
        EMILoot.SET_ANY_DAMAGE = LOOT_FUNCTION_TYPES.register("set_any_damage", () -> new LootFunctionType(SetAnyDamageLootFunction.CODEC));
        EMILoot.OMINOUS_BANNER = LOOT_FUNCTION_TYPES.register("ominous_banner", () -> new LootFunctionType(OminousBannerLootFunction.CODEC));

        EMILoot.RANDOM = ENCHANTMENTS.register("random", () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values()) {
            @Override
            public boolean isAvailableForEnchantedBookOffer() {
                return false;
            }

            @Override
            public boolean isAvailableForRandomSelection() {
                return false;
            }
        });
    }

    public EMILootNeoForge(IEventBus modBus) {
        //EventBuses.registerModEventBus(EMILoot.MOD_ID, modBus);

        LOOT_CONDITION_TYPES.register(modBus);
        LOOT_FUNCTION_TYPES.register(modBus);

        ENCHANTMENTS.register(modBus);

        NeoForge.EVENT_BUS.register(new EMILootForgeEvents());
        //MOD_BUS.register(new EMILootModEvents());

        if (FMLLoader.getDist().isClient()) {
            NeoForge.EVENT_BUS.addListener(BlockRendererImpl::onClientTick);
            NeoForge.EVENT_BUS.register(new EMILootClientForgeEvents());
            modBus.register(new EMILootClientModEvents());
        }

        EMILoot.onInitialize();
    }
}
