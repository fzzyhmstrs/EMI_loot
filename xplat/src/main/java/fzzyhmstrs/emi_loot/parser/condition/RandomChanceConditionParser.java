package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.provider.number.LootNumberProvider;

import java.util.Collections;
import java.util.List;

public class RandomChanceConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        LootNumberProvider chance = ((RandomChanceLootCondition)condition).chance();
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.chance", NumberProcessors.processLootNumberProvider(chance).getString())));
    }
}