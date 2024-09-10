package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.FilteredLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.ItemPredicateParser;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.predicate.item.ItemPredicate;

import java.util.List;

public class FilteredFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        ItemPredicate p = ((FilteredLootFunctionAccessor)function).getItemFilter();
        LootFunction f = ((FilteredLootFunctionAccessor)function).getModifier();
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.filtered", ItemPredicateParser.parseItemPredicate(p).getString(), LootParserRegistry.parseFunction(f, stack, f.getType(), parentIsAlternative, List.of()).text().asText().getString()), ItemStack.EMPTY, conditionTexts);
    }
}