package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.SetCountLootFunctionAccessor;
import fzzyhmstrs.emi_loot.mixins.SetItemLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class SetItemFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        RegistryEntry<Item> item = ((SetItemLootFunctionAccessor)function).getItem();
        stack = stack.copyComponentsToNewStack(item.value(), stack.getCount());
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.set_item", item.value().getName().getString()), stack, conditionTexts);
    }
}