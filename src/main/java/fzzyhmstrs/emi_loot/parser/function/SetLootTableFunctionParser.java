package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetLootTableLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;

import java.util.List;

public class SetLootTableFunctionParser implements FunctionParser{

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack,boolean parentIsAlternative, List<TextKey> conditionTexts){
        Identifier id = ((SetLootTableLootFunctionAccessor)function).getId();
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_loot_table", id.toString()), ItemStack.EMPTY, conditionTexts);
    }
}
