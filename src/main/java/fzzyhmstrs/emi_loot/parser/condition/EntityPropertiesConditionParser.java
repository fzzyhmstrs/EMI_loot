package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.EntityPropertiesLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.EntityPredicateParser;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.MutableText;

import java.util.Collections;
import java.util.List;

public class EntityPropertiesConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LootContext.EntityTarget entity = ((EntityPropertiesLootConditionAccessor)condition).getEntity();
        EntityPredicate predicate = ((EntityPropertiesLootConditionAccessor)condition).getPredicate();
        MutableText propText;
        if (entity == LootContext.EntityTarget.THIS){
            propText = LText.translatable("emi_loot.entity_predicate.entity_this", EntityPredicateParser.parseEntityPredicate(predicate));
        } else {
            propText = LText.translatable("emi_loot.entity_predicate.entity_killer", EntityPredicateParser.parseEntityPredicate(predicate));
        }
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.entity_props",propText.getString())));
    }
}
