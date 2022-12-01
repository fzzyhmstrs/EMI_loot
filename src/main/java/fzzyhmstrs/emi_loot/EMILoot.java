package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

import java.io.*;
import java.util.Arrays;

public class EMILoot implements ModInitializer {

    public static boolean DEBUG = false;
    public static String MOD_ID = "emi_loot";
    public static Random emiLootRandom = new LocalRandom(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static EmiLootConfig config = readOrCreate();
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
    
    private static EmiLootConfig readOrCreate(){
        File dir = FabricLoader.getInstance().getConfigDir().toFile();
        
        if (!dir.exists() && !dir.mkdirs()) {
            System.out.println("EMI Loot could not find or create config directory, using default configs");
            return new EmiLootConfig();
        }
        
        File f = new File(dir,"EmiLootConfig.json");
        
        try{
            if (f.exists()){
                return gson.fromJson(new InputStreamReader(new FileInputStream(f)),EmiLootConfig.class);
            } else if (!f.createNewFile()){
                throw new UnsupportedOperationException("couldn't generate config file");
            } else {
                FileWriter fw = new FileWriter(f);
                fw.write(gson.toJson(new EmiLootConfig()));
            }
        } catch(Exception e){
            System.out.println("Emi Loot failed to create or read it's config file!");
            System.out.println(Arrays.toString(e.getStackTrace()));
            return new EmiLootConfig();
        }
        return new EmiLootConfig();
    }
    
    public static class EmiLootConfig{
        public boolean parseChestLoot = true;
        
        public boolean parseBlockLoot = true;
        
        public boolean parseMobLoot = true;
    
        public boolean parseGameplayLoot = true;
    }
}
