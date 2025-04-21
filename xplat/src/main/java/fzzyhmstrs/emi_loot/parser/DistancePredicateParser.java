package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.text.Text;

public class DistancePredicateParser {

    public static Text parseDistancePredicate(DistancePredicate predicate) {
        NumberRange.DoubleRange abs = predicate.absolute();
        if (!abs.equals(NumberRange.DoubleRange.ANY)) {
            return NumberProcessors.processNumberRange(abs,
                    "emi_loot.entity_predicate.distance_abs.exact",
                    "emi_loot.entity_predicate.distance_abs",
                    "emi_loot.entity_predicate.distance_abs.at_least",
                    "emi_loot.entity_predicate.distance_abs.at_most",
                    "Unknown absolute distance");
        }
        NumberRange.DoubleRange hor = predicate.horizontal();
        if (!hor.equals(NumberRange.DoubleRange.ANY)) {
            return NumberProcessors.processNumberRange(hor,
                    "emi_loot.entity_predicate.distance_hor.exact",
                    "emi_loot.entity_predicate.distance_hor",
                    "emi_loot.entity_predicate.distance_hor.at_least",
                    "emi_loot.entity_predicate.distance_hor.at_most",
                    "Unknown horizontal distance");
        }
        NumberRange.DoubleRange x = predicate.x();
        if (!x.equals(NumberRange.DoubleRange.ANY)) {
            return NumberProcessors.processNumberRange(x,
                    "emi_loot.entity_predicate.distance_x.exact",
                    "emi_loot.entity_predicate.distance_x",
                    "emi_loot.entity_predicate.distance_x.at_least",
                    "emi_loot.entity_predicate.distance_x.at_most",
                    "Unknown X distance");
        }
        NumberRange.DoubleRange y = predicate.y();
        if (!y.equals(NumberRange.DoubleRange.ANY)) {
            return NumberProcessors.processNumberRange(y,
                    "emi_loot.entity_predicate.distance_y.exact",
                    "emi_loot.entity_predicate.distance_y",
                    "emi_loot.entity_predicate.distance_y.at_least",
                    "emi_loot.entity_predicate.distance_y.at_most",
                    "Unknown Y distance");
        }
        NumberRange.DoubleRange z = predicate.z();
        if (!z.equals(NumberRange.DoubleRange.ANY)) {
            return NumberProcessors.processNumberRange(z,
                    "emi_loot.entity_predicate.distance_z.exact",
                    "emi_loot.entity_predicate.distance_z",
                    "emi_loot.entity_predicate.distance_z.at_least",
                    "emi_loot.entity_predicate.distance_z.at_most",
                    "Unknown Z distance");
        }

        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Unparsable distance predicate in table: " + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}