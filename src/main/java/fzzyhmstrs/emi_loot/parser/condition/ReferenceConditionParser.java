package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReferenceConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        RegistryKey<LootCondition> id = ((ReferenceLootCondition)condition).id();
        if (LootTableParser.registryManager != null) {
            Optional<LootCondition> referenceCondition = LootTableParser.registryManager.getOptional(RegistryKeys.PREDICATE).map((reg) -> reg.get(id));
            if (referenceCondition.isPresent() && referenceCondition.get().getType() != LootConditionTypes.REFERENCE) {
                return LootTableParser.parseLootCondition(referenceCondition.get(), stack, parentIsAlternative);
            }
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.reference", id.toString())));
    }
}