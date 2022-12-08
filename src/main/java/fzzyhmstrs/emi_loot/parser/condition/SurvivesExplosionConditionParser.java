package fzzyhmstrs.emi_loot.parser.condition;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;

import java.util.Collections;
import java.util.List;

public class SurvivesExplosionConditionParser implements ConditionParser{

    @Override
    public List<LootTableParser.LootConditionResult> parseCondition(LootCondition condition, ItemStack stack, boolean parentIsAlternative){
        if (parentIsAlternative) return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.of("emi_loot.condition.survives_explosion")));
        return Collections.singletonList(new LootTableParser.LootConditionResult(TextKey.empty()));
    }
}
