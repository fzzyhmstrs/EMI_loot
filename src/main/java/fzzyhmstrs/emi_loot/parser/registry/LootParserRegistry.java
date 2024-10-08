package fzzyhmstrs.emi_loot.parser.registry;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.condition.BlockStatePropertyConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.ConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.DamageSourceConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.EntityPropertiesConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.InvertedConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.LocationCheckConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.MatchToolConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.MultiplesConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.RandomChanceConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.RandomChanceWithLootingConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.ReferenceConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.SimpleConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.SurvivesExplosionConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.TableBonusConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.TimeCheckConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.ValueCheckConditionParser;
import fzzyhmstrs.emi_loot.parser.condition.WeatherCheckConditionParser;
import fzzyhmstrs.emi_loot.parser.function.ApplyBonusFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.CopyNameFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.EnchantRandomlyFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.EnchantWithLevelsFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.ExplorationMapFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.ExplosionDecayFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.FilteredFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.FunctionParser;
import fzzyhmstrs.emi_loot.parser.function.LimitCountFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.ModifyContentsFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.ReferenceFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SequenceFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetAnyDamageFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetAttributesFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetBookCoverFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetCountFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetDamageFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetEnchantmentsFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetFireworkExplosionFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetFireworksFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetInstrumentFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetItemFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetLootTableFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetNameFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetOminousBottleFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetPotionFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SetStewFunctionParser;
import fzzyhmstrs.emi_loot.parser.function.SimpleFunctionParser;
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

public class LootParserRegistry {

    private static final Map<LootConditionType, ConditionParser> CONDITION_PARSERS = new HashMap<>();
    private static final Map<LootFunctionType, FunctionParser> FUNCTION_PARSERS = new HashMap<>();

    public static void registerCondition(LootConditionType type, ConditionParser parser, String registrationContext) {
        if (!CONDITION_PARSERS.containsKey(type)) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Registering condition for type: " + type + " from: " + registrationContext);
            CONDITION_PARSERS.put(type, parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate condition registration attempted with type: " + type + " during " + registrationContext);
        }
    }

    public static void registerFunction(LootFunctionType type, FunctionParser parser, String registrationContext) {
        if (!FUNCTION_PARSERS.containsKey(type)) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.info("Registering function for type: " + type + " from: " + registrationContext);
            FUNCTION_PARSERS.put(type, parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate function registration attempted with type: " + type + " during " + registrationContext);
        }
    }

