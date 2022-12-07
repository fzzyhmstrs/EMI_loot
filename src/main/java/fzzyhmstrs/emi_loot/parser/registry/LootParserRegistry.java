package fzzyhmstrs.emi_loot.parser.registry;

import fzzyhmstrs.emi_loot.EMILoot;
package fzzyhmstrs.emi_loot.parser.LootTableParser;
package fzzyhmstrs.emi_loot.parser.condition.ConditionParser;
package fzzyhmstrs.emi_loot.parser.function.FunctionParser;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;


import java.util.*;

public class LootParserRegistry{

    private static Map<LootConditionType, ConditionParser> CONDITION_PARSERS = new HashMap<>();
    private static Map<LootFunctionType, FunctionParser> FUNCTION_PARSERS = new HashMap<>();

    public static void registerCondition(LootConditionType type, ConditionParser parser, String registrationContext){
        if (!CONDITION_PARSERS.containsKey(type)){
            CONDITION_PARSERS.put(type,parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate condition registration attempted with type: " + type + " during " + registrationContext);
        }
    }
    
    public static void registerFunction(LootFunctionType type, FunctionParser parser, String registrationContext){
        if (!FUNCTION_PARSERS.containsKey(type)){
            FUNCTION_PARSERS.put(type,parser);
        } else {
            EMILoot.LOGGER.warn("Duplicate function registration attempted with type: " + type + " during " + registrationContext);
        }
    }
    
    public static List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, LootConditionType type){
        ConditionParser parser = CONDITION_PARSERS.getOrDefault(type,ConditionParser.EMPTY);
        return parser.parseCondition(condition);
    }
    
    public static LootTableParser.LootFunctionResult parseFunction(LootFunction function, LootFunctionType type, List<TextKey> conditionTexts){
        FunctionParser parser = FUNCTION_PARSERS.getOrDefault(type,FunctionParser.EMPTY);
        return parser.parseFunction(function, conditionTexts);
    }
}
