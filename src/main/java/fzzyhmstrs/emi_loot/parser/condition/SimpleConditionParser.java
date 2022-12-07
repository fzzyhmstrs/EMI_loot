package fzzyhmstrs.emi_loot.parser.condition;

package fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.loot.condition.LootCondition;

import java.util.List;

public class SimpleConditionParser{
    
    private final TextKey key;
    
    public SimpleConditionParser(String key){
        this.key = TextKey.of(key);
    }
    
    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition){
        return Collections.singletonList(new LootConditionResult(key));
    
    }
}
