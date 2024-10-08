package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.AlternativeLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiplesConditionParser implements ConditionParser {

    public MultiplesConditionParser(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        List<LootCondition> terms = ((AlternativeLootConditionAccessor)condition).getConditions();
        List<String> args =terms.stream().map((term)-> {
            List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(term, stack);
            Text termText = LootTableParser.compileConditionTexts(stack, termResults);
            return termText.getString();
        }).toList();
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of(key, args)));

    }
}