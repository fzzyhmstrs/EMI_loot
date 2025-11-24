package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetNbtLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class SetNbtFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        NbtCompound nbtCompound = ((SetNbtLootFunctionAccessor)function).getNbt();
        stack.getOrCreateNbt().copyFrom(nbtCompound);
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_nbt"), ItemStack.EMPTY, conditionTexts);
    }
}