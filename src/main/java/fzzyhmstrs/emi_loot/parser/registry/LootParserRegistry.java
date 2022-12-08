package fzzyhmstrs.emi_loot.parser.registry;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.condition.*;
import fzzyhmstrs.emi_loot.parser.function.*;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootParserRegistry{

    private static final Map<LootConditionType, ConditionParser> CONDITION_PARSERS = new HashMap<>();
    private static final Map<LootFunctionType, FunctionParser> FUNCTION_PARSERS = new HashMap<>();

    public static void registerCondition(LootConditionType type, ConditionParser parser, String registrationContext){
        if (!CONDITION_PARSERS.containsKey(type)){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Registering condition for type: " + type + " from: " + registrationContext);
            CONDITION_PARSERS.put(type,parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate condition registration attempted with type: " + type + " during " + registrationContext);
        }
    }
    
    public static void registerFunction(LootFunctionType type, FunctionParser parser, String registrationContext){
        if (!FUNCTION_PARSERS.containsKey(type)){
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Registering function for type: " + type + " from: " + registrationContext);
            FUNCTION_PARSERS.put(type,parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate function registration attempted with type: " + type + " during " + registrationContext);
        }
    }
    
    public static List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, LootConditionType type, ItemStack stack, boolean parentIsAlternative){
        ConditionParser parser = CONDITION_PARSERS.getOrDefault(type,ConditionParser.EMPTY);
        return parser.parseCondition(condition,stack, parentIsAlternative);
    }
    
    public static LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, LootFunctionType type,boolean parentIsAlternative, List<TextKey> conditionTexts){
        FunctionParser parser = FUNCTION_PARSERS.getOrDefault(type,FunctionParser.EMPTY);
        return parser.parseFunction(function,stack,parentIsAlternative, conditionTexts);
    }

    static{

        registerFunction(LootFunctionTypes.APPLY_BONUS,new ApplyBonusFunctionParser(),"Registering vanilla apply bonus function parser");
        registerFunction(LootFunctionTypes.SET_POTION,new SetPotionFunctionParser(),"Registering vanilla set potion function parser");
        registerFunction(LootFunctionTypes.SET_COUNT,new SetCountFunctionParser(),"Registering vanilla set count function parser");
        registerFunction(LootFunctionTypes.ENCHANT_WITH_LEVELS,new EnchantWithLevelsFunctionParser(),"Registering vanilla enchant with levels function parser");
        registerFunction(LootFunctionTypes.ENCHANT_RANDOMLY,new EnchantRandomlyFunctionParser(),"Registering vanilla enchant randomly function parser");
        registerFunction(LootFunctionTypes.SET_ENCHANTMENTS,new SetEnchantmentsFunctionParser(),"Registering vanilla set enchantments function parser");
        registerFunction(LootFunctionTypes.FURNACE_SMELT,new SimpleFunctionParser("emi_loot.function.smelt"),"Registering vanilla furnace smelt function parser");
        registerFunction(LootFunctionTypes.LOOTING_ENCHANT,new SimpleFunctionParser("emi_loot.function.looting"),"Registering vanilla looting function parser");
        registerFunction(LootFunctionTypes.EXPLORATION_MAP,new ExplorationMapFunctionParser(),"Registering vanilla exploration map function parser");
        registerFunction(LootFunctionTypes.SET_NAME,new SetNameFunctionParser(),"Registering vanilla set name function parser");
        registerFunction(LootFunctionTypes.SET_CONTENTS,new SimpleFunctionParser("emi_loot.function.set_contents"),"Registering vanilla set contents function parser");
        registerFunction(LootFunctionTypes.SET_DAMAGE,new SetDamageFunctionParser(),"Registering vanilla set damage function parser");
        registerFunction(LootFunctionTypes.SET_INSTRUMENT,new SetInstrumentFunctionParser(),"Registering vanilla set instrument function parser");
        registerFunction(LootFunctionTypes.COPY_STATE,new SimpleFunctionParser("emi_loot.function.copy_state"),"Registering vanilla copy state function parser");
        registerFunction(LootFunctionTypes.COPY_NAME,new CopyNameFunctionParser(),"Registering vanilla copy name function parser");
        registerFunction(LootFunctionTypes.COPY_NBT,new SimpleFunctionParser("emi_loot.function.copy_nbt"),"Registering vanilla copy nbt function parser");
        registerFunction(LootFunctionTypes.EXPLOSION_DECAY,new ExplosionDecayFunctionParser(),"Registering vanilla explosion decay function parser");
        registerFunction(LootFunctionTypes.FILL_PLAYER_HEAD,new SimpleFunctionParser("emi_loot.function.fill_player_head"),"Registering vanilla fill-player-head function parser");
        registerFunction(LootFunctionTypes.LIMIT_COUNT,new LimitCountFunctionParser(),"Registering vanilla limit-count function parser");
        registerFunction(LootFunctionTypes.SET_ATTRIBUTES,new SetAttributesFunctionParser(),"Registering vanilla set attributes function parser");
        registerFunction(LootFunctionTypes.SET_BANNER_PATTERN,new SimpleFunctionParser("emi_loot.function.banner"),"Registering vanilla set banner function parser");
        registerFunction(LootFunctionTypes.SET_LORE,new SimpleFunctionParser("emi_loot.function.lore"),"Registering vanilla set lore function parser");
        registerFunction(LootFunctionTypes.SET_STEW_EFFECT,new SetStewFunctionParser(),"Registering vanilla set stew effect function parser");
        registerFunction(LootFunctionTypes.SET_NBT,new SimpleFunctionParser("emi_loot.function.set_nbt"),"Registering vanilla set nbt function parser");
        registerFunction(LootFunctionTypes.SET_LOOT_TABLE,new SetLootTableFunctionParser(),"Registering vanilla set loot table function parser");
        registerFunction(EMILoot.OMINOUS_BANNER,new OminousBannerFunctionParser(),"Registering Lootify ominous banner function parser");
        registerFunction(EMILoot.SET_ANY_DAMAGE,new SetAnyDamageFunctionParser(),"Registering Lootify set-any-damage function parser");

        registerCondition(LootConditionTypes.SURVIVES_EXPLOSION, new SurvivesExplosionConditionParser(),"Registering vanilla survives-explosion condition parser");
        registerCondition(LootConditionTypes.BLOCK_STATE_PROPERTY, new BlockStatePropertyConditionParser(),"Registering vanilla block state property condition parser");
        registerCondition(LootConditionTypes.TABLE_BONUS, new TableBonusConditionParser(),"Registering vanilla table bonus condition parser");
        registerCondition(LootConditionTypes.INVERTED, new InvertedConditionParser(),"Registering vanilla inverted condition parser");
        registerCondition(LootConditionTypes.ALTERNATIVE, new AlternativesConditionParser(),"Registering vanilla alternatives condition parser");
        registerCondition(LootConditionTypes.KILLED_BY_PLAYER, new SimpleConditionParser("emi_loot.condition.killed_player"),"Registering vanilla killed-by-player condition parser");
        registerCondition(LootConditionTypes.RANDOM_CHANCE, new RandomChanceConditionParser(),"Registering vanilla random chance condition parser");
        registerCondition(LootConditionTypes.RANDOM_CHANCE_WITH_LOOTING, new RandomChanceWithLootingConditionParser(),"Registering vanilla random chance with looting condition parser");
        registerCondition(LootConditionTypes.DAMAGE_SOURCE_PROPERTIES, new DamageSourceConditionParser(),"Registering vanilla damage source properties condition parser");
        registerCondition(LootConditionTypes.LOCATION_CHECK, new LocationCheckConditionParser(),"Registering vanilla location check condition parser");
        registerCondition(LootConditionTypes.ENTITY_PROPERTIES, new EntityPropertiesConditionParser(),"Registering vanilla entity properties condition parser");
        registerCondition(LootConditionTypes.MATCH_TOOL, new MatchToolConditionParser(),"Registering vanilla match-tool condition parser");
        registerCondition(LootConditionTypes.ENTITY_SCORES, new SimpleConditionParser("emi_loot.condition.entity_scores"),"Registering vanilla entity scores condition parser");
        registerCondition(LootConditionTypes.REFERENCE, new ReferenceConditionParser(),"Registering vanilla reference condition parser");
        registerCondition(LootConditionTypes.TIME_CHECK, new TimeCheckConditionParser(),"Registering vanilla time check condition parser");
        registerCondition(LootConditionTypes.VALUE_CHECK, new ValueCheckConditionParser(),"Registering vanilla value check condition parser");
        registerCondition(LootConditionTypes.WEATHER_CHECK, new WeatherCheckConditionParser(),"Registering vanilla weather check condition parser");
        registerCondition(EMILoot.SPAWNS_WITH, new SimpleConditionParser("emi_loot.condition.spawns_with"),"Registering lootify spawns with condition parser");
        registerCondition(EMILoot.CREEPER, new SimpleConditionParser("emi_loot.condition.creeper"),"Registering lootify creeper condition parser");
        registerCondition(EMILoot.WITHER_KILL, new SimpleConditionParser("emi_loot.condition.wither_kill"),"Registering lootify wither-killed condition parser");
    }
}















