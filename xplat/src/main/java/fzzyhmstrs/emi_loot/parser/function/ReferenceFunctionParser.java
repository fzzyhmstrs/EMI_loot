package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ReferenceLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;

import java.util.List;

public class ReferenceFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        Identifier id = ((ReferenceLootFunctionAccessor)function).getName();
        if (LootTableParser.lootManager != null) {
            LootFunction referenceFunction = LootTableParser.lootManager.getElement(new LootDataKey<>(LootDataType.ITEM_MODIFIERS, id));
            if (referenceFunction != null && referenceFunction.getType() != LootFunctionTypes.REFERENCE) {
                return LootParserRegistry.parseFunction(referenceFunction, stack, referenceFunction.getType(), parentIsAlternative, conditionTexts);
            }
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.reference", id.toString()), stack, conditionTexts);
    }
}