package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetComponentsLootFunctionAccessor;
import fzzyhmstrs.emi_loot.mixins.SetCustomDataLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCustomDataLootFunction;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class SetCustomDataFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        NbtCompound compound = ((SetCustomDataLootFunctionAccessor)function).getNbt();
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> nbt.copyFrom(compound));
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_nbt"), ItemStack.EMPTY, conditionTexts);
    }
}