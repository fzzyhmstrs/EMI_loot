package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RandomChanceWithLootingConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        float chance = ((RandomChanceWithLootingLootCondition)condition).chance();
        float multiplier = ((RandomChanceWithLootingLootCondition)condition).lootingMultiplier();
        List<Text> args = new LinkedList<>(Arrays.stream(new Text[]{LText.literal(Float.toString((chance*100))), LText.literal(Float.toString((multiplier*100)))}).toList());
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.chance_looting", args)));
    }
}