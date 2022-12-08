package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetCountLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;

import java.util.List;

public class SetCountFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        LootNumberProvider provider = ((SetCountLootFunctionAccessor)function).getCountRange();
        float rollAvg = NumberProcessors.getRollAvg(provider);
        boolean add = ((SetCountLootFunctionAccessor)function).getAdd();
        if (add){
            stack.setCount(Math.max(stack.getCount() + (int)rollAvg,1));
        } else {
            stack.setCount(Math.max((int)rollAvg,1));
        }
        if (add){
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_count_add"),ItemStack.EMPTY,conditionTexts);
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_count_set"),ItemStack.EMPTY,conditionTexts);
    }
}
