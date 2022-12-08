package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.InvertedLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;

public class InvertedConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootCondition term = ((InvertedLootConditionAccessor)condition).getCondition();
        List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(term, stack);
        List<LootTableParser.LootConditionResult> finalResults = new LinkedList<>();
        termResults.forEach((result)->{
            Text resultText = result.getText().process(stack,null).text();
            finalResults.add(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.invert",resultText.getString())));
        });
        return finalResults;
    }
}
