package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public interface FunctionParser{
    FunctionParser EMPTY = (function,stack, conditionTexts, parentIsAlternative) -> LootTableParser.LootFunctionResult.EMPTY;
    
    LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts);

    default ItemStack parseStack(ItemStack stack){
        return stack;
    }
}
