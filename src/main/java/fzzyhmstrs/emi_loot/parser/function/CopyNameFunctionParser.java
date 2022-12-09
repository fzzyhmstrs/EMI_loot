package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.CopyNameLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.text.Text;

import java.util.List;

public class CopyNameFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        CopyNameLootFunction.Source source = ((CopyNameLootFunctionAccessor)function).getSource();
        Text sourceText = LText.translatable("emi_loot.function.copy_name." + source.name);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.copy_name",sourceText.getString()),ItemStack.EMPTY,conditionTexts);
    }
}
