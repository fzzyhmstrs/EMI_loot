package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.LimitCountLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.text.Text;

import java.util.List;

public class LimitCountFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        BoundedIntUnaryOperator operator = ((LimitCountLootFunctionAccessor)function).getLimit();
        Text limit = NumberProcessors.processBoundedIntUnaryOperator(operator);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.limit_count", limit.getString()),ItemStack.EMPTY,conditionTexts);
    }
}
