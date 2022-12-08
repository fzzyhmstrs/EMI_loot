package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.RandomChanceWithLootingLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RandomChanceWithLootingConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        float chance = ((RandomChanceWithLootingLootConditionAccessor)condition).getChance();
        float multiplier = ((RandomChanceWithLootingLootConditionAccessor)condition).getLootingMultiplier();
        List<String> args = new LinkedList<>(Arrays.stream(new String[]{Float.toString((chance*100)), Float.toString((multiplier*100))}).toList());
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.chance_looting", args)));
    }
}
