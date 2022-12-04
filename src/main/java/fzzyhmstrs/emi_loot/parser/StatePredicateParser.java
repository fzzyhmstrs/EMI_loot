package fzzyhmstrs.emi_loot.parser;


import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.text.Text;

public class StatePredicateParser {
  
    public static Text parseStatePredicate(StatePredicate predicate){
        if (EMILoot.DEBUG) EMILoot.LOGGER.warning("State predicates not currently implemented.... Affects table: "  + LootTableParser.currentTable);
        return LText.empty();
    }
  
}
