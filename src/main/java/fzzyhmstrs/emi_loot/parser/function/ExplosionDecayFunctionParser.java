package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class ExplosionDecayFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        if (parentIsAlternative) return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.decay"), ItemStack.EMPTY, conditionTexts);
        return new LootTableParser.LootFunctionResult(TextKey.empty(), ItemStack.EMPTY, conditionTexts);
    }
}
