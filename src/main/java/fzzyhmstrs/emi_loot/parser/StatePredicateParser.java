package fzzyhmstrs.emi_loot.parser;


import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.StatePredicateAccessor;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.StatePredicate.Condition;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;

public class StatePredicateParser {
  
    public static Text parseStatePredicate(StatePredicate predicate){
        List<Condition> list = ((StatePredicateAccessor)predicate).getConditions();
        if (!list.isEmpty()){
            List<MutableText> list2 = new LinkedList<>();
            for (Condition condition : list){
                if (condition instanceof StatePredicate.RangedValueCondition){
                    String key = condition.key;
                    String min = ((StatePredicate.RangedValueCondition) condition).min;
                    String max = ((StatePredicate.RangedValueCondition) condition).max;
                    list2.add(LText.translatable("emi_loot.state_predicate.state_between",key,min,max));
                } else if (condition instanceof StatePredicate.ExactValueCondition){
                    String key = condition.key;
                    String value = ((StatePredicate.ExactValueCondition) condition).value;
                    list2.add(LText.translatable("emi_loot.state_predicate.state_exact",key,value));
                }
            }
            return LText.translatable("emi_loot.state_predicate.base", ListProcessors.buildAndList(list2));
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty or unparsable block/fluid state predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
  
}
