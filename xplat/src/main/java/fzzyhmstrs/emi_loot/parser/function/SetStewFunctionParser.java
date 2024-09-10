package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetStewEffectLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.text.MutableText;

import java.util.LinkedList;
import java.util.List;

public class SetStewFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        List<SetStewEffectLootFunction.StewEffect> effects = ((SetStewEffectLootFunctionAccessor)function).getEffects();
        List<MutableText> list = new LinkedList<>();
        for (SetStewEffectLootFunction.StewEffect effect : effects) {
            list.add(effect.effect().value().getName().copy());
        }
        if (list.isEmpty()) {
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_stew", LText.translatable("emi_loot.function.set_stew_unknown").getString()), ItemStack.EMPTY, conditionTexts);
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_stew", ListProcessors.buildOrList(list).getString()), ItemStack.EMPTY, conditionTexts);
    }
}