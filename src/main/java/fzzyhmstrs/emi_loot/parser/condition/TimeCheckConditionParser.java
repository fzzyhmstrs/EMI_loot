package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TimeCheckConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        Optional<Long> period = ((TimeCheckLootCondition)condition).period(); // TODO?
        BoundedIntUnaryOperator value = ((TimeCheckLootCondition)condition).value();
        String processedValue = NumberProcessors.processBoundedIntUnaryOperator(value).getString();
        if (period.isPresent()){
            return Collections.singletonList(
                    new LootTableParser.LootConditionResult(TextKey.of(
                            "emi_loot.condition.time_check_period",
                            period.get().toString(),
                            processedValue
                    )
                    )
            );
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.time_check",processedValue)));
    }
}
