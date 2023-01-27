package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetStewEffectLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.text.MutableText;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SetStewFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Map<StatusEffect, LootNumberProvider> effects = ((SetStewEffectLootFunctionAccessor)function).getEffects();
        List<MutableText> list = new LinkedList<>();
        for (StatusEffect effect : effects.keySet()){
            list.add(effect.getName().copy());
        }
        if (list.isEmpty()){
            return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_stew", LText.translatable("emi_loot.function.set_stew_unknown").getString()), ItemStack.EMPTY, conditionTexts);
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_stew", ListProcessors.buildOrList(list).getString()), ItemStack.EMPTY, conditionTexts);
    }
}
