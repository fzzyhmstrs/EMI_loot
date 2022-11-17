package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.server.LootTableParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

import net.minecraft.util.registry.Registry;

import java.util.Random;

public class EMILoot implements ModInitializer {

    public static String MOD_ID = "emi_loot";
    public static Random emiLootRandom = new Random(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    public static Enchantment RANDOM = new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values()){
        @Override
        public boolean isAvailableForEnchantedBookOffer() {
            return false;
        }

        public boolean isAvailableForRandomSelection() {
            return false;
        }
    };

    @Override
    public void onInitialize() {
        parser.registerServer();
        Registry.register(Registry.ENCHANTMENT,new Identifier(MOD_ID,"random"),RANDOM);
    }
}
