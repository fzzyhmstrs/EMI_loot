package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetComponentsLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;

import java.util.List;

public class SetNbtFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        ComponentChanges changes = ((SetComponentsLootFunctionAccessor)function).getChanges();
        stack.applyChanges(changes);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_nbt"), ItemStack.EMPTY, conditionTexts);
    }
}