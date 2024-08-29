package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetNameLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class SetNameFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function,ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts) {
        Optional<Text> text = ((SetNameLootFunctionAccessor)function).getName(); // TODO?
		if (text.isPresent()) {
			stack.setCustomName(text.get());
		}
        return new LootTableParser.LootFunctionResult(TextKey.empty(), stack, conditionTexts);
    }
}