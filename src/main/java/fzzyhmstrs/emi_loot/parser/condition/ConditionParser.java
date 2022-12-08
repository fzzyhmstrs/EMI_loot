package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;

import java.util.Collections;
import java.util.List;

public interface ConditionParser{
    ConditionParser EMPTY = (condition, stack, parentIsAlternative) -> Collections.singletonList(LootTableParser.LootConditionResult.EMPTY);
    
    List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative);
}
