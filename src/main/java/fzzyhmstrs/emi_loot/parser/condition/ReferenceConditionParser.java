package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReferenceConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative) {
        RegistryKey<LootCondition> id = ((ReferenceLootCondition)condition).id();
        if (LootTableParser.registryOps != null) {
            Optional<Optional<? extends RegistryEntry<LootCondition>>> referenceCondition = LootTableParser.registryOps.getEntryLookup(RegistryKeys.PREDICATE).map((reg) -> reg.getOptional(id));
            if (referenceCondition.isPresent() && referenceCondition.get().isPresent() && referenceCondition.get().get().value().getType() != LootConditionTypes.REFERENCE) {
                return LootTableParser.parseLootCondition(referenceCondition.get().get().value(), stack, parentIsAlternative);
            }
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.reference", id.toString())));
    }
}