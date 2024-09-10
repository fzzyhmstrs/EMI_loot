package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetLootTableLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryKey;

import java.util.List;

public class SetLootTableFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        RegistryKey<LootTable> id = ((SetLootTableLootFunctionAccessor)function).getLootTable();
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_loot_table", id.getValue().toString()), ItemStack.EMPTY, conditionTexts);
    }
}