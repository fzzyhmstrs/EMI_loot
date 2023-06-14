package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.ReferenceLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class ReferenceConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        Identifier id = ((ReferenceLootConditionAccessor)condition).getId();
        if (LootTableParser.lootManager != null){
            LootCondition referenceCondition = (LootCondition) LootTableParser.lootManager.getElement(new LootDataKey(LootDataType.PREDICATES, id));
            if (referenceCondition != null && referenceCondition.getType() != LootConditionTypes.REFERENCE){
                return LootTableParser.parseLootCondition(referenceCondition,stack,parentIsAlternative);
            }
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.reference",id.toString())));
    }
}
