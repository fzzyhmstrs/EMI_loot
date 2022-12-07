package fzzyhmstrs.emi_loot.parser.condition;

package fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.condition.LootCondition;

import java.util.List;

public interface ConditionParser{
    public static ConditionParser EMPTY = new ConditionParser(){
        @Override
        public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition){
            return Collections.singletonList(LootTableParser.LootConditionResult.EMPTY);
        }
    }
    
    List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition);
}
