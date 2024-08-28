package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class WeatherCheckConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        Optional<Boolean> raining = ((WeatherCheckLootCondition)condition).raining();
        if (raining.isPresent()) {
            if (raining.get()) {
                return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.raining_true")));
            }
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.raining_false")));
        }
        Optional<Boolean> thundering = ((WeatherCheckLootCondition)condition).thundering();
        if (thundering.isPresent()) {
            if (thundering.get()) {
                return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.thundering_true")));
            }
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.thundering_false")));
        }
        return Collections.singletonList(LootTableParser.LootConditionResult.EMPTY);
    }
}