package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.AndLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class SequenceFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        List<LootFunction> list = ((AndLootFunctionAccessor)function).getTerms();
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.sequence", ListProcessors.buildAndList(list.stream().map(lf -> LootParserRegistry.parseFunction(lf, stack, lf.getType(), parentIsAlternative, List.of()).text().asText().copyContentOnly()).toList()).getString()), stack, conditionTexts);
    }
}