package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetOminousBottleAmplifierLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.text.Text;

import java.util.List;

public class SetOminousBottleFunctionParser implements FunctionParser {

	@Override
	public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
		LootNumberProvider amplifier = ((SetOminousBottleAmplifierLootFunctionAccessor)function).getAmplifier();
		Text a = NumberProcessors.processLootNumberProvider(amplifier);
		return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.ominous_bottle", a.getString()), ItemStack.EMPTY, conditionTexts);
	}
}