package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.mixins.LocationCheckLootConditionAccessor;
import fzzyhmstrs.emi_loot.parser.LocationPredicateParser;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class LocationCheckConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        LocationPredicate predicate = ((LocationCheckLootConditionAccessor)condition).getPredicate();
        Text locText = LocationPredicateParser.parseLocationPredicate(predicate);
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.location", locText.getString())));
    }
}
