package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.text.Text;

import java.util.Objects;

public class LightPredicateParser {

    public static Text parseLightPredicate(LightPredicate predicate) {
        NumberRange.IntRange range = predicate.range();
        if (range.equals(NumberRange.IntRange.ANY)) {
            if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Undefined light predicate in table: "  + LootTableParser.currentTable);
            return LText.translatable("emi_loot.predicate.invalid");
        }
        Integer min = range.min().orElse(null);
        Integer max = range.max().orElse(null);
        if (Objects.equals(min, max) && min != null) {
            return LText.translatable("emi_loot.location_predicate.light", min);
        } else {
            return LText.translatable("emi_loot.location_predicate.light_2", min, max);
        }
    }
}