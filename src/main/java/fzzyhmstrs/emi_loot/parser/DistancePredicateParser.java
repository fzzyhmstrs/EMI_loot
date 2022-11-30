package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.*;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;

public class DistancePredicateParser{

    public static Text parseDistancePredicate(DistancePredicate predicate){
        NumberRange.FloatRange abs = ((DistancePredicateAccessor)predicate).getAbsolute();
        if (!abs.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.entity_predicate.distance_abs",abs.getMin(),abs.getMax());
        }
        NumberRange.FloatRange hor = ((DistancePredicateAccessor)predicate).getHorizontal();
        if (!hor.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.entity_predicate.distance_hor",hor.getMin(),hor.getMax());
        }
        NumberRange.FloatRange x = ((DistancePredicateAccessor)predicate).getX();
        if (!x.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.entity_predicate.distance_x",x.getMin(),x.getMax());
        }
        NumberRange.FloatRange y = ((DistancePredicateAccessor)predicate).getY();
        if (!y.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.entity_predicate.distance_y",y.getMin(),y.getMax());
        }
        NumberRange.FloatRange z = ((DistancePredicateAccessor)predicate).getZ();
        if (!z.equals(NumberRange.FloatRange.ANY)){
            return LText.translatable("emi_loot.entity_predicate.distance_z",z.getMin(),z.getMax());
        }
        return LText.empty();
    }

}
