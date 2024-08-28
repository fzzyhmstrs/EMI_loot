package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.DamageSourcePredicateParser;
import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class DamageSourceConditionParser implements ConditionParser {

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        DamageSourcePredicate damageSourcePredicate = ((DamageSourcePropertiesLootCondition)condition).predicate().orElse(null); // TODO?
		if (damageSourcePredicate == null) {
			return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.damage_source", LText.literal("Unkknown"))));
		}
		Text damageText = DamageSourcePredicateParser.parseDamageSourcePredicate(damageSourcePredicate);
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.damage_source", damageText.getString())));
    }
}