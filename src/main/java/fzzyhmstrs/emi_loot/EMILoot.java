package fzzyhmstrs.emi_loot;

import fzzyhmstrs.emi_loot.server.LootTableParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EMILoot implements ModInitializer {

    public static String MOD_ID = "emi_loot";
    public static LootTableParser parser = new LootTableParser();
    public static Enchantment RANDOM = new Enchantment(Enchantment.Rarity.COMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values()){};

    @Override
    public void onInitialize() {
        parser.registerServer();
        Registry.register(Registry.ENCHANTMENT,new Identifier(MOD_ID,"random"),RANDOM);
    }
}