    public static List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, LootConditionType type, ItemStack stack, boolean parentIsAlternative) {
        ConditionParser parser = CONDITION_PARSERS.getOrDefault(type, ConditionParser.EMPTY);
        return parser.parseCondition(condition, stack, parentIsAlternative);
    }

    public static LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, LootFunctionType type, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        FunctionParser parser = FUNCTION_PARSERS.getOrDefault(type, FunctionParser.EMPTY);
        return parser.parseFunction(function, stack, parentIsAlternative, conditionTexts);
    }

    static {

        registerFunction(LootFunctionTypes.APPLY_BONUS, new ApplyBonusFunctionParser(), "Registering vanilla apply bonus function parser");
        registerFunction(LootFunctionTypes.SET_POTION, new SetPotionFunctionParser(), "Registering vanilla set potion function parser");
        registerFunction(LootFunctionTypes.SET_COUNT, new SetCountFunctionParser(), "Registering vanilla set count function parser");
        registerFunction(LootFunctionTypes.SET_ITEM, new SetItemFunctionParser(), "Registering vanilla set item function parser");
        registerFunction(LootFunctionTypes.ENCHANT_WITH_LEVELS, new EnchantWithLevelsFunctionParser(), "Registering vanilla enchant with levels function parser");
        registerFunction(LootFunctionTypes.ENCHANT_RANDOMLY, new EnchantRandomlyFunctionParser(), "Registering vanilla enchant randomly function parser");
        registerFunction(LootFunctionTypes.SET_ENCHANTMENTS, new SetEnchantmentsFunctionParser(), "Registering vanilla set enchantments function parser");
        registerFunction(LootFunctionTypes.SET_CUSTOM_DATA, new SimpleFunctionParser("emi_loot.function.set_nbt"), "Registering vanilla set nbt function parser");
        registerFunction(LootFunctionTypes.SET_COMPONENTS, new SimpleFunctionParser("emi_loot.function.set_nbt"), "Registering vanilla set components function parser");
        registerFunction(LootFunctionTypes.FURNACE_SMELT, new SimpleFunctionParser("emi_loot.function.smelt"), "Registering vanilla furnace smelt function parser");
        registerFunction(LootFunctionTypes.ENCHANTED_COUNT_INCREASE, new SimpleFunctionParser("emi_loot.function.looting"), "Registering vanilla looting function parser");
        registerFunction(LootFunctionTypes.EXPLORATION_MAP, new ExplorationMapFunctionParser(), "Registering vanilla exploration map function parser");
        registerFunction(LootFunctionTypes.SET_NAME, new SetNameFunctionParser(), "Registering vanilla set name function parser");
        registerFunction(LootFunctionTypes.SET_CONTENTS, new SimpleFunctionParser("emi_loot.function.set_contents"), "Registering vanilla set contents function parser");
        registerFunction(LootFunctionTypes.MODIFY_CONTENTS, new ModifyContentsFunctionParser(), "Registering vanilla modify contents function parser");
        registerFunction(LootFunctionTypes.FILTERED, new FilteredFunctionParser(), "Registering vanilla filtered function parser");
        registerFunction(LootFunctionTypes.SET_DAMAGE, new SetDamageFunctionParser(), "Registering vanilla set damage function parser");
        registerFunction(LootFunctionTypes.SET_INSTRUMENT, new SetInstrumentFunctionParser(), "Registering vanilla set instrument function parser");
        registerFunction(LootFunctionTypes.COPY_STATE, new SimpleFunctionParser("emi_loot.function.copy_state"), "Registering vanilla copy state function parser");
        registerFunction(LootFunctionTypes.COPY_NAME, new CopyNameFunctionParser(), "Registering vanilla copy name function parser");
        registerFunction(LootFunctionTypes.COPY_CUSTOM_DATA, new SimpleFunctionParser("emi_loot.function.copy_nbt"), "Registering vanilla copy nbt function parser");
        registerFunction(LootFunctionTypes.COPY_COMPONENTS, new SimpleFunctionParser("emi_loot.function.copy_nbt"), "Registering vanilla copy components function parser");
        registerFunction(LootFunctionTypes.EXPLOSION_DECAY, new ExplosionDecayFunctionParser(), "Registering vanilla explosion decay function parser");
        registerFunction(LootFunctionTypes.FILL_PLAYER_HEAD, new SimpleFunctionParser("emi_loot.function.fill_player_head"), "Registering vanilla fill-player-head function parser");
        registerFunction(LootFunctionTypes.LIMIT_COUNT, new LimitCountFunctionParser(), "Registering vanilla limit-count function parser");
        registerFunction(LootFunctionTypes.SET_ATTRIBUTES, new SetAttributesFunctionParser(), "Registering vanilla set attributes function parser");
        registerFunction(LootFunctionTypes.SET_BANNER_PATTERN, new SimpleFunctionParser("emi_loot.function.banner"), "Registering vanilla set banner function parser");
        registerFunction(LootFunctionTypes.SET_LORE, new SimpleFunctionParser("emi_loot.function.lore"), "Registering vanilla set lore function parser");
        registerFunction(LootFunctionTypes.SET_STEW_EFFECT, new SetStewFunctionParser(), "Registering vanilla set stew effect function parser");
        registerFunction(LootFunctionTypes.SET_LOOT_TABLE, new SetLootTableFunctionParser(), "Registering vanilla set loot table function parser");
        registerFunction(LootFunctionTypes.REFERENCE, new ReferenceFunctionParser(), "Registering vanilla reference function parser");
        registerFunction(LootFunctionTypes.SEQUENCE, new SequenceFunctionParser(), "Registering vanilla sequence function parser");
        registerFunction(LootFunctionTypes.SET_FIREWORKS, new SetFireworksFunctionParser(), "Registering vanilla fireworks function parser");
        registerFunction(LootFunctionTypes.SET_FIREWORK_EXPLOSION, new SetFireworkExplosionFunctionParser(), "Registering vanilla firework explosion function parser");
        registerFunction(LootFunctionTypes.SET_BOOK_COVER, new SetBookCoverFunctionParser(), "Registering vanilla book cover function parser");
        registerFunction(LootFunctionTypes.SET_WRITTEN_BOOK_PAGES, new SimpleFunctionParser("emi_loot.function.set_pages"), "Registering vanilla set book pages function parser");
        registerFunction(LootFunctionTypes.SET_WRITABLE_BOOK_PAGES, new SimpleFunctionParser("emi_loot.function.set_pages"), "Registering vanilla set book pages function parser");
        registerFunction(LootFunctionTypes.TOGGLE_TOOLTIPS, new SimpleFunctionParser("emi_loot.function.toggle_tooltip"), "Registering vanilla toggle tooltips function parser");
        registerFunction(LootFunctionTypes.SET_OMINOUS_BOTTLE_AMPLIFIER, new SetOminousBottleFunctionParser(), "Registering vanilla ominous bottle amplifier function parser");
        registerFunction(LootFunctionTypes.SET_CUSTOM_MODEL_DATA, new SimpleFunctionParser("emi_loot.function.custom_model_data"), "Registering vanilla custom model data function parser");


        registerFunction(EMILoot.OMINOUS_BANNER, new SimpleFunctionParser("emi_loot.function.ominous_banner"), "Registering Lootify ominous banner function parser");
        registerFunction(EMILoot.SET_ANY_DAMAGE, new SetAnyDamageFunctionParser(), "Registering Lootify set-any-damage function parser");

        registerCondition(LootConditionTypes.ANY_OF, new MultiplesConditionParser("emi_loot.condition.any_of"), "Registering vanilla any-of condition parser");
        registerCondition(LootConditionTypes.ALL_OF, new MultiplesConditionParser("emi_loot.condition.all_of"), "Registering vanilla all-of condition parser");
        registerCondition(LootConditionTypes.SURVIVES_EXPLOSION, new SurvivesExplosionConditionParser(), "Registering vanilla survives-explosion condition parser");
        registerCondition(LootConditionTypes.BLOCK_STATE_PROPERTY, new BlockStatePropertyConditionParser(), "Registering vanilla block state property condition parser");
        registerCondition(LootConditionTypes.TABLE_BONUS, new TableBonusConditionParser(), "Registering vanilla table bonus condition parser");
        registerCondition(LootConditionTypes.INVERTED, new InvertedConditionParser(), "Registering vanilla inverted condition parser");
        registerCondition(LootConditionTypes.KILLED_BY_PLAYER, new SimpleConditionParser("emi_loot.condition.killed_player"), "Registering vanilla killed-by-player condition parser");
        registerCondition(LootConditionTypes.RANDOM_CHANCE, new RandomChanceConditionParser(), "Registering vanilla random chance condition parser");
        registerCondition(LootConditionTypes.RANDOM_CHANCE_WITH_ENCHANTED_BONUS, new RandomChanceWithLootingConditionParser(), "Registering vanilla random chance with looting condition parser");
        registerCondition(LootConditionTypes.DAMAGE_SOURCE_PROPERTIES, new DamageSourceConditionParser(), "Registering vanilla damage source properties condition parser");
        registerCondition(LootConditionTypes.LOCATION_CHECK, new LocationCheckConditionParser(), "Registering vanilla location check condition parser");
        registerCondition(LootConditionTypes.ENTITY_PROPERTIES, new EntityPropertiesConditionParser(), "Registering vanilla entity properties condition parser");
        registerCondition(LootConditionTypes.MATCH_TOOL, new MatchToolConditionParser(), "Registering vanilla match-tool condition parser");
        registerCondition(LootConditionTypes.ENTITY_SCORES, new SimpleConditionParser("emi_loot.condition.entity_scores"), "Registering vanilla entity scores condition parser");
        registerCondition(LootConditionTypes.REFERENCE, new ReferenceConditionParser(), "Registering vanilla reference condition parser");
        registerCondition(LootConditionTypes.TIME_CHECK, new TimeCheckConditionParser(), "Registering vanilla time check condition parser");
        registerCondition(LootConditionTypes.VALUE_CHECK, new ValueCheckConditionParser(), "Registering vanilla value check condition parser");
        registerCondition(LootConditionTypes.WEATHER_CHECK, new WeatherCheckConditionParser(), "Registering vanilla weather check condition parser");
        registerCondition(EMILoot.SPAWNS_WITH, new SimpleConditionParser("emi_loot.condition.spawns_with"), "Registering lootify spawns with condition parser");
        registerCondition(EMILoot.CREEPER, new SimpleConditionParser("emi_loot.condition.creeper"), "Registering lootify creeper condition parser");
        registerCondition(EMILoot.WITHER_KILL, new SimpleConditionParser("emi_loot.condition.wither_kill"), "Registering lootify wither-killed condition parser");
    }
}