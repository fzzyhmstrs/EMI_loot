package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.RandomChanceWithLootingLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RandomChanceWithLootingConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        float chance = ((RandomChanceWithLootingLootConditionAccessor)condition).getChance();
        float multiplier = ((RandomChanceWithLootingLootConditionAccessor)condition).getLootingMultiplier();
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.chance_looting", LText.literal(Float.toString((chance*100))), LText.literal(Float.toString((multiplier*100))))));
    }
}