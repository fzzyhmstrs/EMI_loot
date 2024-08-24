package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;

import java.util.Collections;
import java.util.List;

public class SimpleConditionParser implements ConditionParser {
    
    private final TextKey key;
    
    public SimpleConditionParser(String key) {
        this.key = TextKey.of(key);
    }
    
    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        return Collections.singletonList(new LootTableParser.LootConditionResult(key));
    }
}