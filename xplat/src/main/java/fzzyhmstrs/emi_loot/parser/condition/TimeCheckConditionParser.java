package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.TimeCheckLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class TimeCheckConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        Long period = ((TimeCheckLootConditionAccessor)condition).getPeriod();
        BoundedIntUnaryOperator value = ((TimeCheckLootConditionAccessor)condition).getValue();
        Text processedValue = NumberProcessors.processBoundedIntUnaryOperator(value);
        if (period != null) {
            return Collections.singletonList(
                    new LootTableParser.LootConditionResult(TextKey.of(
                            "emi_loot.condition.time_check_period",
                            LText.literal(String.valueOf(period)),
                            processedValue
                    )
                    )
            );
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.time_check", processedValue)));
    }
}