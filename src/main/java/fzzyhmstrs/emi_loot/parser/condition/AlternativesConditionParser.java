package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.AlternativeLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AlternativesConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootCondition[] terms = ((AlternativeLootConditionAccessor)condition).getConditions();
        int size = terms.length;
        if (size == 1){
            List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(terms[0], stack);
            Text termText = LootTableParser.compileConditionTexts(stack,termResults);
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.alternates",termText.getString())));
        } else if (size == 2){
            List<LootTableParser.LootConditionResult> termResults1 = LootTableParser.parseLootCondition(terms[0], stack);
            List<LootTableParser.LootConditionResult> termResults2 = LootTableParser.parseLootCondition(terms[1], stack);
            Text termText1 = LootTableParser.compileConditionTexts(stack,termResults1);
            Text termText2;
            if (termResults2.size() == 1){
                TextKey key = termResults2.get(0).getText();
                if (key.args().size() == 1){
                    termText2 = Text.of(key.args().get(0));
                } else {
                    termText2 = LootTableParser.compileConditionTexts(stack,termResults2);
                }
            } else {
                termText2 = LootTableParser.compileConditionTexts(stack, termResults2);
            }
            List<String> args = new LinkedList<>(Arrays.stream(new String[]{termText1.getString(), termText2.getString()}).toList());
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.alternates_2",args)));
        } else {
            List<String> args = new LinkedList<>();
            Arrays.stream(terms).toList().forEach((term)-> {
                List<LootTableParser.LootConditionResult> termResults = LootTableParser.parseLootCondition(term, stack);
                Text termText = LootTableParser.compileConditionTexts(stack,termResults);
                args.add(termText.getString());
            });
            return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.alternates_3",args)));
        }
    }
}
