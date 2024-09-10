package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetFireworksLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetFireworksLootFunction;

import java.util.List;

public class SetWrittenPagesFunctionParser implements FunctionParser {

	@Override
	public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
		if (!stack.isEmpty()) {
			stack.apply(DataComponentTypes.FIREWORKS, SetFireworksLootFunction.DEFAULT_FIREWORKS, ((SetFireworksLootFunctionAccessor)function)::callApply);
		}
		return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.fireworks"), ItemStack.EMPTY, conditionTexts);
	}
}