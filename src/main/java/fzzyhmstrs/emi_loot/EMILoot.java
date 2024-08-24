package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.server.condition.BlownUpByCreeperLootCondition;
import fzzyhmstrs.emi_loot.server.condition.KilledByWitherLootCondition;
import fzzyhmstrs.emi_loot.server.condition.MobSpawnedWithLootCondition;
import fzzyhmstrs.emi_loot.server.function.OminousBannerLootFunction;
import fzzyhmstrs.emi_loot.server.function.SetAnyDamageLootFunction;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.annotations.NonSync;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresRestart;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.util.FcText;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedChoice;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class EMILoot implements ModInitializer {

    public static String MOD_ID = "emi_loot";
    public static final Logger LOGGER = LoggerFactory.getLogger("emi_loot");
    public static Random emiLootRandom = new LocalRandom(System.currentTimeMillis());
    public static LootTableParser parser = new LootTableParser();
    public static EmiLootConfig config = ConfigApi.registerAndLoadConfig(EmiLootConfig::new);
    public static boolean DEBUG = config.debugMode;

    //conditions & functions will be used in Lootify also, copying the identifier here so both mods can serialize the same conditions separately
    public static LootConditionType WITHER_KILL = LootConditionTypes.register("lootify:wither_kill", new KilledByWitherLootCondition.Serializer());
    public static LootConditionType SPAWNS_WITH = LootConditionTypes.register("lootify:spawns_with", new MobSpawnedWithLootCondition.Serializer());
    public static LootConditionType CREEPER = LootConditionTypes.register("lootify:creeper", new BlownUpByCreeperLootCondition.Serializer());
    public static LootFunctionType SET_ANY_DAMAGE = LootFunctionTypes.register("lootify:set_any_damage", new SetAnyDamageLootFunction.Serializer());
    public static LootFunctionType OMINOUS_BANNER = LootFunctionTypes.register("lootify:ominous_banner", new OminousBannerLootFunction.Serializer());

    public static Enchantment RANDOM = new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.TRIDENT, EquipmentSlot.values()) {
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
        //Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "random"), RANDOM);
    }

    @ConvertFrom(fileName = "EmiLootConfig_v1.json")
    public static class EmiLootConfig extends Config {

        EmiLootConfig() {
            super(new Identifier(MOD_ID, "emi_loot_config"));
        }

        @RequiresRestart
        public boolean debugMode = false;

        @RequiresRestart
        public boolean parseChestLoot = true;

        @RequiresRestart
        public boolean parseBlockLoot = true;

        @RequiresRestart
        public boolean parseMobLoot = true;

        @RequiresRestart
        public boolean parseGameplayLoot = true;

        @RequiresRestart
        public boolean parseArchaeologyLoot = true;

        @NonSync
        public boolean chestLootCompact = true;

        @NonSync
        public boolean chestLootAlwaysStackSame = false;

        @NonSync
        public boolean mobLootIncludeDirectDrops = true;

        @NonSync
        public ValidatedChoice<String> conditionStyle = FabricLoader.getInstance().isModLoaded("symbols_n_stuff")
                                                            ?
                                                        ValidatedList.ofString("default", "tooltip").toChoices(ValidatedChoice.WidgetType.POPUP, (t, u) -> FcText.INSTANCE.translate(u + "." + t), (t, u) -> FcText.INSTANCE.translate(u + "." + t))
                                                            :
                                                        new ValidatedChoice<>(List.of("default", "install_sns"), ValidatedString.fromList(List.of("default")), (t, u) -> FcText.INSTANCE.translate(u + "." + t), (t, u) -> FcText.INSTANCE.translate(u + "." + t));
	}
}