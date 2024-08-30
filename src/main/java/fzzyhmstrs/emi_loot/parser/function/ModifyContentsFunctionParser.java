package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ModifyContentsLootFunctionAccessor;
import fzzyhmstrs.emi_loot.mixins.SetDamageLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ModifyContentsFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        LootFunction f = ((ModifyContentsLootFunctionAccessor)function).getModifier();
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.modify_contents", LootParserRegistry.parseFunction(f, stack, f.getType(), parentIsAlternative, List.of()).text().asText().getString()), ItemStack.EMPTY, conditionTexts);
    }
}