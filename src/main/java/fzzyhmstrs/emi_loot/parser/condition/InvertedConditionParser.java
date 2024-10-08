package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InvertedConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        LootCondition term = ((InvertedLootCondition)condition).term();
        List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(term, stack);
        List<LootTableParser.LootConditionResult> finalResults = new ArrayList<>();
        termResults.forEach((result)-> {
            Text resultText = result.getText().process(stack, null).text();
            finalResults.add(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.invert", resultText.getString())));
        });
        return finalResults;
    }
}