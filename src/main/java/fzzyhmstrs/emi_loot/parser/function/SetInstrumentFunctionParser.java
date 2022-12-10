package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.mixins.SetInstrumentLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public class SetInstrumentFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        TagKey<Instrument> tag = ((SetInstrumentLootFunctionAccessor)function).getInstrumentTag();
        GoatHornItem.setRandomInstrumentFromTag(stack,tag, EMILoot.emiLootRandom);
        return new LootTableParser.LootFunctionResult(TextKey.empty(), stack, conditionTexts);
    }
}
