package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.text.Text;

public class DistancePredicateParser {

    public static Text parseDistancePredicate(DistancePredicate predicate){
        NumberRange.DoubleRange abs = predicate.absolute();
        if (!abs.equals(NumberRange.DoubleRange.ANY)) {
            return LText.translatable("emi_loot.entity_predicate.distance_abs",abs.min().orElse(null),abs.max().orElse(null));
        }
        NumberRange.DoubleRange hor = predicate.horizontal();
        if (!hor.equals(NumberRange.DoubleRange.ANY)) {
            return LText.translatable("emi_loot.entity_predicate.distance_hor",hor.min().orElse(null),hor.max().orElse(null));
        }
        NumberRange.DoubleRange x = predicate.x();
        if (!x.equals(NumberRange.DoubleRange.ANY)) {
            return LText.translatable("emi_loot.entity_predicate.distance_x",x.min().orElse(null),x.max().orElse(null));
        }
        NumberRange.DoubleRange y = predicate.y();
        if (!y.equals(NumberRange.DoubleRange.ANY)) {
            return LText.translatable("emi_loot.entity_predicate.distance_y",y.min().orElse(null),y.max().orElse(null));
        }
        NumberRange.DoubleRange z = predicate.z();
        if (!z.equals(NumberRange.DoubleRange.ANY)) {
            return LText.translatable("emi_loot.entity_predicate.distance_z",z.min().orElse(null),z.max().orElse(null));
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Unparsable distance predicate in table: " + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}