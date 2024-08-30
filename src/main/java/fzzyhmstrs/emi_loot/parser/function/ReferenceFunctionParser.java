package fzzyhmstrs.emi_loot.parser.function;

import fzzyhmstrs.emi_loot.mixins.ReferenceLootFunctionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.parser.registry.LootParserRegistry;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class ReferenceFunctionParser implements FunctionParser {

    @Override
    public LootTableParser.LootFunctionResult parseFunction(LootFunction function, ItemStack stack, boolean parentIsAlternative, List<TextKey> conditionTexts) {
        RegistryKey<LootFunction> id = ((ReferenceLootFunctionAccessor)function).getName();
        if (LootTableParser.lootManager != null) {
            Optional<LootFunction> referenceFunction = LootTableParser.registryManager.getOptional(RegistryKeys.ITEM_MODIFIER).map(reg -> reg.get(id));
            if (referenceFunction.isPresent() && referenceFunction.get().getType() != LootFunctionTypes.REFERENCE) {
                return LootParserRegistry.parseFunction(referenceFunction.get(), stack, referenceFunction.get().getType(), parentIsAlternative, conditionTexts);
            }
        }
        return new LootTableParser.LootFunctionResult(TextKey.of("emi_loot.function.reference", id.toString()), ItemStack.EMPTY, conditionTexts);
    }
}