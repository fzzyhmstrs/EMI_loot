package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.ValueCheckLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;

import java.util.Collections;
import java.util.List;

public class ValueCheckConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootNumberProvider value = ((ValueCheckLootConditionAccessor)condition).getValue();
        String processedValue = NumberProcessors.processLootNumberProvider(value).getString();
        BoundedIntUnaryOperator range = ((ValueCheckLootConditionAccessor)condition).getRange();
        String processedRange = NumberProcessors.processBoundedIntUnaryOperator(range).getString();
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.value_check",processedValue,processedRange)));
    }
}
